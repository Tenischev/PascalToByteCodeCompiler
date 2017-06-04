package ru.ifmo.ctddev.tenishcev.compiler;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by kris13 on 08.05.15.
 */
public class PascalToByteCode implements SimplePascalListener {
    public ClassWriter classWriter;
    public String nameOfProgram;
    private Map<String, VariableContainer> variables;
    private Stack<Label> ifStack;
    private Stack<Label> whileStack;
    private MethodVisitor mainVisitor;

    public PascalToByteCode(){
        classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        variables = new HashMap<>();
        ifStack = new Stack<>();
        whileStack = new Stack<>();
    }

    private String getByteType(String text) {
        switch (text) {
            case "integer": return "I";
            case "longint": return "I";
        }
        return text;
    }

    private void parseArithmetic(SimplePascalParser.ExpressionArithmeticContext context) {
        context.expressionValueEmbeded().forEach(this::parseEmbeded);
        if (context.expressionValueEmbeded().size() > 1) {
            if (context.minus() != null) {
                mainVisitor.visitInsn(ISUB); // -
            } else if (context.plus() != null) {
                mainVisitor.visitInsn(IADD); // +
            } else if (context.modWord() != null) {
                mainVisitor.visitInsn(IREM); // %
            }
        }
    }

    private void parseEmbeded(SimplePascalParser.ExpressionValueEmbededContext context) {
        if (context.variable() != null) {
            VariableContainer variable = variables.get(context.variable().getText());
            mainVisitor.visitFieldInsn(GETSTATIC, nameOfProgram, variable.byteName, variable.byteType);
        } else {
            String number = context.number().getText();
            mainVisitor.visitLdcInsn(Integer.parseInt(number));
        }
    }

    @Override
    public void enterProgram(SimplePascalParser.ProgramContext ctx) {

    }

    @Override
    public void exitProgram(SimplePascalParser.ProgramContext ctx) {
        classWriter.visitEnd();
    }

    @Override
    public void enterNameOfProg(SimplePascalParser.NameOfProgContext ctx) {
        // Specify name of class as program name
        nameOfProgram = ctx.getChild(1).getText();
        classWriter.visit(V1_5, ACC_PUBLIC, nameOfProgram, null, getInternalName(Object.class), null);

        MethodVisitor constructor = classWriter.visitMethod(
                ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, getInternalName(Object.class), "<init>", "()V");
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();
    }

    @Override
    public void exitNameOfProg(SimplePascalParser.NameOfProgContext ctx) {

    }

    @Override
    public void enterVariables(SimplePascalParser.VariablesContext ctx) {
        String type = getByteType(ctx.type().getText());
        for (int i = 0; i < ctx.variable().size(); i++) {
            String fieldName = ctx.variable(i).getText();
            variables.put(fieldName, new VariableContainer(fieldName, fieldName, type));
            classWriter.visitField(ACC_PUBLIC + ACC_STATIC, fieldName, type, null, null).visitEnd();
        }
    }

    @Override
    public void exitVariables(SimplePascalParser.VariablesContext ctx) {

    }

    @Override
    public void enterBlock(SimplePascalParser.BlockContext ctx) {
        if (ctx.getParent() instanceof SimplePascalParser.ProgramContext) {
            mainVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        }
    }

    @Override
    public void exitBlock(SimplePascalParser.BlockContext ctx) {
        if (ctx.getParent() instanceof SimplePascalParser.ProgramContext) {
            mainVisitor.visitInsn(RETURN);
            mainVisitor.visitMaxs(8, 8);
            mainVisitor.visitEnd();
        }
    }

    @Override
    public void enterAssigment(SimplePascalParser.AssigmentContext ctx) {
        SimplePascalParser.ExpressionArithmeticContext context = ctx.expressionArithmetic();
        parseArithmetic(context);
        VariableContainer variable = variables.get(ctx.VAR().getText());
        mainVisitor.visitFieldInsn(PUTSTATIC, nameOfProgram, variable.byteName, variable.byteType);
    }

    @Override
    public void exitAssigment(SimplePascalParser.AssigmentContext ctx) {

    }

    @Override
    public void enterWhileLoop(SimplePascalParser.WhileLoopContext ctx) {
        SimplePascalParser.ExpressionCompareContext context = ctx.expressionLogic().expressionCompare();
        Label labelBefore = new Label();
        mainVisitor.visitLabel(labelBefore);
        context.expressionArithmetic().forEach(this::parseArithmetic);
        Label labelAfter = new Label();
        whileStack.push(labelAfter);
        whileStack.push(labelBefore);
        if (context.op().equals() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPNE, labelAfter); // ==
        } else if (context.op().less() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPGE, labelAfter); // <
        } else if (context.op().great() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPLE, labelAfter); // >
        } else if (context.op().notEq() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPEQ, labelAfter); // !=
        } else if (context.op().lessOrEq() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPGT, labelAfter); // <=
        } else if (context.op().greatOrEq() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPLT, labelAfter); // >=
        }
    }

    @Override
    public void exitWhileLoop(SimplePascalParser.WhileLoopContext ctx) {
        mainVisitor.visitJumpInsn(GOTO, whileStack.pop());
        mainVisitor.visitLabel(whileStack.pop());
    }

    @Override
    public void enterIfStatement(SimplePascalParser.IfStatementContext ctx) {
        SimplePascalParser.ExpressionCompareContext context = ctx.expressionLogic().expressionCompare();
        context.expressionArithmetic().forEach(this::parseArithmetic);
        Label label = new Label();
        ifStack.push(label);
        if (context.op().equals() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPNE, label); // ==
        } else if (context.op().less() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPGE, label); // <
        } else if (context.op().great() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPLE, label); // >
        } else if (context.op().notEq() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPEQ, label); // !=
        } else if (context.op().lessOrEq() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPGT, label); // <=
        } else if (context.op().greatOrEq() != null) {
            mainVisitor.visitJumpInsn(IF_ICMPLT, label); // >=
        }
    }

    @Override
    public void exitIfStatement(SimplePascalParser.IfStatementContext ctx) {
        mainVisitor.visitLabel(ifStack.pop());
    }

    @Override
    public void enterWriteln(SimplePascalParser.WritelnContext ctx) {
        mainVisitor.visitFieldInsn(GETSTATIC, getInternalName(System.class), "out", getDescriptor(PrintStream.class));
        parseArithmetic(ctx.arguments().expressionArithmetic());
        mainVisitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(PrintStream.class), "println", "(I)V", false);
    }

    @Override
    public void exitWriteln(SimplePascalParser.WritelnContext ctx) {

    }

    @Override
    public void enterBrk(SimplePascalParser.BrkContext ctx) {
        Label saveLabel = whileStack.pop();
        mainVisitor.visitJumpInsn(GOTO, whileStack.peek());
        whileStack.push(saveLabel);
    }

    @Override
    public void exitBrk(SimplePascalParser.BrkContext ctx) {

    }

    @Override
    public void enterType(SimplePascalParser.TypeContext ctx) {

    }

    @Override
    public void exitType(SimplePascalParser.TypeContext ctx) {

    }

    @Override
    public void enterLine(SimplePascalParser.LineContext ctx) {

    }

    @Override
    public void exitLine(SimplePascalParser.LineContext ctx) {

    }


    @Override
    public void enterDefs(SimplePascalParser.DefsContext ctx) {

    }

    @Override
    public void exitDefs(SimplePascalParser.DefsContext ctx) {

    }

    @Override
    public void enterVarDef(SimplePascalParser.VarDefContext ctx) {

    }

    @Override
    public void exitVarDef(SimplePascalParser.VarDefContext ctx) {

    }

    @Override
    public void enterExpressionLogic(SimplePascalParser.ExpressionLogicContext ctx) {

    }

    @Override
    public void exitExpressionLogic(SimplePascalParser.ExpressionLogicContext ctx) {

    }

    @Override
    public void enterExpressionCompare(SimplePascalParser.ExpressionCompareContext ctx) {

    }

    @Override
    public void exitExpressionCompare(SimplePascalParser.ExpressionCompareContext ctx) {

    }

    @Override
    public void enterExpressionArithmetic(SimplePascalParser.ExpressionArithmeticContext ctx) {

    }

    @Override
    public void exitExpressionArithmetic(SimplePascalParser.ExpressionArithmeticContext ctx) {

    }

    @Override
    public void enterExpressionValueEmbeded(SimplePascalParser.ExpressionValueEmbededContext ctx) {

    }

    @Override
    public void exitExpressionValueEmbeded(SimplePascalParser.ExpressionValueEmbededContext ctx) {

    }

    @Override
    public void enterOp(SimplePascalParser.OpContext ctx) {

    }

    @Override
    public void exitOp(SimplePascalParser.OpContext ctx) {

    }

    @Override
    public void enterVariable(SimplePascalParser.VariableContext ctx) {

    }

    @Override
    public void exitVariable(SimplePascalParser.VariableContext ctx) {

    }

    @Override
    public void enterNumber(SimplePascalParser.NumberContext ctx) {

    }

    @Override
    public void exitNumber(SimplePascalParser.NumberContext ctx) {

    }

    @Override
    public void enterDoWord(SimplePascalParser.DoWordContext ctx) {

    }

    @Override
    public void exitDoWord(SimplePascalParser.DoWordContext ctx) {

    }

    @Override
    public void enterThenWord(SimplePascalParser.ThenWordContext ctx) {
    }

    @Override
    public void exitThenWord(SimplePascalParser.ThenWordContext ctx) {
    }
    @Override
    public void enterModWord(SimplePascalParser.ModWordContext ctx) {
    }

    @Override
    public void exitModWord(SimplePascalParser.ModWordContext ctx) {
    }

    @Override
    public void enterMinus(SimplePascalParser.MinusContext ctx) {

    }

    @Override
    public void exitMinus(SimplePascalParser.MinusContext ctx) {

    }

    @Override
    public void enterPlus(SimplePascalParser.PlusContext ctx) {

    }

    @Override
    public void exitPlus(SimplePascalParser.PlusContext ctx) {

    }

    @Override
    public void enterLess(SimplePascalParser.LessContext ctx) {

    }

    @Override
    public void exitLess(SimplePascalParser.LessContext ctx) {

    }

    @Override
    public void enterLessOrEq(SimplePascalParser.LessOrEqContext ctx) {

    }

    @Override
    public void exitLessOrEq(SimplePascalParser.LessOrEqContext ctx) {

    }

    @Override
    public void enterGreat(SimplePascalParser.GreatContext ctx) {

    }

    @Override
    public void exitGreat(SimplePascalParser.GreatContext ctx) {

    }

    @Override
    public void enterGreatOrEq(SimplePascalParser.GreatOrEqContext ctx) {

    }

    @Override
    public void exitGreatOrEq(SimplePascalParser.GreatOrEqContext ctx) {

    }

    @Override
    public void enterEquals(SimplePascalParser.EqualsContext ctx) {

    }

    @Override
    public void exitEquals(SimplePascalParser.EqualsContext ctx) {

    }

    @Override
    public void enterNotEq(SimplePascalParser.NotEqContext ctx) {

    }

    @Override
    public void exitNotEq(SimplePascalParser.NotEqContext ctx) {

    }

    @Override
    public void enterArguments(SimplePascalParser.ArgumentsContext ctx) {
    }

    @Override
    public void exitArguments(SimplePascalParser.ArgumentsContext ctx) {
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {
    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {
    }
}

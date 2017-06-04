package ru.ifmo.ctddev.tenishcev.compiler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;

/**
 * Created by kris13 on 08.05.15.
 */
public class Main {
    static final String fileName = "SimpleNumber";

    public static void main(String[] args) {
        try {
            File file = new File(fileName + ".fpc");
            ANTLRInputStream stream = new ANTLRInputStream(new FileInputStream(file));
            SimplePascalLexer lexer = new SimplePascalLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SimplePascalParser parser = new SimplePascalParser(tokens);
            ParseTree tree = parser.program();

            ParseTreeWalker walker = new ParseTreeWalker();
            PascalToByteCode pascalToByteCode = new PascalToByteCode();
            walker.walk(pascalToByteCode, tree);

            File byteCode = new File(pascalToByteCode.nameOfProgram + ".class");
            byteCode.delete();
            byteCode.createNewFile();
            OutputStream outputStream = new FileOutputStream(byteCode);
            outputStream.write(pascalToByteCode.classWriter.toByteArray());
            outputStream.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
    }
}

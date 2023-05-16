package ru.ifmo.ctddev.tenishchev.compiler;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by kris13 on 08.05.15.
 */
public class Main {
    private static final String PASCAL_EXTENSION = ".fpc";

    public static void main(String[] args) {
        try {
            CharStream stream = CharStreams.fromFileName(args[0] + PASCAL_EXTENSION);
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

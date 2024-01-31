package main;

import main.ast.node.Program;
import main.compileError.CompileError;
import main.visitor.astPrinter.ASTPrinter;
import main.visitor.typeChecker.TypeChecker;
import main.visitor.nameAnalyzer.NameAnalyzer;
import org.antlr.v4.runtime.*;
import parsers.*;

import java.io.IOException;

public class UTL {
    public static void main(String[] args) throws IOException {
        CharStream reader = CharStreams.fromFileName(args[0]);
        UTLLexer lexer = new UTLLexer(reader);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        UTLParser parser = new UTLParser(tokens);
        Program program = parser.program().pro;
        NameAnalyzer nameAnalyzer = new NameAnalyzer();
        nameAnalyzer.visit(program);

        TypeChecker typeChecker = new TypeChecker();
        typeChecker.visit(program);

        if (typeChecker.typeErrors.size() > 0) {
            for (CompileError compileError : typeChecker.typeErrors)
                System.out.println(compileError.getMessage());
            return;
        }

        System.out.println("Compilation was Successful!!");





//        if (!nameAnalyzer.nameErrors.isEmpty()){
//            for(CompileError compileError: nameAnalyzer.nameErrors)
//                System.out.println(compileError.getMessage());
//        }
//        else {
//            ASTPrinter astPrinter = new ASTPrinter();
//            astPrinter.visit(program);
//        }





    }
}

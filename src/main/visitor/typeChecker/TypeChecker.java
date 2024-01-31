package main.visitor.typeChecker;

import main.visitor.Visitor;

import java.util.ArrayList;

import main.ast.node.expression.values.BoolValue;
import main.ast.node.expression.values.FloatValue;
import main.ast.node.expression.values.IntValue;
import main.compileError.*;
import main.compileError.name.UnsupportedOperandType;
import main.compileError.name.*;
import main.ast.type.Type;
import main.ast.type.NoType;
import main.ast.type.primitiveType.*;
import main.ast.type.complexType.*;
import main.ast.node.expression.*;
import main.ast.node.statement.*;
import main.ast.node.declaration.*;
import main.ast.node.expression.operators.BinaryOperator;
import main.ast.node.expression.operators.UnaryOperator;
import main.symbolTable.SymbolTable;
import main.symbolTable.symbolTableItems.*;

import main.symbolTable.itemException.*;
import main.visitor.Visitor;
import main.ast.node.Program;

public class TypeChecker extends Visitor<Void> {

    public ArrayList<CompileError> typeErrors = new ArrayList<>();
    ExpressionTypeChecker expressionTypeChecker = new ExpressionTypeChecker(typeErrors);

    @Override
    public Void visit(Program program) {
        program.getMain().accept(this);
        return null;
    }

    @Override
    public Void visit(MainDeclaration mainDeclaration) {
        var mainItem = new MainItem(mainDeclaration);
        var mainSymbolTable = new SymbolTable(SymbolTable.top, "main");

        mainItem.setMainSymbolTable(mainSymbolTable);

        SymbolTable.push(mainItem.getMainSymbolTable());

        for (var stmt : mainDeclaration.getBody()) {
            stmt.accept(this);

        }
        return null;
    }



    @Override
    public Void visit(ForStmt forStmt) {

        try {
            ForLoopItem forLoopItem = (ForLoopItem) SymbolTable.top.get(ForLoopItem.START_KEY + forStmt.toString());
            SymbolTable.push(forLoopItem.getForLoopSymbolTable());

        } catch (ItemNotFoundException e) {
            // unreachable
        }
        if (forStmt.getCondition() instanceof BoolValue )
            forStmt.getCondition().accept(expressionTypeChecker);
        else
            typeErrors.add(new ConditionInvalid(forStmt.getCondition().getLine()));


        for (var stmt : forStmt.getBody()) {
            stmt.accept(this);
        }

        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(IfElseStmt ifElseStmt){

        if (ifElseStmt.getCondition() instanceof BoolValue )
            ifElseStmt.getCondition().accept(expressionTypeChecker);
        else
            typeErrors.add(new ConditionInvalid(ifElseStmt.getCondition().getLine()));

        return null;
    }


    @Override
    public Void visit(AssignStmt assignStmt) {
        Type tl = assignStmt.getLValue().accept(expressionTypeChecker);
        Type tr = assignStmt.getRValue().accept(expressionTypeChecker);

        if (!expressionTypeChecker.sameType(tl, tr) && !(tl instanceof NoType) && !(tr instanceof NoType)) {
            typeErrors.add(new UnsupportedOperandType(assignStmt.getLine(), BinaryOperator.ASSIGN.name()));
        }

        if (!expressionTypeChecker.isLvalue(assignStmt.getLValue())) {
            // is handled in grammar
            typeErrors.add(new LeftSideNotLValue(assignStmt.getLine()));
        }
        return null;
    }

    @Override
    public Void visit(FunctionCall functionCall) {
        try {
            SymbolTable.root.get(FunctionItem.START_KEY + functionCall.getFunctionName().getName());
        } catch (ItemNotFoundException e) {
            // unreachable
        }

        functionCall.accept(expressionTypeChecker);
        return null;
    }

    @Override
    public Void visit(ReturnStmt returnStmt) {
        var retExpr = returnStmt.getReturnedExpr();
        if (retExpr != null) {
            retExpr.accept(expressionTypeChecker);
        }
        return null;
    }




    @Override
    public Void visit(VarDeclaration vardecStmt) {

        Type t1 = vardecStmt.getIdentifier().accept(expressionTypeChecker);
        Type t2 = vardecStmt.getType();
        if (!expressionTypeChecker.sameType(t1, t2) && !(t1 instanceof NoType) && !(t2 instanceof NoType)) {
            typeErrors.add(new UnsupportedOperandType(vardecStmt.getLine(), BinaryOperator.ASSIGN.name()));
        }
        return null;
    }



}

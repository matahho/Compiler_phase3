package main.visitor.typeChecker;

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


public class ExpressionTypeChecker extends Visitor<Type> {
    public ArrayList<CompileError> typeErrors;

    public ExpressionTypeChecker(ArrayList<CompileError> typeErrors) {
        this.typeErrors = typeErrors;
    }

    public boolean sameType(Type el1, Type el2) {
        return el1.getClass().equals(el2.getClass()) && !(el1 instanceof NoType || el2 instanceof NoType);
    }

    public boolean isLvalue(Expression expr) {
        return expr instanceof Identifier;
    }



    @Override
    public Type visit(BinaryExpression binaryExpression)
    {

        Type tl = binaryExpression.getLeft().accept(this);
        Type tr = binaryExpression.getRight().accept(this);

        BinaryOperator operator = binaryExpression.getBinaryOperator();

        if (tl instanceof NoType && tr instanceof NoType) {
            return (new NoType());
        }

        switch (operator) {

            case PLUS, MINUS, MULT,DIV,MOD -> {
                if (tl instanceof IntType && tr instanceof IntType) {
                    return new IntType();
                }
                if (tl instanceof FloatType && tr instanceof FloatType) {
                    return new FloatType();
                }
                if ((tl instanceof NoType && (tr instanceof IntType || tr instanceof FloatType)) ||
                        (tr instanceof NoType && (tl instanceof IntType || tl instanceof FloatType))) {
                    return new NoType();
                }

            }



            case EQ, NEQ, GT,LT -> {

                if (tl instanceof IntType && tr instanceof IntType) {
                    return new BoolType();
                }
                if (tl instanceof FloatType && tr instanceof FloatType) {
                    return new BoolType();
                }
                if (tl instanceof StringType && tr instanceof StringType) {
                    return new BoolType();
                }


                if (tl instanceof NoType || tr instanceof NoType) {
                    return new NoType();
                }

            }

            case AND,OR -> {
                if (tl instanceof BoolType && tr instanceof BoolType) {
                    return new BoolType();
                }
                if ((tl instanceof NoType && tr instanceof BoolType) ||
                        (tr instanceof NoType && tl instanceof BoolType)) {
                    return new NoType();
                }
            }
        }

        typeErrors.add(new UnsupportedOperandType(binaryExpression.getLine() , operator.name()));
        return new NoType();
    }


    @Override
     public Type visit(Identifier identifier) {
         try {


             VariableItem var = (VariableItem) SymbolTable.top.get(VariableItem.START_KEY + identifier.getName());
             return var.getType();
         } catch (ItemNotFoundException e) {
//             typeErrors.add(new VarNotDeclared(identifier.getLine(), identifier.getName()));
             VariableItem vi = new VariableItem(identifier.getName(), new NoType());
             try {
                 SymbolTable.top.put(vi);
             }
             catch (ItemAlreadyExistsException ee) {
                 // unreachable?
             }
             return new NoType();
         }
    }



    @Override
    public Type visit(IntValue value) {
        return new IntType();
    }

    @Override
    public Type visit(FloatValue value) {
        return new FloatType();
    }

    @Override
    public Type visit(BoolValue value) {
        return new BoolType();
    }






}























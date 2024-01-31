package main.symbolTable.symbolTableItems;

import main.symbolTable.SymbolTable;
import main.ast.node.statement.ForStmt;


public class ForLoopItem extends SymbolTableItem {
    protected SymbolTable forLoopSymbolTable;
    protected ForStmt forloopStmt;

    public static final String START_KEY = "ForLoop_";
    private static int counter = 0;
    private int id;




    public ForLoopItem(ForStmt forloopStmt) {
        this.name = forloopStmt.toString();
        this.forloopStmt = forloopStmt;
    }

    public SymbolTable getForLoopSymbolTable() {
        return this.forLoopSymbolTable;
    }

    public void setForLoopSymbolTable(SymbolTable symbolTable) {
        this.forLoopSymbolTable = symbolTable;
    }

    @Override
    public String getKey() {
        return ForLoopItem.START_KEY + this.name + this.id;
    }
}

package main.compileError.name;

import main.compileError.CompileError;

public class ConditionInvalid extends CompileError {
    int line;

    public ConditionInvalid(int line){
        this.line = line;

    }
    public String getMessage(){
        return "Line " + line + ": Condition must be bool" ;
    }
}

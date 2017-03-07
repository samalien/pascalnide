package com.duy.interpreter.tokens.grouping;


import com.duy.interpreter.linenumber.LineInfo;
import com.duy.interpreter.tokens.Token;

public class BracketedToken extends GrouperToken {

    public BracketedToken(LineInfo line) {
        super(line);
    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder("[");
        if (next != null) {
            tmp.append(next);
        }
        for (Token t : this.queue) {
            tmp.append(t).append(' ');
        }
        tmp.append(']');
        return tmp.toString();

    }

    public String toCode() {
        return "[";
    }

    @Override
    protected String getClosingText() {
        return "]";
    }

    @Override
    public precedence getOperatorPrecedence() {
        return precedence.Dereferencing;
    }
}

package com.duy.interpreter.tokens.value;


import com.duy.interpreter.linenumber.LineInfo;

public class IntegerToken extends ValueToken {
    public int value;

    public IntegerToken(LineInfo line, int i) {
        super(line);
        value = i;
    }

    @Override
    public String toString() {
        return "integer_value_of[" + value + ']';
    }

    @Override
    public String toCode() {
        return String.valueOf(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}

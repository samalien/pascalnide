package com.duy.interpreter.exceptions;


import com.js.interpreter.ast.AbstractFunction;
import com.js.interpreter.ast.FunctionDeclaration;
import com.duy.interpreter.exceptions.*;
import com.duy.interpreter.linenumber.LineInfo;

public class OverridingFunctionException extends com.duy.interpreter.exceptions.ParsingException {

    public OverridingFunctionException(FunctionDeclaration old, LineInfo line) {
        super(line, "Redefining function body for " + old.toString()
                + ", which was previous define at " + old.getLineNumber());
    }

    public OverridingFunctionException(AbstractFunction old,
                                       FunctionDeclaration news) {
        super(news.getLineNumber(), "Attempting to override plugin definition"
                + old.toString());
    }
}

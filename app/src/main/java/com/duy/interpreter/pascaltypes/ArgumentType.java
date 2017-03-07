package com.duy.interpreter.pascaltypes;


import com.duy.interpreter.exceptions.ParsingException;
import com.js.interpreter.ast.expressioncontext.ExpressionContext;
import com.js.interpreter.ast.returnsvalue.ReturnsValue;

import java.util.Iterator;

public interface ArgumentType {

    public ReturnsValue convertArgType(Iterator<ReturnsValue> args,
                                       ExpressionContext f) throws ParsingException;

    public ReturnsValue perfectFit(Iterator<ReturnsValue> types,
                                   ExpressionContext e) throws ParsingException;

    @SuppressWarnings("rawtypes")
    public Class getRuntimeClass();

}

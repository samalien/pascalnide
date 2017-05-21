package com.js.interpreter.runtime.codeunit;

import com.duy.pascal.backend.function_declaretion.AbstractFunction;
import com.duy.pascal.backend.lib.PascalLibrary;
import com.duy.pascal.backend.pascaltypes.DeclaredType;
import com.google.common.collect.ListMultimap;
import com.js.interpreter.ConstantDefinition;
import com.js.interpreter.VariableDeclaration;
import com.js.interpreter.codeunit.library.LibraryPascal;
import com.js.interpreter.expressioncontext.ExpressionContextMixin;
import com.js.interpreter.runtime.VariableContext;
import com.js.interpreter.runtime.exception.RuntimePascalException;

import java.util.ArrayList;
import java.util.Map;

public class RuntimePascalLibrary extends RuntimeExecutableCodeUnit<LibraryPascal>
        implements PascalLibrary {

    public RuntimePascalLibrary(LibraryPascal l) {
        super(l);
    }

    @Override
    public void runImpl() throws RuntimePascalException {

    }


    @Override
    public VariableContext getParentContext() {
        return null;
    }

    @Override
    public boolean instantiate(Map<String, Object> pluginargs) {
        return false;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void declareConstants(ExpressionContextMixin context) {
        ExpressionContextMixin program = getDefinition().getProgram();
        Map<String, ConstantDefinition> constants = program.getConstants();
        for (Map.Entry<String, ConstantDefinition> constant : constants.entrySet()) {
            context.declareConst(constant.getValue());
        }
    }

    @Override
    public void declareTypes(ExpressionContextMixin context) {
        ExpressionContextMixin program = getDefinition().getProgram();
        Map<String, DeclaredType> typedefs = program.getTypedefs();
        for (Map.Entry<String, DeclaredType> type : typedefs.entrySet()) {
            context.declareTypedef(type.getKey(), type.getValue());
        }
    }

    @Override
    public void declareVariables(ExpressionContextMixin context) {
        ExpressionContextMixin program = getDefinition().getProgram();
        ArrayList<VariableDeclaration> variables = program.getVariables();
        for (VariableDeclaration variable : variables) {
            context.declareVariable(variable);
        }
    }

    @Override
    public void declareFunctions(ExpressionContextMixin context) {
        ListMultimap<String, AbstractFunction> callableFunctions = context.getCallableFunctions();
        for (Map.Entry<String, AbstractFunction> function : callableFunctions.entries()) {
            context.declareFunction(function.getValue());
        }
    }
}

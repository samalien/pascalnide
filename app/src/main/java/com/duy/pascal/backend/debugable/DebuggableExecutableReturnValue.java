package com.duy.pascal.backend.debugable;

import com.js.interpreter.ast.expressioncontext.ExpressionContext;
import com.js.interpreter.ast.instructions.Executable;
import com.js.interpreter.ast.instructions.ExecutionResult;
import com.js.interpreter.ast.returnsvalue.LeftValue;
import com.js.interpreter.ast.returnsvalue.ReturnValue;
import com.js.interpreter.runtime.VariableContext;
import com.js.interpreter.runtime.codeunit.RuntimeExecutable;
import com.js.interpreter.runtime.exception.RuntimePascalException;
import com.js.interpreter.runtime.exception.UnhandledPascalException;

public abstract class DebuggableExecutableReturnValue implements Executable,
        ReturnValue {

    protected ReturnValue[] outputFormat;

    @Override
    public ReturnValue[] getOutputFormat() {
        return outputFormat;
    }

    @Override
    public void setOutputFormat(ReturnValue[] formatInfo) {
        this.outputFormat = formatInfo;
    }

    @Override
    public Object getValue(VariableContext f, RuntimeExecutable<?> main)
            throws RuntimePascalException {
        try {
            return getValueImpl(f, main);
        } catch (RuntimePascalException e) {
            throw e;
        } catch (Exception e) {
            throw new UnhandledPascalException(this.getLineNumber(), e);
        }
    }

    @Override
    public LeftValue asLValue(ExpressionContext f) {
        return null;
    }

    public abstract Object getValueImpl(VariableContext f, RuntimeExecutable<?> main)
            throws RuntimePascalException;

    @Override
    public ExecutionResult execute(VariableContext f, RuntimeExecutable<?> main)
            throws RuntimePascalException {
        try {
            if (main != null) {
                if (main.isDebugMode()) {
                    main.getDebugListener().onLine(getLineNumber());
                }
                main.incStack(getLineNumber());
            }
            ExecutionResult result = executeImpl(f, main);
            if (main != null) {
                main.decStack();
            }
            return result;
        } catch (RuntimePascalException e) {
            throw e;
        } catch (Exception e) {
            throw new UnhandledPascalException(this.getLineNumber(), e);
        }
    }

    public abstract ExecutionResult executeImpl(VariableContext f,
                                                RuntimeExecutable<?> main) throws RuntimePascalException;
}
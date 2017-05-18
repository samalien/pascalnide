package com.js.interpreter.ast;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.pascaltypes.DeclaredType;

import java.util.Map;

public class VariableDeclaration implements NamedEntity {

    /**
     * name of variable, always lower case
     */
    public String name;
    public DeclaredType type;
    private Object initialValue;
    private LineInfo line;

    public VariableDeclaration(@NonNull String name, @NonNull DeclaredType type,
                               @Nullable Object initialValue, LineInfo line) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.initialValue = initialValue;
    }

    public VariableDeclaration(@NonNull String name, @NonNull DeclaredType type,
                               LineInfo line) {
        this.name = name;
        this.type = type;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public DeclaredType getType() {
        return type;
    }

    public Object getInitialValue() {
        return initialValue;
    }

    public LineInfo getLineNumber() {
        return line;
    }


    public void initialize(Map<String, Object> map) {
        map.put(name, initialValue == null ? type.initialize() : initialValue);
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + type.hashCode();
    }

    @Override
    public String getEntityType() {
        return "variable";
    }

    @Override
    public String toString() {
        return "var " + name + " = " + initialValue;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return null;
    }
}
/*
 *  Copyright (c) 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.pascal.interperter.exceptions.parsing.convert;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.duy.pascal.interperter.ast.expressioncontext.ExpressionContext;
import com.duy.pascal.interperter.ast.runtime_value.value.RuntimeValue;
import com.duy.pascal.interperter.ast.runtime_value.value.access.VariableAccess;
import com.duy.pascal.interperter.declaration.lang.types.Type;
import com.duy.pascal.interperter.exceptions.parsing.ParsingException;


public final class UnConvertibleTypeException extends ParsingException {
    @NonNull
    private RuntimeValue value;
    @Nullable
    private Type valueType;
    @NonNull
    private Type targetType;
    @Nullable
    private RuntimeValue identifier;
    @NonNull
    private ExpressionContext scope;

    public UnConvertibleTypeException(@NonNull RuntimeValue value, @NonNull Type targetType, @NonNull Type valueType, @NonNull ExpressionContext scope) {
        super(value.getLineNumber(), String.format("The expression or variable \"%s\" is of type \"%s\", which cannot be converted to the type \"%s\"", value, valueType, targetType));
        this.value = value;
        this.valueType = valueType;
        this.targetType = targetType;
        this.scope = scope;
    }

    public UnConvertibleTypeException(@NonNull RuntimeValue value, @NonNull Type identifierType, @Nullable Type valueType, @NonNull RuntimeValue identifier, @NonNull ExpressionContext scope) {
        super(value.getLineNumber(), String.format("The expression or variable \"%s\" is of type \"%s\", which cannot be converted to the type \"%s\" of expression or variable %s", value, valueType, identifierType, identifier));
        this.value = value;
        this.valueType = valueType;
        this.targetType = identifierType;
        this.identifier = identifier;
        this.scope = scope;
    }

    @NonNull
    public final RuntimeValue getValue() {
        return this.value;
    }

    public final void setValue(@NonNull RuntimeValue var1) {
        this.value = var1;
    }

    @Nullable
    public final Type getValueType() {
        return this.valueType;
    }

    public final void setValueType(@Nullable Type var1) {
        this.valueType = var1;
    }

    @NonNull
    public final Type getTargetType() {
        return this.targetType;
    }

    public final void setTargetType(@NonNull Type var1) {
        this.targetType = var1;
    }

    @Nullable
    public final RuntimeValue getIdentifier() {
        return this.identifier;
    }

    public final void setIdentifier(@Nullable RuntimeValue var1) {
        this.identifier = var1;
    }

    @NonNull
    public final ExpressionContext getScope() {
        return this.scope;
    }

    public final void setScope(@NonNull ExpressionContext var1) {
        this.scope = var1;
    }

    public boolean getCanAutoFix() {
        return identifier instanceof VariableAccess || value instanceof VariableAccess;
    }
}
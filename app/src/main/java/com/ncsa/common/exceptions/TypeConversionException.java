package com.ncsa.common.exceptions;

public class TypeConversionException extends BaseCommonException {
    private static final long serialVersionUID = 2021L;

    public TypeConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeConversionException(String message) {
        super(message);
    }

}

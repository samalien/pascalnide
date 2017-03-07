package com.duy.interpreter.tokens.basic;

import com.duy.interpreter.linenumber.LineInfo;

public class UntilToken extends BasicToken {

	public UntilToken(LineInfo line) {
		super(line);
	}

	@Override
	public String toString() {
		return "until";
	}
}

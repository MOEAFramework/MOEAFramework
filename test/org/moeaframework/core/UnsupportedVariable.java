package org.moeaframework.core;

public class UnsupportedVariable implements Variable {

	private static final long serialVersionUID = 7614517658356868257L;

	@Override
	public Variable copy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void randomize() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String encode() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void decode(String value) {
		throw new UnsupportedOperationException();
	}
	
}
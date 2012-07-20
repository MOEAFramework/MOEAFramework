package org.moeaframework.util.tree;

public class NOP extends Node {
	
	public NOP() {
		super(Void.class);
	}

	@Override
	public NOP copyNode() {
		return new NOP();
	}

	@Override
	public Void evaluate(Environment environment) {
		return null;
	}

}

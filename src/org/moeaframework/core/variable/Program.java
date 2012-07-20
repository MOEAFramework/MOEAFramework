package org.moeaframework.core.variable;

import org.moeaframework.core.Variable;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;
import org.moeaframework.util.tree.Rules;

public class Program extends Node implements Variable {

	private static final long serialVersionUID = -2621361322042428290L;

	private final Rules rules;

	public Program(Rules rules, Class<?> type) {
		super(type, type);
		this.rules = rules;
	}
	
	public Rules getRules() {
		return rules;
	}
	
	@Override
	public Program copy() {
		return (Program)copyTree();
	}

	@Override
	public Program copyNode() {
		return new Program(rules, getReturnType());
	}

	@Override
	public Object evaluate(Environment environment) {
		return getArgument(0).evaluate(environment);
	}

}

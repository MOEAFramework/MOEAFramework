package org.moeaframework.util.tree;

public class Lambda extends Node {
	
	private final Node node;
	
	private final String[] variableNames;
	
	public Lambda(Node node, String variableName, Class<?> variableType) {
		this(node, new String[] { variableName }, new Class<?>[] { variableType });
	}
	
	public Lambda(Node node, String variable1, Class<?> type1, String variable2, Class<?> type2) {
		this(node, new String[] { variable1, variable2 }, new Class<?>[] { type1, type2 });
	}
	
	public Lambda(Node node, String[] variableNames, Class<?>[] variableTypes) {
		super(node.getReturnType(), variableTypes);
		this.node = node;
		this.variableNames = variableNames;
		
		if (!node.isValid()) {
			throw new IllegalArgumentException("lambda function is not valid");
		}
	}

	@Override
	public Node copyNode() {
		return new Lambda(node.copyTree(), variableNames, getArgumentTypes());
	}

	@Override
	public Object evaluate(Environment environment) {
		Environment closure = new Environment(environment);
		
		for (int i = 0; i < getNumberOfArguments(); i++) {
			closure.set(variableNames[i], getArgument(i).evaluate(environment));
		}

		return node.evaluate(closure);
	}

}

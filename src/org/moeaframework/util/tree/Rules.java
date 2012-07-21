package org.moeaframework.util.tree;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.PRNG;

public class Rules {
	
	private Class<?> returnType;
	
	private Node scaffolding;
	
	private int maxInitializationDepth;
	
	private int maxCrossoverDepth;
	
	private double functionCrossoverProbability;
	
	private List<Node> availableNodes;
	
	public Rules() {
		super();
		
		returnType = Void.class;
		maxInitializationDepth = 5;
		maxCrossoverDepth = Integer.MAX_VALUE;
		functionCrossoverProbability = 0.5;
		availableNodes = new ArrayList<Node>();
	}
	
	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public Node getScaffolding() {
		return scaffolding;
	}

	public void setScaffolding(Node scaffolding) {
		this.scaffolding = scaffolding;
		setReturnType(scaffolding.getReturnType());
	}

	public int getMaxInitializationDepth() {
		return maxInitializationDepth;
	}

	public void setMaxInitializationDepth(int maxInitializationDepth) {
		this.maxInitializationDepth = maxInitializationDepth;
	}

	public int getMaxCrossoverDepth() {
		return maxCrossoverDepth;
	}

	public void setMaxCrossoverDepth(int maxCrossoverDepth) {
		this.maxCrossoverDepth = maxCrossoverDepth;
	}

	public double getFunctionCrossoverProbability() {
		return functionCrossoverProbability;
	}

	public void setFunctionCrossoverProbability(double functionCrossoverProbability) {
		this.functionCrossoverProbability = functionCrossoverProbability;
	}

	public void add(Node node) {
		availableNodes.add(node);
	}
	
	public void populateWithLogic() {
		add(new And());
		add(new Or());
		add(new Not());
		add(new Xor());
		add(new Equals());
		add(new GreaterThan());
		add(new LessThan());
		add(new GreaterThanOrEqual());
		add(new LessThanOrEqual());
		add(new Constant(true));
		add(new Constant(false));
	}
	
	public void populateWithArithmetic() {
		add(new Add());
		add(new Subtract());
		add(new Multiply());
		add(new Divide());
		add(new Modulus());
		add(new Floor());
		add(new Ceil());
		add(new Round());
		add(new Max());
		add(new Min());
		add(new Power());
		add(new Square());
		add(new SquareRoot());
		add(new Abs());
		add(new Log());
		add(new Log10());
		add(new Exp());
		add(new Sign());
	}
	
	public void populateWithTrig() {
		add(new Sin());
		add(new Cos());
		add(new Tan());
		add(new Asin());
		add(new Acos());
		add(new Atan());
		add(new Sinh());
		add(new Cosh());
		add(new Tanh());
	}
	
	public void populateWithControl() {
		add(new IfElse());
		add(new IfElse(Number.class));
		add(new Sequence());
		add(new NOP());
	}
	
	public void populateWithConstants() {
		add(new Constant(0));
		add(new Constant(1));
		add(new Constant(2));
		add(new Constant(10));
		add(new Constant(-1));
		add(new Constant(Math.E));
		add(new Constant(Math.PI));
	}
	
	public void populateWithDefaults() {
		populateWithLogic();
		populateWithArithmetic();
		populateWithTrig();
		populateWithControl();
		populateWithConstants();
	}
	
	public void replace(Node original, Node replacement) {
		Node parent = original.getParent();
		
		if (parent != null) {
			for (int i = 0; i < parent.getNumberOfArguments(); i++) {
				if (parent.getArgument(i) == original) {
					parent.setArgument(i, replacement);
					break;
				}
			}
		}
	}
	
	public List<Node> getAvailableNodes() {
		return availableNodes;
	}
	
	public List<Node> listAvailableCrossoverNodes(Node node, Class<?> type) {
		List<Node> result = new ArrayList<Node>();
		
		if (type.isAssignableFrom(node.getReturnType())) {
			result.add(node);
		}
		
		for (int i = 0; i < node.getNumberOfArguments(); i++) {
			result.addAll(listAvailableCrossoverNodes(node.getArgument(i),
					type));
		}
		
		return result;
	}

	public List<Node> listAvailableMutations(Node node) {
		List<Node> result = new ArrayList<Node>();
		
		for (Node mutation : availableNodes) {
			if (isMutationCompatible(node, mutation)) {
				result.add(mutation);
			}
		}
		
		return result;
	}
	
	protected boolean isMutationCompatible(Node original, Node mutation) {
		if (!original.getReturnType().isAssignableFrom(mutation.getReturnType())) {
			return false;
		}
		
		if (original.getNumberOfArguments() != mutation.getNumberOfArguments()) {
			return false;
		}
		
		for (int i = 0; i < original.getNumberOfArguments(); i++) {
			if (!original.getArgumentType(i).isAssignableFrom(mutation.getArgumentType(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	public List<Node> listAvailableNodes(Class<?> type) {
		List<Node> result = new ArrayList<Node>();
		
		for (Node node : availableNodes) {
			if (type.isAssignableFrom(node.getReturnType())) {
				result.add(node);
			}
		}
		
		return result;
	}
	
	public List<Node> listAvailableTerminals(Class<?> type) {
		List<Node> result = new ArrayList<Node>();
		
		for (Node node : availableNodes) {
			if ((node.getNumberOfArguments() == 0) &&
					type.isAssignableFrom(node.getReturnType())) {
				result.add(node);
			}
		}
		
		return result;
	}
	
	public List<Node> listAvailableFunctions(Class<?> type) {
		List<Node> result = new ArrayList<Node>();
		
		for (Node node : availableNodes) {
			if ((node.getNumberOfArguments() > 0) &&
					type.isAssignableFrom(node.getReturnType())) {
				result.add(node);
			}
		}
		
		if (result.isEmpty()) {
			result.addAll(listAvailableTerminals(type));
		}
		
		return result;
	}
	
	public Node buildTreeFull(Class<?> type, int depth) {
		if (depth == 0) {
			return PRNG.nextItem(listAvailableTerminals(type)).copyNode();
		} else {
			Node node = PRNG.nextItem(listAvailableFunctions(type)).copyNode();
			
			for (int i = 0; i < node.getNumberOfArguments(); i++) {
				node.setArgument(i, buildTreeFull(node.getArgumentType(i), depth-1));
			}
			
			return node;
		}
	}
	
	public Node buildTreeGrow(Class<?> type, int depth) {
		if (depth == 0) {
			return PRNG.nextItem(listAvailableTerminals(type)).copyNode();
		} else {
			Node node = PRNG.nextItem(listAvailableNodes(type)).copyNode();
			
			for (int i = 0; i < node.getNumberOfArguments(); i++) {
				node.setArgument(i, buildTreeGrow(node.getArgumentType(i), depth-1));
			}
			
			return node;
		}
	}
	
	public Node buildTreeFull(Node node, int depth) {
		if (depth == 0) {
			return node.copyNode();
		} else {
			Node copy = node.copyNode();
			
			for (int i = 0; i < node.getNumberOfArguments(); i++) {
				if (node.getArgument(i) == null) {
					copy.setArgument(i, buildTreeFull(node.getArgumentType(i), depth-1));
				} else {
					copy.setArgument(i, buildTreeFull(node.getArgument(i), depth-1));
				}
			}
			
			return copy;
		}
	}
	
	public Node buildTreeGrow(Node node, int depth) {
		if (depth == 0) {
			return node.copyNode();
		} else {
			Node copy = node.copyNode();
			
			for (int i = 0; i < node.getNumberOfArguments(); i++) {
				if (node.getArgument(i) == null) {
					copy.setArgument(i, buildTreeGrow(node.getArgumentType(i), depth-1));
				} else {
					copy.setArgument(i, buildTreeGrow(node.getArgument(i), depth-1));
				}
			}
			
			return copy;
		}
	}

}

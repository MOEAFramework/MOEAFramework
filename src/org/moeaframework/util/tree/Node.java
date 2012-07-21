package org.moeaframework.util.tree;

public abstract class Node {

	private Node parent;

	private final Node[] arguments;
	
	private final Class<?> returnType;
	
	private final Class<?>[] argumentTypes;
	
	public Node() {
		this(Void.class);
	}

	public Node(Class<?> returnType, Class<?>... argumentTypes) {
		super();
		this.returnType = returnType;
		this.argumentTypes = argumentTypes;
		
		arguments = new Node[argumentTypes.length];
	}

	public int getNumberOfArguments() {
		return argumentTypes.length;
	}

	public Node getArgument(int index) {
		return arguments[index];
	}

	public Node getParent() {
		return parent;
	}

	void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Class<?> getReturnType() {
		return returnType;
	}
	
	public Class<?> getArgumentType(int index) {
		return argumentTypes[index];
	}

	public Node setArgument(int index, Node expression) {
		expression.setParent(this);
		arguments[index] = expression;
		return this;
	}
	
	public int size() {
		int size = 0;
		
		for (Node argument : arguments) {
			size += argument.size();
		}
		
		return size + 1;
	}

	public int getDepth() {
		if (parent == null) {
			return 0;
		} else {
			return parent.getDepth() + 1;
		}
	}

	public int getMinimumHeight() {
		if (arguments.length == 0) {
			return 0;
		} else {
			int height = Integer.MAX_VALUE;
			
			for (Node argument : arguments) {
				height = Math.min(height, argument.getMinimumHeight());
			}
			
			return height + 1;
		}
	}

	public int getMaximumHeight() {
		if (arguments.length == 0) {
			return 0;
		} else {
			int height = 0;
			
			for (Node argument : arguments) {
				height = Math.max(height, argument.getMaximumHeight());
			}
			
			return height+1;
		}
	}

	public abstract Node copyNode();

	public Node copyTree() {
		Node node = copyNode();
		
		for (int i = 0; i < getNumberOfArguments(); i++) {
			node.setArgument(i, getArgument(i).copyTree());
		}
		
		return node;
	}

	public abstract Object evaluate(Environment environment);
	
	public boolean isValid() {
		for (int i = 0; i < getNumberOfArguments(); i++) {
			Node argument = getArgument(i);
			
			if (argument == null) {
				System.err.println("argument is null");
				return false;
			}
			
			if (!getArgumentType(i).isAssignableFrom(argument.getReturnType())) {
				System.err.println(getClass().getSimpleName() + " (" + i + "): " + getArgumentType(i) + " not assignable from " + argument.getReturnType());
				return false;
			}
			
			if (!argument.isValid()) {
				return false;
			}
		}
		
		return true;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(getClass().getSimpleName());

		for (int i = 0; i < getNumberOfArguments(); i++) {
			sb.append(' ');
			if (getArgument(i) == null) {
				sb.append(getArgumentType(i).getSimpleName());
			} else {
				sb.append(getArgument(i).toString());
			}
		}
			
		sb.append(')');
		return sb.toString();
	}
	
	Class<?>[] getArgumentTypes() {
		return argumentTypes;
	}
	
	public boolean isTerminal() {
		return getNumberOfArguments() == 0;
	}
	
	public int getNumberOfFunctions(Class<?> type) {
		return getNumberOfNodes(type, true, false);
	}
	
	public Node getFunctionAt(Class<?> type, int index) {
		return getNodeAt(type, true, false, index);
	}
	
	public int getNumberOfFunctions() {
		return getNumberOfFunctions(Object.class);
	}
	
	public Node getFunctionAt(int index) {
		return getFunctionAt(Object.class, index);
	}
	
	public int getNumberOfTerminals(Class<?> type) {
		return getNumberOfNodes(type, false, true);
	}
	
	public Node getTerminalAt(Class<?> type, int index) {
		return getNodeAt(type, false, true, index);
	}
	
	public int getNumberOfTerminals() {
		return getNumberOfTerminals(Object.class);
	}
	
	public Node getTerminalAt(int index) {
		return getTerminalAt(Object.class, index);
	}
	
	public int getNumberOfNodes(Class<?> type) {
		return getNumberOfNodes(type, true, true);
	}
	
	public Node getNodeAt(Class<?> type, int index) {
		return getNodeAt(type, true, true, index);
	}
	
	public int getNumberOfNodes() {
		return getNumberOfNodes(Object.class);
	}
	
	public Node getNodeAt(int index) {
		return getNodeAt(Object.class, index);
	}
	
	protected int getNumberOfNodes(Class<?> type, boolean includeFunctions,
			boolean includeTerminals) {
		int result = 0;

		// is this node a match?
		if ((isTerminal() && 
				includeTerminals && 
				type.isAssignableFrom(getReturnType())) ||
			(!isTerminal() && 
				includeFunctions && 
				type.isAssignableFrom(getReturnType()))) {
			result++;
		}
		
		// count matching nodes in arguments
		for (Node argument : arguments) {
			result += argument.getNumberOfNodes(type, includeFunctions,
					includeTerminals);
		}
		
		return result;
	}
	
	protected Node getNodeAt(Class<?> type, boolean includeFunctions,
			boolean includeTerminals, int index) {
		// is this node a match?
		if ((isTerminal() && includeTerminals && 
				type.isAssignableFrom(getReturnType())) ||
			(!isTerminal() && includeFunctions && 
				type.isAssignableFrom(getReturnType()))) {
			if (index == 0) {
				return this;
			} else {
				index--;
			}
		}
		
		// recurse on matching nodes in arguments
		for (Node argument : arguments) {
			int size = argument.getNumberOfNodes(type, includeFunctions,
					includeTerminals);
			
			if (size > index) {
				// this argument contains the node to return
				return getNodeAt(type, includeFunctions, includeTerminals, 
						index);
			} else {
				// this argument does not contain the node
				index -= size;
			}
		}
		
		throw new IndexOutOfBoundsException();
	}

}

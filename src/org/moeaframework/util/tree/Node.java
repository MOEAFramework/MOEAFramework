/* Copyright 2009-2018 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.util.tree;

/**
 * A node in an expression tree.  Expression trees are strongly typed, meaning
 * nodes have defined return types and argument types.  The return type of all
 * nodes must match the argument type from its parent node.
 */
public abstract class Node {
	
	/**
	 * {@code true} if this node should not be modified, altered or replaced;
	 * {@code false} otherwise.
	 */
	private boolean fixed;

	/**
	 * The parent of this node; or {@code null} if this node has no parent.
	 */
	private Node parent;

	/**
	 * The arguments (children) of this node.
	 */
	private final Node[] arguments;
	
	/**
	 * The return type of this node.
	 */
	private final Class<?> returnType;
	
	/**
	 * The types of the arguments (children) of this node.
	 */
	private final Class<?>[] argumentTypes;
	
	/**
	 * Constructs a new node.
	 */
	public Node() {
		this(Void.class);
	}

	/**
	 * Constructs a new node with the given return type and argument types.
	 * 
	 * @param returnType the return type of the node
	 * @param argumentTypes the type of the arguments, if any, for the node
	 */
	public Node(Class<?> returnType, Class<?>... argumentTypes) {
		super();
		this.returnType = returnType;
		this.argumentTypes = argumentTypes;
		
		arguments = new Node[argumentTypes.length];
	}
	
	/**
	 * Calls {@link #setFixed(boolean)} on all nodes in the subtree rooted at
	 * this node.
	 * 
	 * @param fixed {@code true} if all nodes in the subtree rooted at this
	 *        node should not be modified, altered, or replaced; {@code false}
	 *        otherwise
	 */
	public void setFixedTree(boolean fixed) {
		setFixed(fixed);
		
		for (Node argument : arguments) {
			if (argument != null) {
				argument.setFixedTree(fixed);
			}
		}
	}
	
	/**
	 * Set to {@code true} if this node should not be modified, altered, or
	 * replaced; {@code false} otherwise.
	 * 
	 * @param fixed {@code true} if this node should not be modified, altered,
	 *        or replaced; {@code false} otherwise
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	/**
	 * Returns {@code true} if this node should not be modified, altered, or
	 * replaced; {@code false} otherwise.
	 * 
	 * @return {@code true} if this node should not be modified, altered, or
	 *         replaced; {@code false} otherwise
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * Returns the number of arguments (child nodes) of this node.
	 * 
	 * @return the number of arguments (child nodes) of this node
	 */
	public int getNumberOfArguments() {
		return argumentTypes.length;
	}

	/**
	 * Returns the argument (child node) at the specified index.
	 * 
	 * @param index the index of the argument to return
	 * @return the argument (child node) at the specified index
	 */
	public Node getArgument(int index) {
		return arguments[index];
	}

	/**
	 * Returns the parent of this node; or {@code null} if this node has no
	 * parent.
	 * 
	 * @return the parent of this node; or {@code null} if this node has no
	 *         parent
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Sets the parent of this node.
	 * 
	 * @param parent the parent of this node
	 */
	void setParent(Node parent) {
		this.parent = parent;
	}
	
	/**
	 * Returns the return type of this node.
	 * 
	 * @return the return type of this node
	 */
	public Class<?> getReturnType() {
		return returnType;
	}
	
	/**
	 * Returns the type of the argument (child node) at the specified index.
	 * 
	 * @param index the index of the argument
	 * @return the type of the argument (child node) at the specified index
	 */
	public Class<?> getArgumentType(int index) {
		return argumentTypes[index];
	}

	/**
	 * Sets the argument (child node) at the specified index.
	 * 
	 * @param index the index of the new argument
	 * @param expression the expression defining the argument
	 * @return a reference to this node, allowing multiple calls to be chained
	 *         together
	 */
	public Node setArgument(int index, Node expression) {
		expression.setParent(this);
		arguments[index] = expression;
		return this;
	}
	
	/**
	 * Returns the number of nodes contained in the tree rooted at this node.
	 * 
	 * @return the number of nodes contained in the tree rooted at this node
	 */
	public int size() {
		int size = 0;
		
		for (Node argument : arguments) {
			size += argument.size();
		}
		
		return size + 1;
	}

	/**
	 * Returns the depth of this node, which is the number of branches between
	 * this node and the root.
	 * 
	 * @return the depth of this node
	 */
	public int getDepth() {
		if (parent == null) {
			return 0;
		} else {
			return parent.getDepth() + 1;
		}
	}

	/**
	 * Returns the number of branches between this node and the nearest leaf.
	 * 
	 * @return the number of branches between this node and the nearest leaf
	 */
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

	/**
	 * Returns the number of branches between this node and the furthest leaf.
	 * 
	 * @return the number of branches between this node and the furthest leaf
	 */
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

	/**
	 * Returns a copy of this node, but without any children or parents
	 * assigned.
	 * 
	 * @return a copy of this node, but without any children or parents
	 *         assigned
	 */
	public abstract Node copyNode();

	/**
	 * Returns a copy of this node including copies of all its arguments (child
	 * nodes).  All attributes of the node are also retained, such as 
	 * {@link #isFixed()}.
	 * 
	 * @return a copy of this node including copies of all its arguments (child
	 *         nodes)
	 */
	public Node copyTree() {
		Node node = copyNode();
		
		node.setFixed(isFixed());
		
		for (int i = 0; i < getNumberOfArguments(); i++) {
			if (getArgument(i) != null) {
				node.setArgument(i, getArgument(i).copyTree());
			}
		}
		
		return node;
	}

	/**
	 * Evaluates this node in the context of the specified environment.
	 * 
	 * @param environment the execution environment
	 * @return the result of evaluating this node
	 */
	public abstract Object evaluate(Environment environment);
	
	/**
	 * Returns {@code true} if this node and its arguments are valid;
	 * {@code false} otherwise.  A valid node has all arguments defined,
	 * all arguments are valid, and all arguments are the appropriate type.
	 * 
	 * @return {@code true} if this node and its arguments are valid;
	 *         {@code false} otherwise
	 */
	public boolean isValid() {
		for (int i = 0; i < getNumberOfArguments(); i++) {
			Node argument = getArgument(i);
			
			if (argument == null) {
				System.err.println("argument is null");
				return false;
			}
			
			if (!getArgumentType(i).isAssignableFrom(argument.getReturnType())) {
				System.err.println(getClass().getSimpleName() + " (" + i +
						"): " + getArgumentType(i) + " not assignable from " +
						argument.getReturnType());
				return false;
			}
			
			if (!argument.isValid()) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
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
	
	/**
	 * Returns the types of the arguments of this node.
	 * 
	 * @return the types of the arguments of this node
	 */
	Class<?>[] getArgumentTypes() {
		return argumentTypes;
	}
	
	/**
	 * Returns {@code true} if this is a terminal node (has no arguments); 
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if this is a terminal node (has no arguments); 
	 *         {@code false} otherwise
	 */
	public boolean isTerminal() {
		return getNumberOfArguments() == 0;
	}
	
	/**
	 * Returns the number of function (non-terminal) nodes contained in the
	 * subtree rooted at this node that match the given return type.
	 * 
	 * @param type the return type of the node
	 * @return the number of function (non-terminal) nodes contained in the
	 *         subtree rooted at this node that match the given return type
	 */
	public int getNumberOfFunctions(Class<?> type) {
		return getNumberOfNodes(type, true, false);
	}

	/**
	 * Returns the function (non-terminal) node at the specified index in the
	 * subtree rooted at this node, counting only nodes that match the given
	 * return type.  A depth-first search (DFS) walk of the tree is used to
	 * find the appropriate node.
	 * 
	 * @param type the return type of the node
	 * @param index the index of the node to return
	 * @return the function (non-terminal) node at the specified index in the
	 *         subtree rooted at this node, counting only nodes that match the
	 *         given return type
	 * @throws IndexOutOfBoundsException if the index refers to a node not
	 *         within the subtree rooted at this node
	 */
	public Node getFunctionAt(Class<?> type, int index) {
		return getNodeAt(type, true, false, index);
	}
	
	/**
	 * Returns the number of function (non-terminal) nodes contained in the
	 * subtree rooted at this node.
	 * 
	 * @return the number of function (non-terminal) nodes contained in the
	 *         subtree rooted at this node
	 */
	public int getNumberOfFunctions() {
		return getNumberOfFunctions(Object.class);
	}
	
	/**
	 * Returns the function (non-terminal) node at the specified index in the
	 * subtree rooted at this node.  A depth-first search (DFS) walk of the
	 * tree is used to find the appropriate node.
	 * 
	 * @param index the index of the node to return
	 * @return the function (non-terminal) node at the specified index in the
	 *         subtree rooted at this node
	 * @throws IndexOutOfBoundsException if the index refers to a node not
	 *         within the subtree rooted at this node
	 */
	public Node getFunctionAt(int index) {
		return getFunctionAt(Object.class, index);
	}
	
	/**
	 * Returns the number of terminal nodes contained in the subtree rooted at
	 * this node that match the given return type.
	 * 
	 * @param type the return type of the node
	 * @return the number of terminal nodes contained in the subtree rooted at
	 *         this node that match the given return type
	 */
	public int getNumberOfTerminals(Class<?> type) {
		return getNumberOfNodes(type, false, true);
	}
	
	/**
	 * Returns the terminal node at the specified index in the subtree rooted
	 * at this node, counting only nodes that match the given return type.  A
	 * depth-first search (DFS) walk of the tree is used to find the
	 * appropriate node.
	 * 
	 * @param type the return type of the node
	 * @param index the index of the node to return
	 * @return the terminal node at the specified index in the subtree rooted
	 *         at this node, counting only nodes that match the given return
	 *         type
	 * @throws IndexOutOfBoundsException if the index refers to a node not
	 *         within the subtree rooted at this node
	 */
	public Node getTerminalAt(Class<?> type, int index) {
		return getNodeAt(type, false, true, index);
	}
	
	/**
	 * Returns the number of terminal nodes contained in the subtree rooted at
	 * this node.
	 * 
	 * @return the number of terminal nodes contained in the subtree rooted at
	 *         this node
	 */
	public int getNumberOfTerminals() {
		return getNumberOfTerminals(Object.class);
	}
	
	/**
	 * Returns the terminal node at the specified index in the subtree rooted
	 * at this node.  A depth-first search (DFS) walk of the tree is used to
	 * find the appropriate node.
	 * 
	 * @param index the index of the node to return
	 * @return the terminal node at the specified index in the subtree rooted
	 *         at this node
	 * @throws IndexOutOfBoundsException if the index refers to a node not
	 *         within the subtree rooted at this node
	 */
	public Node getTerminalAt(int index) {
		return getTerminalAt(Object.class, index);
	}
	
	/**
	 * Returns the number of nodes contained in the subtree rooted at this
	 * node that match the given return type.
	 * 
	 * @param type the return type of the node
	 * @return the number of nodes contained in the subtree rooted at this
	 *         node that match the given return type
	 */
	public int getNumberOfNodes(Class<?> type) {
		return getNumberOfNodes(type, true, true);
	}
	
	/**
	 * Returns the node at the specified index in the subtree rooted at this
	 * node, counting only nodes that match the given return type.  A
	 * depth-first search (DFS) walk of the tree is used to find the
	 * appropriate node.
	 * 
	 * @param type the return type of the node
	 * @param index the index of the node to return
	 * @return the node at the specified index in the subtree rooted at this
	 *         node, counting only nodes that match the given return type
	 * @throws IndexOutOfBoundsException if the index refers to a node not
	 *         within the subtree rooted at this node
	 */
	public Node getNodeAt(Class<?> type, int index) {
		return getNodeAt(type, true, true, index);
	}
	
	/**
	 * Returns the number of nodes contained in the subtree rooted at this
	 * node.
	 * 
	 * @return the number of nodes contained in the subtree rooted at this
	 *         node
	 */
	public int getNumberOfNodes() {
		return getNumberOfNodes(Object.class);
	}
	
	/**
	 * Returns the node at the specified index in the subtree rooted at this
	 * node.  A depth-first search (DFS) walk of the tree is used to find the
	 * appropriate node.
	 * 
	 * @param index the index of the node to return
	 * @return the node at the specified index in the subtree rooted at this
	 *         node
	 * @throws IndexOutOfBoundsException if the index refers to a node not
	 *         within the subtree rooted at this node
	 */
	public Node getNodeAt(int index) {
		return getNodeAt(Object.class, index);
	}
	
	/**
	 * Returns the number of nodes contained in the subtree rooted at this
	 * node.
	 * 
	 * @param type the return type of the node
	 * @param includeFunctions {@code true} if functions (non-terminals) are
	 *        counted; {@code false} otherwise
	 * @param includeTerminals {@code true} if terminals are counted;
	 *        {@code false} otherwise
	 * @return the number of nodes contained in the subtree rooted at this
	 *         node
	 */
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
	
	/**
	 * Returns the node at the specified index in the subtree rooted at this
	 * node.  A depth-first search (DFS) walk of the tree is used to find the
	 * appropriate node.
	 * 
	 * @param type the return type of the node
	 * @param includeFunctions {@code true} if functions (non-terminals) are
	 *        counted; {@code false} otherwise
	 * @param includeTerminals {@code true} if terminals are counted;
	 *        {@code false} otherwise
	 * @param index the index of the node to return
	 * @return the node at the specified index in the subtree rooted at this
	 *         node
	 * @throws IndexOutOfBoundsException if the index refers to a node not
	 *         within the subtree rooted at this node
	 */
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
				return argument.getNodeAt(type, includeFunctions,
						includeTerminals, index);
			} else {
				// this argument does not contain the node
				index -= size;
			}
		}
		
		throw new IndexOutOfBoundsException(
				"index does not reference node in tree");
	}

}

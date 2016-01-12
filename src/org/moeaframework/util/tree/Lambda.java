/* Copyright 2009-2016 David Hadka
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
 * The node for defining an immutable, anonymous function.  Unlike
 * {@link Define}, the behavior (body) of a {@code Lambda} can not be modified.
 * Therefore, {@code Lambda}s are useful for providing pre-defined functions
 * built using existing {@code Node}s.
 * 
 * @see Define
 */
public class Lambda extends Node {
	
	/**
	 * The body of this function.
	 */
	private final Node node;
	
	/**
	 * The names of the arguments to this function.
	 */
	private final String[] variableNames;
	
	/**
	 * Constructs a new node for defining an immutable, anonymous function with
	 * no arguments.
	 * 
	 * @param node the body of this function
	 * @throws IllegalArgumentException if {@code node} is incomplete or not
	 *         strongly typed (i.e., {@code node.isValid()} returns
	 *         {@code false})
	 */
	public Lambda(Node node) {
		this(node, new String[0], new Class<?>[0]);
	}
	
	/**
	 * Constructs a new node for defining an immutable, anonymous function with
	 * one argument.
	 * 
	 * @param node the body of this function
	 * @param variableName the name of the argument
	 * @param variableType the type of the argument
	 * @throws IllegalArgumentException if {@code node} is incomplete or not
	 *         strongly typed (i.e., {@code node.isValid()} returns
	 *         {@code false})
	 */
	public Lambda(Node node, String variableName, Class<?> variableType) {
		this(node, new String[] { variableName },
				new Class<?>[] { variableType });
	}
	
	/**
	 * Constructs a new node for defining an immutable, anonymous function with
	 * two arguments.
	 * 
	 * @param node the body of this function
	 * @param name1 the name of the first argument
	 * @param type1 the type of the first argument
	 * @param name2 the name of the second argument
	 * @param type2 the type of the second argument
	 * @throws IllegalArgumentException if {@code node} is incomplete or not
	 *         strongly typed (i.e., {@code node.isValid()} returns
	 *         {@code false})
	 */
	public Lambda(Node node, String name1, Class<?> type1, 
			String name2, Class<?> type2) {
		this(node, new String[] { name1, name2 },
				new Class<?>[] { type1, type2 });
	}
	
	/**
	 * Constructs a new node for defining an immutable, anonymous function with
	 * a user-defined number of arguments.
	 * 
	 * @param node the body of this function
	 * @param variableNames the names of the arguments to this function
	 * @param variableTypes the types of the arguments to this function
	 * @throws IllegalArgumentException if {@code node} is incomplete or not
	 *         strongly typed (i.e., {@code node.isValid()} returns
	 *         {@code false})
	 */
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

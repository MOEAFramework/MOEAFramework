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
 * The node for calling a named function.
 * 
 * @see Define
 */
public class Call extends Node {
	
	/**
	 * The name of the function to call.
	 */
	private final String functionName;
	
	/**
	 * The names of the variables to the function.
	 */
	private final String[] variableNames;
	
	/**
	 * The types of the variables to the function.
	 */
	private final Class<?>[] variableTypes;
	
	/**
	 * Constructs a new node for calling a function that takes no arguments.
	 * 
	 * @param functionName the name of the function
	 * @param returnType the return type of the function
	 */
	public Call(String functionName, Class<?> returnType) {
		this(functionName, returnType, new String[0], new Class<?>[0]);
	}
	
	/**
	 * Constructs a new node for calling a function that takes one argument.
	 * 
	 * @param functionName the name of the function
	 * @param returnType the return type of the function
	 * @param variableName the name of the argument
	 * @param variableType the type of the argument
	 */
	public Call(String functionName, Class<?> returnType, String variableName,
			Class<?> variableType) {
		this(functionName, returnType, new String[] { variableName },
				new Class<?>[] { variableType });
	}
	
	/**
	 * Constructs a new node for calling a function that takes two arguments.
	 * 
	 * @param functionName the name of the function
	 * @param returnType the return type of the function
	 * @param name1 the name of the first argument
	 * @param type1 the type of the first argument
	 * @param name2 the name of the second argument
	 * @param type2 the type of the second argument
	 */
	public Call(String functionName, Class<?> returnType, String name1,
			Class<?> type1, String name2, Class<?> type2) {
		this(functionName, returnType, new String[] { name1, name2 },
				new Class<?>[] { type1, type2 });
	}
	
	/**
	 * Constructs a new node for calling the function as defined.
	 * 
	 * @param function the function
	 */
	public Call(Define function) {
		this(function.getFunctionName(), function.getArgumentType(0),
				function.getVariableNames(), function.getVariableTypes());
	}
	
	/**
	 * Constructs a new node for calling a function that takes a
	 * user-defined number of arguments.
	 * 
	 * @param functionName the name of the function
	 * @param returnType the return type of the function
	 * @param variableNames the names of the arguments to the function
	 * @param variableTypes the types of the arguments to the function
	 */
	public Call(String functionName, Class<?> returnType,
			String[] variableNames, Class<?>[] variableTypes) {
		super(returnType, variableTypes);
		this.functionName = functionName;
		this.variableNames = variableNames;
		this.variableTypes = variableTypes;
	}

	/**
	 * Returns the name of this function.
	 * 
	 * @return the name of this function
	 */
	public String getFunctionName() {
		return functionName;
	}

	/**
	 * Returns the names of the arguments to this function.
	 * 
	 * @return the names of the arguments to this function
	 */
	public String[] getVariableNames() {
		return variableNames;
	}

	/**
	 * Returns the types of the arguments to this function.
	 * 
	 * @return the types of the arguments to this function
	 */
	public Class<?>[] getVariableTypes() {
		return variableTypes;
	}

	@Override
	public Node copyNode() {
		return new Call(functionName, getReturnType(), variableNames,
				variableTypes);
	}

	@Override
	public Object evaluate(Environment environment) {
		Environment closure = new Environment(environment);
		
		for (int i = 0; i < getNumberOfArguments(); i++) {
			closure.set(variableNames[i], getArgument(i).evaluate(environment));
		}
		
		Node functionBody = environment.get(Node.class, functionName);
		return functionBody.evaluate(closure);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(getClass().getSimpleName());
		sb.append(' ');
		sb.append(getFunctionName());
		
		for (int i = 0; i < getNumberOfArguments(); i++) {
			sb.append(' ');
			sb.append(getArgument(0).toString());
		}
		
		sb.append(')');
		return sb.toString();
	}

}

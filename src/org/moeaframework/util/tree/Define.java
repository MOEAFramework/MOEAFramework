/* Copyright 2009-2012 David Hadka
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

public class Define extends Node {
	
	private final String functionName;
	
	private final String[] variableNames;
	
	private final Class<?>[] variableTypes;
	
	public Define(String functionName, Class<?> returnType, String variableName, Class<?> variableType) {
		this(functionName, returnType, new String[] { variableName }, new Class<?>[] { variableType });
	}
	
	public Define(String functionName, Class<?> returnType, String variable1, Class<?> type1, String variable2, Class<?> type2) {
		this(functionName, returnType, new String[] { variable1, variable2 }, new Class<?>[] { type1, type2 });
	}
	
	public Define(String functionName, Class<?> returnType, String[] variableNames, Class<?>[] variableTypes) {
		super(Void.class, returnType);
		this.functionName = functionName;
		this.variableNames = variableNames;
		this.variableTypes = variableTypes;
	}

	public String getFunctionName() {
		return functionName;
	}

	public String[] getVariableNames() {
		return variableNames;
	}

	public Class<?>[] getVariableTypes() {
		return variableTypes;
	}

	@Override
	public Node copyNode() {
		return new Define(functionName, getArgumentType(0), variableNames, variableTypes);
	}

	@Override
	public Void evaluate(Environment environment) {
		environment.set(functionName, getArgument(0));
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(getClass().getSimpleName());
		sb.append(' ');
		sb.append(getFunctionName());
		sb.append(" (");
		
		for (int i = 0; i < variableNames.length; i++) {
			if (i > 0) {
				sb.append(' ');
			}
			
			sb.append(variableNames[i]);
		}
		
		sb.append(") ");
		sb.append(getArgument(0).toString());
		sb.append(')');
		return sb.toString();
	}

}

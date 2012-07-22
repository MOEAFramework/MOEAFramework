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

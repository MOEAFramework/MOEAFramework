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
package org.moeaframework.core.variable;

import org.moeaframework.core.Variable;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;
import org.moeaframework.util.tree.Rules;

/**
 * 
 * <b>Note: Although {@code Program} extends {@link Node}, the {@code Program}
 * object must never be altered by the optimization algorithm.</b>  Only
 * its arguments can undergo variation.
 */
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

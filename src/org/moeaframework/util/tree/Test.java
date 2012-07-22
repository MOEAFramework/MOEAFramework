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


public class Test {
	
	public static void main(String[] args) {
		Rules nodeSet = new Rules();
		nodeSet.populateWithDefaults();
		
		Define function = new Define("eval", Number.class, "x", Number.class, "y", Number.class);
		
		//ensure the function and its arguments can be used as nodes
		nodeSet.add(new Get(Number.class, "x"));
		nodeSet.add(new Get(Number.class, "y"));
		nodeSet.add(new Call(function));
		
		Node base = new Sequence(Number.class)
				.setArgument(0, function);

		Node node = nodeSet.buildTreeFull(base, 5);
		System.out.println(node);
		System.out.println(node.isValid());
		System.out.println(node.evaluate(new Environment()));
	}

}

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


public class TestFactorial {
	
	public static void main(String[] args) {
		/*
		 * fact(n):
		 *   if (n <= 1) return 1;
		 *   else return n * fact(n-1);
		 */
		Node factorial = new Define("factorial", Number.class, "n", Number.class).setArgument(0, 
				new IfElse(Number.class)
						.setArgument(0, new LessThanOrEqual()
								.setArgument(0, new Get(Number.class, "n"))
								.setArgument(1, new Constant(1)))
						.setArgument(1, new Constant(1))
						.setArgument(2, new Multiply()
								.setArgument(0, new Get(Number.class, "n"))
								.setArgument(1, new Call("factorial", Number.class, "n", Number.class)
										.setArgument(0, new Subtract()
												.setArgument(0, new Get(Number.class, "n"))
												.setArgument(1, new Constant(1))))));
		
		Node program = new Sequence()
				.setArgument(0, factorial)
				.setArgument(1, new Call("factorial", Number.class, "n", Number.class)
						.setArgument(0, new Constant(5)));
		
		Environment environment = new Environment();
		System.out.println(program.isValid());
		System.out.println(program.evaluate(environment));
	}

}

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

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.PRNG;

/**
 * The rules defining the program syntax.  At a minimum, the rules must define
 * the program return type and the set of nodes which can appear in the program.
 * <p>
 * It is also possible to define program scaffolding, which defines the fixed
 * initial structure of the program that is not modified by any variation
 * operators.  For example, this can be used to define function prototypes
 * (similar to automatically defined functions), so that the function appears
 * in all programs.  Any undefined arguments (i.e., {@code null}) to a node
 * will be filled by the genetic programming algorithm.
 */
public class Rules {
	
	/**
	 * The return type of all programs produced using these rules.
	 */
	private Class<?> returnType;
	
	/**
	 * The program scaffolding; or {@code null} if the program has no defined
	 * scaffolding.
	 */
	private Node scaffolding;
	
	/**
	 * The maximum depth of the expression trees produced by any initialization
	 * routine.
	 */
	private int maxInitializationDepth;
	
	/**
	 * The maximum depth of the expression trees produced by any variation
	 * operator.
	 */
	private int maxVariationDepth;
	
	/**
	 * The probability of crossover being applied to a function (non-terminal)
	 * node.
	 */
	private double functionCrossoverProbability;
	
	/**
	 * The list of all available nodes that may appear in the expression tree;
	 * nodes that are exclusive to program scaffolding need not be listed.
	 */
	private List<Node> availableNodes;
	
	/**
	 * Constructs a new set of rules for defining program syntax.
	 */
	public Rules() {
		super();
		
		returnType = Void.class;
		maxInitializationDepth = 5;
		maxVariationDepth = 10;
		functionCrossoverProbability = 0.5;
		availableNodes = new ArrayList<Node>();
	}
	
	/**
	 * Returns the return type of all programs produced using these rules.
	 * 
	 * @return the return type of all programs produced using these rules
	 */
	public Class<?> getReturnType() {
		return returnType;
	}

	/**
	 * Sets the return type of all programs produced using these rules.
	 * 
	 * @param returnType the return type of all programs produced using these
	 *        rules
	 */
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	/**
	 * Returns the program scaffolding; or {@code null} if the program has no
	 * defined scaffolding.
	 *  
	 * @return the program scaffolding; or {@code null} if the program has no
	 *         defined scaffolding
	 */
	public Node getScaffolding() {
		return scaffolding;
	}

	/**
	 * Sets the program scaffolding.  Setting the program scaffolding
	 * automatically sets the return type.
	 * 
	 * @param scaffolding the program scaffolding
	 */
	public void setScaffolding(Node scaffolding) {
		this.scaffolding = scaffolding;
		
		scaffolding.setFixedTree(true);
		setReturnType(scaffolding.getReturnType());
	}

	/**
	 * Returns the maximum depth of the expression trees produced by any
	 * initialization routine.
	 * 
	 * @return the maximum depth of the expression trees produced by any
	 *         initialization routine
	 */
	public int getMaxInitializationDepth() {
		return maxInitializationDepth;
	}

	/**
	 * Sets the maximum depth of the expression trees produced by any
	 * initialization routine.
	 * 
	 * @param maxInitializationDepth  the maximum depth of the expression trees
	 *        produced by any initialization routine
	 */
	public void setMaxInitializationDepth(int maxInitializationDepth) {
		this.maxInitializationDepth = maxInitializationDepth;
	}

	/**
	 * Returns the maximum depth of the expression trees produced by any
	 * variation operator.
	 * 
	 * @return the maximum depth of the expression trees produced by any
	 *         variation operator
	 */
	public int getMaxVariationDepth() {
		return maxVariationDepth;
	}

	/**
	 * Sets the maximum depth of the expression trees produced by any
	 * variation operator.
	 * 
	 * @param maxVariationDepth the maximum depth of the expression trees
	 *        produced by any variation operator
	 */
	public void setMaxVariationDepth(int maxVariationDepth) {
		this.maxVariationDepth = maxVariationDepth;
	}

	/**
	 * Returns the probability of crossover being applied to a function
	 * (non-terminal) node.
	 * 
	 * @return the probability of crossover being applied to a function
	 *         (non-terminal) node
	 */
	public double getFunctionCrossoverProbability() {
		return functionCrossoverProbability;
	}

	/**
	 * Sets the probability of crossover being applied to a function
	 * (non-terminal) node.  To set an equal probability of crossing all nodes,
	 * set the function crossover probability to
	 * {@code (No. of Functions) / (No. of Functions + No. of Terminals)}.
	 * 
	 * @param functionCrossoverProbability the probability of crossover being
	 *        applied to a function (non-terminal) node
	 */
	public void setFunctionCrossoverProbability(
			double functionCrossoverProbability) {
		this.functionCrossoverProbability = functionCrossoverProbability;
	}

	/**
	 * Adds a node that can appear in programs produced using these rules.
	 * 
	 * @param node the node that can appear in programs produced using these
	 *        rules
	 */
	public void add(Node node) {
		availableNodes.add(node);
	}
	
	/**
	 * Allows the default logic nodes to appear in programs produced using
	 * these rules.  This includes {@link And}, {@link Or}, {@link Not},
	 * {@link Equals}, {@link GreaterThan}, {@link LessThan}, 
	 * {@link GreaterThanOrEqual}, {@link LessThanOrEqual}, and constants
	 * {@code true} and {@code false}.
	 */
	public void populateWithLogic() {
		add(new And());
		add(new Or());
		add(new Not());
		add(new Equals());
		add(new GreaterThan());
		add(new LessThan());
		add(new GreaterThanOrEqual());
		add(new LessThanOrEqual());
		add(new Constant(true));
		add(new Constant(false));
	}
	
	/**
	 * Allows the default arithmetic nodes to appear in programs produced using
	 * these rules.  This includes {@link Add}, {@link Subtract},
	 * {@link Multiply}, {@link Divide}, {@link Modulus}, {@link Floor},
	 * {@link Ceil}, {@link Round}, {@link Max}, {@link Min}, {@link Power},
	 * {@link Square}, {@link SquareRoot}, {@link Abs}, {@link Log},
	 * {@link Log10}, {@link Exp}, and {@link Sign}.
	 */
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
	
	/**
	 * Allows the default trigonometric nodes to appear in programs produced
	 * using these rules.  This includes {@link Sin}, {@link Cos}, {@link Tan},
	 * {@link Asin}, {@link Acos}, {@link Atan}, {@link Sinh}, {@link Cosh},
	 * {@link Tanh}, {@link Asinh}, {@link Acosh}, and {@link Atanh}.
	 */
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
		add(new Asinh());
		add(new Acosh());
		add(new Atanh());
	}
	
	/**
	 * Allows the default control nodes to appear in programs produced using
	 * these rules.  This includes {@link IfElse}, {@link Sequence}, and
	 * {@link NOP}.  Several control nodes are not included in the defaults,
	 * such as {@link For} and {@link While}.  These are not included as they
	 * can easily result in infinite loops.  These other control nodes can be
	 * manually added if needed.
	 */
	public void populateWithControl() {
		add(new IfElse());
		add(new IfElse(Number.class));
		add(new Sequence());
		add(new NOP());
	}
	
	/**
	 * Allows the default constant nodes to appear in programs produced using
	 * these rules.  This includes the constants {@code 0}, {@code 1},
	 * {@code 2}, {@code 10}, {@code -1}, {@code Math.E}, and {@code Math.PI}.
	 */
	public void populateWithConstants() {
		add(new Constant(0));
		add(new Constant(1));
		add(new Constant(2));
		add(new Constant(10));
		add(new Constant(-1));
		add(new Constant(Math.E));
		add(new Constant(Math.PI));
	}
	
	/**
	 * Allows all default nodes to appear in programs.  See
	 * {@link #populateWithLogic()}, {@link #populateWithArithmetic()},
	 * {@link #populateWithTrig()}, {@link #populateWithControl()}, and
	 * {@link #populateWithConstants()} for details.
	 */
	public void populateWithDefaults() {
		populateWithLogic();
		populateWithArithmetic();
		populateWithTrig();
		populateWithControl();
		populateWithConstants();
	}
	
	/**
	 * Returns the list of all nodes which can appear in programs produced
	 * using these rules.
	 * 
	 * @return the list of all nodes which can appear in programs produced
	 *         using these rules
	 */
	public List<Node> getAvailableNodes() {
		return availableNodes;
	}
	
	/**
	 * Returns the list of all nodes in the tree rooted at the specified node
	 * with the given return type.  This method ensures the crossover remains
	 * strongly-typed.
	 * 
	 * @param node the root of the tree
	 * @param type the required return type
	 * @return the list of all nodes in the tree rooted at the specified node
	 *         with the given return type
	 */
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

	/**
	 * Returns the list of all available mutations to the given node.  This
	 * method ensures the mutation remains strongly-typed.
	 * 
	 * @param node the node to be mutated
	 * @return the list of all available mutations to the given node
	 */
	public List<Node> listAvailableMutations(Node node) {
		List<Node> result = new ArrayList<Node>();
		
		for (Node mutation : availableNodes) {
			if (isMutationCompatible(node, mutation)) {
				result.add(mutation);
			}
		}
		
		return result;
	}
	
	/**
	 * Returns {@code true} if the original node can be replaced via point
	 * mutation with the given mutation node and satisfy type safety;
	 * {@code false} otherwise.
	 * 
	 * @param original the original node
	 * @param mutation the mutation node
	 * @return {@code true} if the original node can be replaced via point
	 *         mutation with the given mutation node and satisfy type safety;
	 *         {@code false} otherwise
	 */
	protected boolean isMutationCompatible(Node original, Node mutation) {
		if (!original.getReturnType().isAssignableFrom(
				mutation.getReturnType())) {
			return false;
		}
		
		if (original.getNumberOfArguments() != 
				mutation.getNumberOfArguments()) {
			return false;
		}
		
		for (int i = 0; i < original.getNumberOfArguments(); i++) {
			if (!original.getArgumentType(i).isAssignableFrom(
					mutation.getArgumentType(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the list of all available nodes with the given return type.
	 * 
	 * @param type the required return type
	 * @return the list of all available nodes with the given return type
	 */
	public List<Node> listAvailableNodes(Class<?> type) {
		List<Node> result = new ArrayList<Node>();
		
		for (Node node : availableNodes) {
			if (type.isAssignableFrom(node.getReturnType())) {
				result.add(node);
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the list of all available terminal nodes with the given return
	 * type.
	 * 
	 * @param type the required return type
	 * @return the list of all available terminal nodes with the given return
	 *         type
	 */
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
	
	/**
	 * Returns the list of all available function (non-terminal) nodes with the
	 * given return type.
	 * 
	 * @param type the required return type
	 * @return the list of all available function (non-terminal) nodes with the
	 *         given return type
	 */
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
	
	/**
	 * Generates an expression tree with the given return type using the
	 * <i>full</i> initialization method.  This method builds the tree so every
	 * leaf node is at the specified depth.
	 * 
	 * @param type the required return type
	 * @param depth the required depth of each leaf node in the tree
	 * @return an expression tree with the given return type using the
	 *         <i>full</i> initialization method
	 */
	public Node buildTreeFull(Class<?> type, int depth) {
		if (depth == 0) {
			return PRNG.nextItem(listAvailableTerminals(type)).copyNode();
		} else {
			Node node = PRNG.nextItem(listAvailableFunctions(type)).copyNode();
			
			for (int i = 0; i < node.getNumberOfArguments(); i++) {
				node.setArgument(i, buildTreeFull(node.getArgumentType(i),
						depth-1));
			}
			
			return node;
		}
	}
	
	/**
	 * Generates an expression tree with the given return type using the
	 * <i>grow</i> initialization method.  This method builds the tree such
	 * that the depth of each leaf is at most the specified maximum depth.
	 * 
	 * @param type the required return type
	 * @param depth the maximum depth of each leaf node in the tree
	 * @return an expression tree with the given return type using the
	 *         <i>grow</i> initialization method
	 */
	public Node buildTreeGrow(Class<?> type, int depth) {
		if (depth == 0) {
			return PRNG.nextItem(listAvailableTerminals(type)).copyNode();
		} else {
			Node node = PRNG.nextItem(listAvailableNodes(type)).copyNode();
			
			for (int i = 0; i < node.getNumberOfArguments(); i++) {
				node.setArgument(i, buildTreeGrow(node.getArgumentType(i),
						depth-1));
			}
			
			return node;
		}
	}
	
	/**
	 * Generates an expression tree with the given scaffolding using the
	 * <i>full</i> initialization method.  This method builds the tree so every
	 * leaf node is at the specified depth.
	 * 
	 * @param node the initial scaffolding for the tree
	 * @param depth the required depth of each leaf node in the tree
	 * @return an expression tree with the given scaffolding using the
	 *         <i>full</i> initialization method
	 */
	public Node buildTreeFull(Node node, int depth) {
		if (depth == 0) {
			return node.copyNode();
		} else {
			Node copy = node.copyNode();
			
			for (int i = 0; i < node.getNumberOfArguments(); i++) {
				if (node.getArgument(i) == null) {
					copy.setArgument(i, buildTreeFull(node.getArgumentType(i),
							depth-1));
				} else {
					copy.setArgument(i, buildTreeFull(node.getArgument(i),
							depth-1));
				}
			}
			
			return copy;
		}
	}
	
	/**
	 * Generates an expression tree with the given scaffolding using the
	 * <i>grow</i> initialization method.  This method builds the tree such
	 * that the depth of each leaf is at most the specified maximum depth.
	 * 
	 * @param node the initial scaffolding for the tree
	 * @param depth the maximum depth of each leaf node in the tree
	 * @return an expression tree with the given scaffolding using the
	 *         <i>grow</i> initialization method
	 */
	public Node buildTreeGrow(Node node, int depth) {
		if (depth == 0) {
			return node.copyNode();
		} else {
			Node copy = node.copyNode();
			
			for (int i = 0; i < node.getNumberOfArguments(); i++) {
				if (node.getArgument(i) == null) {
					copy.setArgument(i, buildTreeGrow(node.getArgumentType(i),
							depth-1));
				} else {
					copy.setArgument(i, buildTreeGrow(node.getArgument(i),
							depth-1));
				}
			}
			
			return copy;
		}
	}

}

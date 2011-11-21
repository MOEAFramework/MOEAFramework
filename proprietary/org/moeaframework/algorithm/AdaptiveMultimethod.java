/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.util.ArrayMath;

/**
 * Auto-adaptive multi-method recombination operator. Applies operators with
 * probabilities proportional to the number of offspring produced by each
 * operator in the archive.
 */
public class AdaptiveMultimethod implements Variation, Selection {
	
	/**
	 * The attribute for the operator index.
	 */
	public static final String OPERATOR_ATTRIBUTE = "operator";
	
	public static final int NO_OPERATOR_SELECTED = -1;
	
	/**
	 * The selection operators for each variation operator.
	 */
	private List<Selection> selectionOperators;

	/**
	 * The available variation operators.
	 */
	private List<Variation> variationOperators;

	/**
	 * The probabilities for applying each operator.
	 */
	protected double[] probabilities;
	
	protected int nextOperatorIndex;

	/**
	 * The number of invocations of the {@code evolve} method when the
	 * probabilities were last updated.
	 */
	private int lastUpdate;

	/**
	 * The archive used to update the probabilities.
	 */
	private final Population archive;

	/**
	 * The number of invocations of the {@code evolve} method between updating
	 * the probabilities.
	 */
	private static final int updateWindow = 100;

	/**
	 * Constructs an auto-adaptive multi-method recombination operator with the
	 * specified archive for updating probabilities.
	 * 
	 * @param archive the archive used to update the probabilities
	 */
	public AdaptiveMultimethod(Population archive) {
		super();
		this.archive = archive;

		selectionOperators = new ArrayList<Selection>();
		variationOperators = new ArrayList<Variation>();
		clearNextOperator();
	}

	/**
	 * Adds an operator to be used in the auto-adaptive multi-method
	 * recombination.
	 * 
	 * @param selection the selection operator
	 * @param variation the variation operator
	 */
	public void add(Selection selection, Variation variation) {
		selectionOperators.add(selection);
		variationOperators.add(variation);
	}

	/**
	 * Returns the number of available operators.
	 * 
	 * @return the number of available operators
	 */
	public int getNumberOfOperators() {
		return variationOperators.size();
	}

	/**
	 * Returns the variation operator at the specified index.
	 * 
	 * @param index the index of the operator to be returned
	 * @return the variation operator at the specified index
	 */
	public Variation getVariationOperator(int index) {
		return variationOperators.get(index);
	}
	
	/**
	 * Returns the selection operator at the specified index.
	 * 
	 * @param index the index of the operator to be returned
	 * @return the selection operator at the specified index
	 */
	public Selection getSelectionOperator(int index) {
		return selectionOperators.get(index);
	}

	/**
	 * Returns the probability that the operator at the specified index is
	 * applied.
	 * 
	 * @param index the index of the operator whose probability is returned
	 * @return the probability that the operator at the specified index is
	 *         applied
	 */
	public double getOperatorProbability(int index) {
		if (probabilities == null) {
			lastUpdate = 0;
			probabilities = getOperatorProbabilities();
		}

		return probabilities[index];
	}

	/**
	 * Returns the array of probabilities of applying each operator.
	 * 
	 * @return the array of probabilities of applying each operator
	 */
	private double[] getOperatorProbabilities() {
		int[] count = new int[getNumberOfOperators()];
		Arrays.fill(count, 1);

		for (Solution solution : archive) {
			if (solution.hasAttribute(OPERATOR_ATTRIBUTE)) {
				count[(Integer)solution.getAttribute(OPERATOR_ATTRIBUTE)]++;
			}
		}

		int sum = ArrayMath.sum(count);
		double[] probabilities = new double[count.length];

		for (int i = 0; i < count.length; i++) {
			probabilities[i] = count[i] / (double)sum;
		}

		return probabilities;
	}
	
	/**
	 * Clears the {@code nextOperatorIndex} value, so that the next call to
	 * {@link #getNextOperator()} returns a new, randomly-selected operator.
	 */
	protected void clearNextOperator() {
		nextOperatorIndex = NO_OPERATOR_SELECTED;
	}

	/**
	 * Sets the {@code nextOperatorIndex} value to a randomly-selected operator
	 * using the current operator probabilities.
	 * 
	 * @return the {@code nextOperatorIndex} value
	 */
	protected int getNextOperator() {
		if (nextOperatorIndex != NO_OPERATOR_SELECTED) {
			return nextOperatorIndex;
		}
		
		if (getNumberOfOperators() <= 0) {
			throw new IllegalStateException("no operators added");
		}
		
		lastUpdate++;

		if ((lastUpdate > updateWindow) || (probabilities == null)) {
			lastUpdate = 0;
			probabilities = getOperatorProbabilities();
		}

		double rand = PRNG.nextDouble();
		double sum = 0.0;

		for (int i = 0; i < getNumberOfOperators(); i++) {
			sum += probabilities[i];

			if (sum > rand) {
				nextOperatorIndex = i;
				return nextOperatorIndex;
			}
		}

		throw new IllegalStateException();
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		int index = getNextOperator();
		Variation variation = variationOperators.get(index);
		Solution[] result = variation.evolve(Arrays.copyOf(parents, 
				parents.length));

		for (int i = 0; i < result.length; i++) {
			result[i].setAttribute(OPERATOR_ATTRIBUTE, index);
		}
		
		clearNextOperator();

		return result;
	}

	@Override
	public int getArity() {
		return variationOperators.get(getNextOperator()).getArity();
	}

	@Override
	public Solution[] select(int arity, Population population) {
		return selectionOperators.get(getNextOperator()).select(arity, 
				population);
	}

}

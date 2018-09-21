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
package org.moeaframework.core.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

/**
 * Auto-adaptive multi-method recombination operator. Applies operators with
 * probabilities proportional to the number of offspring produced by each
 * operator in the archive.
 */
public class AdaptiveMultimethodVariation implements Variation {
	
	/**
	 * The attribute for the operator index.
	 */
	public static final String OPERATOR_ATTRIBUTE = "operator";

	/**
	 * The available operators.
	 */
	private List<Variation> operators;

	/**
	 * The probabilities for applying each operator.
	 */
	private double[] probabilities;

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
	 * the operator selection probabilities.
	 */
	private static final int UPDATE_WINDOW = 100;

	/**
	 * Constructs an auto-adaptive multi-method recombination operator with the
	 * specified archive for updating probabilities.
	 * 
	 * @param archive the archive used to update the probabilities
	 */
	public AdaptiveMultimethodVariation(Population archive) {
		this.archive = archive;

		operators = new ArrayList<Variation>();
	}
	
	/**
	 * Returns the number of invocations of the {@code evolve} method between
	 * updating the operator selection probabilities.
	 * 
	 * @return the number of invocations of the {@code evolve} method between
	 *         updating the operator selection probabilities
	 */
	public int getUpdateWindow() {
		return UPDATE_WINDOW;
	}

	/**
	 * Adds an operator to be used in the auto-adaptive multi-method
	 * recombination.
	 * 
	 * @param operator the operator
	 */
	public void addOperator(Variation operator) {
		operators.add(operator);
	}

	/**
	 * Returns the number of available operators.
	 * 
	 * @return the number of available operators
	 */
	public int getNumberOfOperators() {
		return operators.size();
	}

	/**
	 * Returns the operator at the specified index.
	 * 
	 * @param index the index of the operator to be returned
	 * @return the operator at the specified index
	 */
	public Variation getOperator(int index) {
		return operators.get(index);
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
	protected double[] getOperatorProbabilities() {
		double[] count = new double[operators.size()];
		Arrays.fill(count, 1.0);

		for (Solution solution : archive) {
			if (solution.hasAttribute(OPERATOR_ATTRIBUTE)) {
				count[(Integer)solution.getAttribute(OPERATOR_ATTRIBUTE)]++;
			}
		}

		double sum = StatUtils.sum(count);
		double[] probabilities = new double[count.length];

		for (int i = 0; i < count.length; i++) {
			probabilities[i] = count[i] / sum;
		}

		return probabilities;
	}

	/**
	 * Returns the index of one of the available operators randomly selected
	 * using the probabilities.
	 * 
	 * @return the index of one of the available operators randomly selected
	 *         using the probabilities
	 */
	protected int selectOperator() {
		lastUpdate++;

		if ((lastUpdate >= UPDATE_WINDOW) || (probabilities == null)) {
			lastUpdate = 0;
			probabilities = getOperatorProbabilities();
		}

		double rand = PRNG.nextDouble();
		double sum = 0.0;

		for (int i = 0; i < operators.size(); i++) {
			sum += probabilities[i];

			if (sum > rand) {
				return i;
			}
		}

		throw new IllegalStateException();
	}

	//TODO: getArity should only request the # of parents required for the next
	//invocation of evolve, but this will require all uses to not cache the
	//variation operator arity.
	@Override
	public Solution[] evolve(Solution[] parents) {
		if (operators.isEmpty()) {
			throw new IllegalStateException("no operators added");
		}

		int index = selectOperator();
		Variation operator = operators.get(index);
		Solution[] result = operator.evolve(Arrays.copyOf(parents, 
				operator.getArity()));

		for (int i = 0; i < result.length; i++) {
			result[i].setAttribute(OPERATOR_ATTRIBUTE, index);
		}

		return result;
	}

	@Override
	public int getArity() {
		if (operators.isEmpty()) {
			throw new IllegalStateException("no operators added");
		}

		int arity = 0;
		for (Variation operator : operators) {
			arity = Math.max(arity, operator.getArity());
		}

		return arity;
	}

//	/**
//	 * Procedure for adjusting an array of probabilities to ensure the
//	 * probabilities are at least {@code minimum}.
//	 * 
//	 * @param probabilities the array of probabilities
//	 * @param minimum the minimum probability
//	 */
//	public static void correctProbabilities(double[] probabilities,
//			double minimum) {
//		double diff = 0.0;
//		double allowance = 0.0;
//
//		for (int i = 0; i < probabilities.length; i++) {
//			if (probabilities[i] <= minimum) {
//				diff += minimum - probabilities[i];
//				probabilities[i] = minimum;
//			} else {
//				allowance += probabilities[i] - minimum;
//			}
//		}
//
//		for (int i = 0; i < probabilities.length; i++) {
//			if (probabilities[i] > minimum) {
//				probabilities[i] -= diff
//						* ((probabilities[i] - minimum) / allowance);
//			}
//		}
//	}

}

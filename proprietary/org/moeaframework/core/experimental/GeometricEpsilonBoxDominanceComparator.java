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
package org.moeaframework.core.experimental;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.EpsilonBoxDominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

/**
 * Generalization of the multiplicative epsilon-box dominance relation. A
 * geometric sequence starting with the epsilon value and multiplied by the
 * growth rate forms the side-length of the epsilon-boxes. Set {@code growthRate
 * = 1 + epsilon} to get the multiplicative epsilon-dominance relation.
 */
public class GeometricEpsilonBoxDominanceComparator extends
		EpsilonBoxDominanceComparator {

	private static final long serialVersionUID = 2606206188208117024L;

	/**
	 * The Pareto dominance comparator used when two solutions are within the
	 * same epsilon-box.
	 */
	private static final ParetoDominanceComparator comparator = new ParetoDominanceComparator();

	/**
	 * The growth rates used by this comparator.
	 */
	private final double[] growthRates;

	/**
	 * Constructs a geometric epsilon-box dominance comparator with the 
	 * specified epsilon value and growth rate.
	 * 
	 * @param epsilon the epsilon value used by this comparator
	 * @param growthRate the growth rate used by this comparator
	 */
	public GeometricEpsilonBoxDominanceComparator(double epsilon,
			double growthRate) {
		super(epsilon);

		if (growthRate <= 1.0) {
			throw new IllegalArgumentException(
					"growth rate must be greater than 1");
		}

		growthRates = new double[] { growthRate };
	}

	/**
	 * Constructs a geometric epsilon-box dominance comparator with the 
	 * specified epsilon values and growth rates.
	 * 
	 * @param epsilons the epsilon values used by this comparator
	 * @param growthRates the growth rates used by this comparator
	 */
	public GeometricEpsilonBoxDominanceComparator(double[] epsilons,
			double[] growthRates) {
		super(epsilons);

		for (double growthRate : growthRates) {
			if (growthRate <= 1.0) {
				throw new IllegalArgumentException(
						"growth rate must be greater than 1");
			}
		}
		this.growthRates = growthRates;
	}

	/**
	 * Returns the growth rates used by this comparator for the specified
	 * objective. For cases where {@code (objective >= growthRates.length) * },
	 * the last growth rate in this array is used {@code
	 * (growthRates[growthRates.length-1])}.
	 * 
	 * @return the growth rate used by this comparator for the specified
	 *         objective
	 */
	public double getGrowthRate(int objective) {
		return growthRates[objective < growthRates.length ? objective
				: growthRates.length - 1];
	}

	/**
	 * Returns the epsilon-box index for the specified objective index and
	 * objective value.
	 * 
	 * @param index the objective index
	 * @param value the objective value
	 * @return the epsilon-box index for the specified objective index and
	 *         objective value
	 */
	private int index(int index, double value) {
		double epsilon = getEpsilon(index);
		double growthRate = getGrowthRate(index);

		return (int)Math.floor(Math.log(1.0 - value * (1.0 - growthRate)
				/ epsilon)
				/ Math.log(growthRate));
	}

	@Override
	public int compare(Solution solution1, Solution solution2) {
		setSameBox(false);

		boolean dominate1 = false;
		boolean dominate2 = false;

		for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
			int index1 = index(i, solution1.getObjective(i));
			int index2 = index(i, solution2.getObjective(i));

			if (index1 < index2) {
				dominate1 = true;

				if (dominate2) {
					return 0;
				}
			} else if (index1 > index2) {
				dominate2 = true;

				if (dominate1) {
					return 0;
				}
			}
		}

		if (!dominate1 && !dominate2) {
			setSameBox(true);

			if (comparator.compare(solution1, solution2) < 0) {
				return -1;
			} else {
				return 1;
			}
		} else if (dominate1 && dominate2) {
			return 0;
		} else if (dominate1) {
			return -1;
		} else {
			return 1;
		}
	}

}

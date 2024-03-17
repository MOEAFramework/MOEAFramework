/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.core.comparator;

import org.moeaframework.core.Epsilons;
import org.moeaframework.core.Solution;

/**
 * Compares two solutions using the additive &epsilon;-box objective comparator.  This dominance relation divides
 * objective space into boxes with side-length &epsilon; and specifies that only one solution may exist within the
 * same box.  If two solutions were to reside in the same box, the solution closer to the box's minimum corner.
 * <p>
 * In general, the {@link EpsilonBoxDominanceComparator} should be used instead as it also incorporates constraint
 * violation checks.
 * <p>
 * References:
 * <ol>
 *   <li>Laumanns et al. "Combining Convergence and Diversity in Evolutionary Multi-Objective Optimization."
 *       Evolutionary Computation. 10(3). 2002
 *   <li>Deb et al. "A Fast Multi-Objective Evolutionary Algorithm for Finding Well-Spread Pareto-Optimal Solutions."
 *       KanGAL Report No 2003002. Feb 2003.
 * </ol>
 */
public class EpsilonBoxObjectiveComparator implements DominanceComparator {

	/**
	 * {@code true} if the the two solutions passed to the previous invocation of {@code compare} existed within the
	 * same &epsilon;-box; {@code false} otherwise.
	 */
	protected boolean isSameBox;

	/**
	 * The &epsilon; values used by this comparator.
	 */
	protected final Epsilons epsilons;

	/**
	 * Constructs an additive &epsilon;-box dominance comparator with the specified &epsilon; value.
	 * 
	 * @param epsilon the &epsilon; value used by this comparator
	 */
	public EpsilonBoxObjectiveComparator(double epsilon) {
		this(new Epsilons(epsilon));
	}

	/**
	 * Constructs an additive &epsilon;-box dominance comparator with the specified &epsilon; values.
	 * 
	 * @param epsilons the &epsilon; values used by this comparator
	 */
	public EpsilonBoxObjectiveComparator(double[] epsilons) {
		this(new Epsilons(epsilons));
	}
	
	/**
	 * Constructs an additive &epsilon;-box dominance comparator with the specified &epsilon; values.
	 * 
	 * @param epsilons the &epsilon; values used by this comparator
	 */
	public EpsilonBoxObjectiveComparator(Epsilons epsilons) {
		super();
		this.epsilons = epsilons;
	}

	/**
	 * Returns {@code true} if the the two solutions passed to the previous invocation of {@code compare} existed
	 * within the same &epsilon;-box; {@code false} otherwise.
	 * 
	 * @return {@code true} if the the two solutions passed to the previous invocation of {@code compare} existed
	 *         within the same &epsilon;-box; {@code false} otherwise.
	 */
	public boolean isSameBox() {
		return isSameBox;
	}

	/**
	 * Set to {@code true} if the the two solutions passed to the previous invocation of {@code compare} existed
	 * within the same &epsilon;-box; {@code false} otherwise.
	 * 
	 * @param isSameBox {@code true} if the the two solutions passed to the previous invocation of {@code compare}
	 *        existed within the same &epsilon;-box; {@code false} otherwise.
	 */
	protected void setSameBox(boolean isSameBox) {
		this.isSameBox = isSameBox;
	}
	
	/**
	 * Returns the &epsilon; values in use by this comparator.
	 * 
	 * @return the &epsilon; values
	 */
	public Epsilons getEpsilons() {
		return epsilons;
	}

	/**
	 * Compares the two solutions using the additive &epsilon;-box dominance relation.
	 */
	@Override
	public int compare(Solution solution1, Solution solution2) {
		setSameBox(false);

		boolean dominate1 = false;
		boolean dominate2 = false;

		for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
			double epsilon = epsilons.get(i);
			double index1 = Math.floor(solution1.getObjective(i) / epsilon);
			double index2 = Math.floor(solution2.getObjective(i) / epsilon);
			int flag = Double.compare(index1, index2);

			if (flag < 0) {
				dominate1 = true;

				if (dominate2) {
					return 0;
				}
			} else if (flag > 0) {
				dominate2 = true;

				if (dominate1) {
					return 0;
				}
			}
		}

		if (!dominate1 && !dominate2) {
			setSameBox(true);

			double dist1 = 0.0;
			double dist2 = 0.0;

			for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
				double epsilon = epsilons.get(i);
				double index1 = Math.floor(solution1.getObjective(i) / epsilon);
				double index2 = Math.floor(solution2.getObjective(i) / epsilon);

				dist1 += Math.pow(solution1.getObjective(i) - index1 * epsilon, 2.0);
				dist2 += Math.pow(solution2.getObjective(i) - index2 * epsilon, 2.0);
			}

			return Double.compare(dist1, dist2) < 0 ? -1 : 1;
		} else if (dominate1) {
			return -1;
		} else {
			return 1;
		}
	}

}

/* Copyright 2009-2015 David Hadka
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
package org.moeaframework.core.indicator;

import org.moeaframework.core.Indicator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;

/**
 * Abstract class for indicators that require normalized approximation and
 * reference sets.
 */
public abstract class NormalizedIndicator implements Indicator {

	/**
	 * The problem.
	 */
	protected final Problem problem;

	/**
	 * The normalizer to normalize populations so that all objectives reside in
	 * the range {@code [0, 1]}.
	 */
	private final Normalizer normalizer;
	
	/**
	 * The normalized reference set.
	 */
	private final NondominatedPopulation normalizedReferenceSet;

	/**
	 * Constructs a normalized indicator for the specified problem and 
	 * corresponding reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set for the problem
	 * @throws IllegalArgumentException if the reference set contains fewer
	 *         than two solutions
	 */
	public NormalizedIndicator(Problem problem,
			NondominatedPopulation referenceSet) {
		super();
		this.problem = problem;
		
		normalizer = new Normalizer(problem, referenceSet);
		normalizedReferenceSet = normalizer.normalize(referenceSet);
	}
	
	/**
	 * Normalizes the specified approximation set.
	 * 
	 * @param approximationSet the approximation set to be normalized
	 * @return a new population representing the normalized approximation set
	 */
	protected NondominatedPopulation normalize(
			NondominatedPopulation approximationSet) {
		return normalizer.normalize(approximationSet);
	}
	
	/**
	 * Returns the normalized reference set.
	 * 
	 * @return the normalized reference set
	 */
	protected NondominatedPopulation getNormalizedReferenceSet() {
		return normalizedReferenceSet;
	}

}

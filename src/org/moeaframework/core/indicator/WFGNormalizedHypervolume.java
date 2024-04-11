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
package org.moeaframework.core.indicator;

import java.util.Arrays;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;

/**
 * Fast hypervolume calculation published by the Walking Fish Group (WFG).  This version is normalized by the bounds of
 * a reference set.
 * <p>
 * References:
 * <ol>
 *   <li>While, Ronald Lyndon et al. “A Fast Way of Calculating Exact Hypervolumes.” IEEE Transactions on Evolutionary
 *       Computation 16 (2012): 86-95.
 * </ol>
 */
public class WFGNormalizedHypervolume extends NormalizedIndicator {
	
	/**
	 * The un-normalized hypervolume calculation.
	 */
	private WFGHypervolume hypervolume;

	/**
	 * Constructs a hypervolume evaluator for the specified problem and reference set.  See {@link DefaultNormalizer}
	 * for details on configuring normalization.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 */
	public WFGNormalizedHypervolume(Problem problem, NondominatedPopulation referenceSet) {
		this(problem, DefaultNormalizer.getInstance().getHypervolumeNormalizer(problem, referenceSet));
	}
	
	/**
	 * Constructs a hypervolume evaluator for the specified problem using the given normalizer.
	 * 
	 * @param problem the problem
	 * @param normalizer a user-provided normalizer
	 */
	public WFGNormalizedHypervolume(Problem problem, Normalizer normalizer) {
		super(problem, new NondominatedPopulation(), normalizer);
		this.hypervolume = new WFGHypervolume(problem, getNormalizedReferencePoint(problem));
	}
	
	/**
	 * Creates the reference point to be used after normalization, i.e., {@code [1, ..., 1]}.
	 * 
	 * @param problem the problem
	 * @return the reference point
	 */
	private static final double[] getNormalizedReferencePoint(Problem problem) {
		double[] referencePoint = new double[problem.getNumberOfObjectives()];
		Arrays.fill(referencePoint, 1.0);
		return referencePoint;
	}

	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return hypervolume.evaluate(normalize(approximationSet));
	}

}

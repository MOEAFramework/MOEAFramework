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
package org.moeaframework.core.indicator;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;

/**
 * Evaluates multiple indicators while avoiding repetitive computations.
 */
public class QualityIndicator {

	/**
	 * The problem.
	 */
	private final Problem problem;

	/**
	 * The reference set for the problem.
	 */
	private final NondominatedPopulation referenceSet;
	
	/**
	 * The normalized reference set.
	 */
	private final NondominatedPopulation normalizedReferenceSet;
	
	/**
	 * The normalizer to normalize populations so that all objectives reside in
	 * the range {@code [0, 1]}.
	 */
	private final Normalizer normalizer;
	
	/**
	 * The normalizer that includes the hypervolume delta.
	 */
	private final Normalizer hypervolumeNormalizer;
	
	/**
	 * The approximation set used during the last invocation of
	 * {@code calculate}.
	 */
	private NondominatedPopulation normalizedApproximationSet;

	/**
	 * The generational distance of the approximation set from the last
	 * invocation of {@code calculate}.
	 */
	private double generationalDistance;

	/**
	 * The inverted generational distance of the approximation set from the last
	 * invocation of {@code calculate}.
	 */
	private double invertedGenerationalDistance;

	/**
	 * The additive &epsilon;-indicator of the approximation set from the last
	 * invocation of {@code calculate}.
	 */
	private double additiveEpsilonIndicator;

	/**
	 * The hypervolume of the approximation set from the last invocation of
	 * {@code calculate}.
	 */
	private double hypervolume;

	/**
	 * The maximum Pareto front error of the approximation set from the last
	 * invocation of {@code calculate}.
	 */
	private double maximumParetoFrontError;

	/**
	 * The spacing of the approximation set from the last invocation of
	 * {@code calculate}.
	 */
	private double spacing;

	/**
	 * Constructs a quality indicator evaluator for the specified problem and 
	 * corresponding reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set for the problem
	 */
	public QualityIndicator(Problem problem, 
			NondominatedPopulation referenceSet) {
		this.problem = problem;
		this.referenceSet = referenceSet;
		
		normalizer = new Normalizer(problem, referenceSet);
		normalizedReferenceSet = normalizer.normalize(referenceSet);
		
		hypervolumeNormalizer = new Normalizer(problem, referenceSet,
				Settings.getHypervolumeDelta());
	}

	/**
	 * Returns the problem.
	 * 
	 * @return the problem
	 */
	public Problem getProblem() {
		return problem;
	}

	/**
	 * Returns the generational distance of the approximation set from the last
	 * invocation of {@code calculate}.
	 * 
	 * @return the generational distance of the approximation set from the last
	 *         invocation of {@code calculate}
	 * @throws IllegalStateException if the {@code calculate} method has not
	 *         been invoked
	 */
	public double getGenerationalDistance() {
		checkCalculateInvocation();
		
		return generationalDistance;
	}

	/**
	 * Returns the inverted generational distance of the approximation set from
	 * the last invocation of {@code calculate}.
	 * 
	 * @return the inverted generational distance of the approximation set from
	 *         the last invocation of {@code calculate}
	 * @throws IllegalStateException if the {@code calculate} method has not
	 *         been invoked
	 */
	public double getInvertedGenerationalDistance() {
		checkCalculateInvocation();
		
		return invertedGenerationalDistance;
	}

	/**
	 * Returns the additive &epsilon;-indicator of the approximation set from 
	 * the last invocation of {@code calculate}.
	 * 
	 * @return the additive &epsilon;-indicator of the approximation set from 
	 *         the last invocation of {@code calculate}
	 * @throws IllegalStateException if the {@code calculate} method has not
	 *         been invoked
	 */
	public double getAdditiveEpsilonIndicator() {
		checkCalculateInvocation();
		
		return additiveEpsilonIndicator;
	}

	/**
	 * Returns the hypervolume of the approximation set from the last invocation
	 * of {@code calculate}.
	 * 
	 * @return the hypervolume of the approximation set from the last invocation
	 *         of {@code calculate}
	 * @throws IllegalStateException if the {@code calculate} method has not
	 *         been invoked
	 */
	public double getHypervolume() {
		checkCalculateInvocation();
		
		return hypervolume;
	}

	/**
	 * Returns the maximum Pareto front error of the approximation set from the
	 * last invocation of {@code calculate}.
	 * 
	 * @return the maximum Pareto front error of the approximation set from the
	 *         last invocation of {@code calculate}
	 * @throws IllegalStateException if the {@code calculate} method has not
	 *         been invoked
	 */
	public double getMaximumParetoFrontError() {
		checkCalculateInvocation();
		
		return maximumParetoFrontError;
	}

	/**
	 * Returns the spacing of the approximation set from the last invocation of
	 * {@code calculate}.
	 * 
	 * @return the spacing of the approximation set from the last invocation of
	 *         {@code calculate}
	 * @throws IllegalStateException if the {@code calculate} method has not
	 *         been invoked
	 */
	public double getSpacing() {
		checkCalculateInvocation();
		
		return spacing;
	}

	/**
	 * The reference set for the problem.
	 * 
	 * @return the reference set for the problem
	 * @throws IllegalStateException if the {@code calculate} method has not
	 *         been invoked
	 */
	public NondominatedPopulation getReferenceSet() {
		return referenceSet;
	}
	
	/**
	 * Throws an exception if the {@code calculate} method has not been
	 * invoked.
	 * 
	 * @throws IllegalStateException if the {@code calculate} method has not
	 *         been invoked
	 */
	private void checkCalculateInvocation() {
		if (normalizedApproximationSet == null) {
			throw new IllegalStateException(
					"invoke calculate prior to getting indicator values");
		}
	}

	/**
	 * Calculates all the metrics for the specified approximation set. By
	 * grouping all calculates into one method, repetitive calculates are
	 * avoided.
	 * 
	 * @param approximationSet the approximation set
	 */
	public void calculate(NondominatedPopulation approximationSet) {
		if (Settings.isHypervolumeEnabled()) {
			hypervolume = Hypervolume.evaluate(problem, 
					hypervolumeNormalizer.normalize(approximationSet));
		} else {
			hypervolume = Double.NaN;
		}
		
		normalizedApproximationSet = normalizer.normalize(approximationSet);
		
		generationalDistance = GenerationalDistance.evaluate(problem,
				normalizedApproximationSet, normalizedReferenceSet,
				Settings.getGDPower());
		invertedGenerationalDistance = InvertedGenerationalDistance.evaluate(
				problem, normalizedApproximationSet, normalizedReferenceSet,
				Settings.getIGDPower());
		additiveEpsilonIndicator = AdditiveEpsilonIndicator.evaluate(problem,
				normalizedApproximationSet, normalizedReferenceSet);
		maximumParetoFrontError = MaximumParetoFrontError.evaluate(problem,
				normalizedApproximationSet, normalizedReferenceSet);
		spacing = Spacing.evaluate(problem, approximationSet);
	}

}

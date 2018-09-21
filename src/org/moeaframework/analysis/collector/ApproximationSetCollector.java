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
package org.moeaframework.analysis.collector;

import java.util.ArrayList;

import org.moeaframework.analysis.sensitivity.EpsilonHelper;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

/**
 * Collects approximation sets from an {@link Algorithm}.
 */
public class ApproximationSetCollector implements Collector {
	
	/**
	 * The algorithm instance used by this collector; or {@code null} if this 
	 * collector has not yet been attached.
	 */
	private final Algorithm algorithm;
	
	/**
	 * The &epsilon;-values used when collecting only the &epsilon;-dominant
	 * solutions; or {@code null} if regular Pareto dominance is used.
	 */
	private final double[] epsilon;
	
	/**
	 * Constructs an unattached collector for recording Pareto dominance
	 * approximation sets from an algorithm.
	 */
	public ApproximationSetCollector() {
		this(null, null);
	}
	
	/**
	 * Constructs an unattached collector for recording &epsilon;-box dominance
	 * approximation sets from an algorithm.
	 * 
	 * @param epsilon the &epsilon; value
	 */
	public ApproximationSetCollector(double epsilon) {
		this(null, new double[] { epsilon });
	}
	
	/**
	 * Constructs an unattached collector for recording &epsilon;-box dominance
	 * approximation sets from an algorithm.
	 * 
	 * @param epsilon the &epsilon; values
	 */
	public ApproximationSetCollector(double[] epsilon) {
		this(null, epsilon);
	}
	
	/**
	 * Constructs a collector for recording approximation sets from the
	 * specified algorithm.
	 * 
	 * @param algorithm the algorithm this collector records data from
	 * @param epsilon the &epsilon;-values used when collecting only the 
	 *        &epsilon;-dominant solutions; or {@code null} if regular Pareto
	 *        dominance is used
	 */
	public ApproximationSetCollector(Algorithm algorithm, double[] epsilon) {
		super();
		this.algorithm = algorithm;
		this.epsilon = epsilon;
	}

	@Override
	public AttachPoint getAttachPoint() {
		return AttachPoint.isSubclass(Algorithm.class).and(
				AttachPoint.not(AttachPoint.isNestedIn(Algorithm.class)));
	}

	@Override
	public Collector attach(Object object) {
		return new ApproximationSetCollector((Algorithm)object, epsilon);
	}

	@Override
	public void collect(Accumulator accumulator) {
		ArrayList<Solution> list = new ArrayList<Solution>();
		NondominatedPopulation result = algorithm.getResult();
		
		//if epsilons are provided, convert result to epsilon-dominance archive
		if (epsilon != null) {
			result = EpsilonHelper.convert(result, epsilon);
		}
		
		for (Solution solution : result) {
			list.add(solution);
		}
		
		accumulator.add("Approximation Set", list);
	}

}

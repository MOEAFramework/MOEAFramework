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

import org.apache.commons.math3.util.MathArrays;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Indicator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.EpsilonBoxDominanceComparator;

/**
 * Measures the contribution of the approximation set to the reference set.
 */
public class Contribution implements Indicator {
	
	/**
	 * The reference set.
	 */
	private final NondominatedPopulation referenceSet;

	/**
	 * The &epsilon;-box dominance comparator used to determine if solutions in
	 * the reference set are covered by solutions in the approximation set; or 
	 * {@code null} if exact matching is used.
	 */
	private final EpsilonBoxDominanceComparator comparator;
	
	/**
	 * Constructs the contribution indicator using the specified reference set.
	 * Exact matching is used.
	 * 
	 * @param referenceSet the reference set
	 * @throws IllegalArgumentException if the reference set is empty
	 */
	public Contribution(NondominatedPopulation referenceSet) {
		this(referenceSet, (EpsilonBoxDominanceComparator)null);
	}
	
	/**
	 * Constructs the contribution indicator using the specified reference set
	 * and &epsilon; value.  Solutions residing in the same &epsilon;-box are
	 * considered to be equivalent.
	 * 
	 * @param referenceSet the reference set
	 * @param epsilon the &epsilon; value
	 * @throws IllegalArgumentException if the reference set is empty
	 */
	public Contribution(NondominatedPopulation referenceSet, double epsilon) {
		this(referenceSet, new EpsilonBoxDominanceComparator(epsilon));
	}
	
	/**
	 * Constructs the contribution indicator using the specified reference set
	 * and &epsilon; values.  Solutions residing in the same &epsilon;-box are
	 * considered to be equivalent.
	 * 
	 * @param referenceSet the reference set
	 * @param epsilon the &epsilon; values
	 * @throws IllegalArgumentException if the reference set is empty
	 */
	public Contribution(NondominatedPopulation referenceSet, double[] epsilon) {
		this(referenceSet, new EpsilonBoxDominanceComparator(epsilon));
	}
	
	/**
	 * Constructs the contribution indicator using the specified reference set
	 * and &epsilon;-box dominance comparator.  Solutions residing in the same 
	 * &epsilon;-box are considered to be equivalent.If the comparator is 
	 * {@code null}, exact matching is used.
	 * 
	 * @param referenceSet the reference set
	 * @param comparator the &epsilon;-box dominance comparator used to 
	 *        determine if solutions in the reference set are covered by 
	 *        solutions in the approximation set; or {@code null} if exact
	 *        matching is used
	 * @throws IllegalArgumentException if the reference set is empty
	 */
	public Contribution(NondominatedPopulation referenceSet, 
			EpsilonBoxDominanceComparator comparator) {
		super();
		this.comparator = comparator;
		
		if (referenceSet.isEmpty()) {
			throw new IllegalArgumentException("reference set is empty");
		}
		
		if (comparator == null) {
			this.referenceSet = referenceSet;
		} else {
			this.referenceSet = new EpsilonBoxDominanceArchive(comparator, 
					referenceSet);
		}
	}

	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		int count = 0;

		for (Solution solution1 : referenceSet) {
			boolean match = false;
			
			for (Solution solution2 : approximationSet) {
				if (comparator == null) {
					double distance = MathArrays.distance(
							solution1.getObjectives(), 
							solution2.getObjectives());
					
					if (distance < Settings.EPS) {
						match = true;
						break;
					}
				} else {
					comparator.compare(solution1, solution2);
					
					if (comparator.isSameBox()) {
						match = true;
						break;
					}
				}
			}

			if (match) {
				count++;
			}
		}
		
		return count / (double)referenceSet.size();
	}

}

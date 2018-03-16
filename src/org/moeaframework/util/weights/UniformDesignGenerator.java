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
package org.moeaframework.util.weights;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.primes.Primes;

/**
 * Generates weights according to a uniform design of mixtures using the
 * Hammersley low-discrepancy sequence generator.  Uniform design has several
 * advantages over {@link NormalBoundaryIntersectionGenerator}, including
 * avoiding generating many weights on the boundary and avoiding the
 * combinatorial growth of weights as the number of objectives increases.
 * Tan et al. first proposed the use of uniform design to generate weights for
 * an MOEA, but their method becomes computationally prohibitive as the number
 * of objectives or number of points increases.  The use of the more efficient
 * Hammersley method was proposed by Berenguer and Coello Coello (2015).
 * <p>
 * References:
 * <ol>
 *   <li>Tan Y., Y. Jiao, H. Li, and X. Wang (2013).  "MOEA/D + uniform design:
 *       A new version of MOEA/D for optimization problems with many
 *       objectives."  Computers & Operations Research, 40, 1648-1660.
 *   <li>Berenguer, J.A.M. and C.A. Coello Coello (2015).  "Evolutionary Many-
 *       Objective Optimization Based on Kuhn-Munkres' Algorithm."  Evolutionary
 *       Multi-Criterion Optimization: 8th International Conference, pp. 3-17.
 * </ol>
 */
public class UniformDesignGenerator implements WeightGenerator {
	
	/**
	 * The number of objectives.
	 */
	private final int numberOfObjectives;
	
	/**
	 * The number of weights to generate.
	 */
	private final int numberOfPoints;
	
	/**
	 * Constructs a new weight generator based on uniform design.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param numberOfPoints the number of weights to generate
	 */
	public UniformDesignGenerator(int numberOfObjectives, int numberOfPoints) {
		super();
		this.numberOfObjectives = numberOfObjectives;
		this.numberOfPoints = numberOfPoints;
	}

	@Override
	public int size() {
		return numberOfPoints;
	}
	
	/**
	 * Returns the first k prime numbers.
	 * 
	 * @param k the number of prime numbers to return
	 * @return the first k prime numbers
	 */
	protected int[] generateFirstKPrimes(int k) {
		int[] primes = new int[k];
		primes[0] = 2;
		
		for (int i = 1; i < k; i++) {
			primes[i] = Primes.nextPrime(primes[i-1]);
		}
		
		return primes;
	}

	@Override
	public List<double[]> generate() {
		// generate uniform design using Hammersley method
		List<double[]> designs = new ArrayList<double[]>();
		int[] primes = generateFirstKPrimes(numberOfObjectives-2);
		
		for (int i = 0; i < numberOfPoints; i++) {
			double[] design = new double[numberOfObjectives-1];
			design[0] = (2.0*(i+1) - 1.0) / (2.0*numberOfPoints);
			
			for (int j = 1; j < numberOfObjectives-1; j++) {
				double f = 1.0/primes[j-1];
				int d = i+1;
				design[j] = 0.0;
				
				while (d > 0) {
					design[j] += f * (d % primes[j-1]);
					d = d / primes[j-1];
					f = f / primes[j-1];
				}
			}
			
			designs.add(design);
		}
		
		// transform designs into weight vectors (sum to 1)
		List<double[]> weights = new ArrayList<double[]>();
		
		for (double[] design : designs) {
			double[] weight = new double[numberOfObjectives];
			
			for (int i = 1; i <= numberOfObjectives; i++) {
				if (i == numberOfObjectives) {
					weight[i-1] = 1.0;
				} else {
					weight[i-1] = 1.0 - Math.pow(design[i-1], 1.0 / (numberOfObjectives-i));
				}
				
				for (int j = 1; j <= i-1; j++) {
					weight[i-1] *= Math.pow(design[j-1], 1.0 / (numberOfObjectives-j));
				}
			}
			
			weights.add(weight);
		}
		
		return weights;
	}

}

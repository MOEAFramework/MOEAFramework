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
package org.moeaframework.core.fitness;

import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.Normalizer;

/**
 * Abstract class for assigning fitness based on a binary indicator.  This
 * class combines the pair-wise fitness of each solution into an aggregate
 * fitness within the entire population.
 */
public abstract class IndicatorFitnessEvaluator implements FitnessEvaluator {

	/**
	 * The problem.
	 */
	private Problem problem;

	/**
	 * Scaling factor for fitness calculation.
	 */
	private static final double kappa = 0.05;
	
	/**
	 * Record of the maximum indicator value from the last call to
	 * {@link #evaluate(Population)}.
	 */
	private double maxAbsIndicatorValue;
	
	/**
	 * Record of the fitness components from the last call to
	 * {@link #evaluate(Population)}.
	 */
	private double[][] fitcomp;

	/**
	 * Constructs an indicator-based fitness for the specified problem.
	 * 
	 * @param problem the problem
	 */
	public IndicatorFitnessEvaluator(Problem problem) {
		this.problem = problem;
	}

	/**
	 * Returns the problem.
	 * 
	 * @return the problem
	 */
	public Problem getProblem() {
		return problem;
	}

	/*
	 * The following method is modified from the IBEA implementation for the
	 * PISA framework, available at <a href="http://www.tik.ee.ethz.ch/pisa/">
	 * PISA Homepage</a>.
	 * 
	 * Copyright (c) 2002-2003 Swiss Federal Institute of Technology,
	 * Computer Engineering and Networks Laboratory. All rights reserved.
	 * 
	 * PISA - A Platform and Programming Language Independent Interface for
	 * Search Algorithms.
	 * 
	 * IBEA - Indicator Based Evoluationary Algorithm - A selector module
	 * for PISA
	 * 
	 * Permission to use, copy, modify, and distribute this software and its
	 * documentation for any purpose, without fee, and without written
	 * agreement is hereby granted, provided that the above copyright notice
	 * and the following two paragraphs appear in all copies of this
	 * software.
	 * 
	 * IN NO EVENT SHALL THE SWISS FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER
	 * ENGINEERING AND NETWORKS LABORATORY BE LIABLE TO ANY PARTY FOR DIRECT,
	 * INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF
	 * THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE SWISS
	 * FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER ENGINEERING AND NETWORKS
	 * LABORATORY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	 * 
	 * THE SWISS FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER ENGINEERING AND
	 * NETWORKS LABORATORY, SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING,
	 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
	 * FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS
	 * ON AN "AS IS" BASIS, AND THE SWISS FEDERAL INSTITUTE OF TECHNOLOGY,
	 * COMPUTER ENGINEERING AND NETWORKS LABORATORY HAS NO OBLIGATION TO
	 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
	 */
	@Override
	public void evaluate(Population population) {
		Normalizer normalizer = new Normalizer(problem, population);
		Population normalizedPopulation = normalizer.normalize(population);

		// compute fitness components
		fitcomp = new double[population.size()][population.size()];
		maxAbsIndicatorValue = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < population.size(); i++) {
			for (int j = 0; j < population.size(); j++) {
				fitcomp[i][j] = calculateIndicator(normalizedPopulation.get(i),
						normalizedPopulation.get(j));

				if (Math.abs(fitcomp[i][j]) > maxAbsIndicatorValue) {
					maxAbsIndicatorValue = Math.abs(fitcomp[i][j]);
				}
			}
		}

		// calculate fitness from fitness components
		for (int i = 0; i < population.size(); i++) {
			double sum = 0.0;
			
			for (int j = 0; j < population.size(); j++) {
				if (i != j) {
					sum += Math.exp((-fitcomp[j][i] / maxAbsIndicatorValue) / kappa);
				}
			}
			
			population.get(i).setAttribute(FitnessEvaluator.FITNESS_ATTRIBUTE, sum);
		}
	}
	
	/**
	 * After calling {@link #evaluate(Population)}, this method is used to
	 * iteratively remove solutions from the population while updating the
	 * fitness value. There must be no other modifications to the population
	 * between invocations of {@link #evaluate(Population)} and this method
	 * other than removing solutions using this method.
	 * 
	 * @param population the population
	 * @param removeIndex the index to remove
	 */
	public void removeAndUpdate(Population population, int removeIndex) {
		if (fitcomp == null) {
			throw new FrameworkException("evaluate must be called first");
		}
		
		for (int i = 0; i < population.size(); i++) {
			if (i != removeIndex) {
				Solution solution = population.get(i);
				double fitness = (Double)solution.getAttribute(
						FitnessEvaluator.FITNESS_ATTRIBUTE);
				
				fitness -= Math.exp((-fitcomp[removeIndex][i] / maxAbsIndicatorValue) / kappa);
				
				solution.setAttribute(FITNESS_ATTRIBUTE, fitness);
			}
		}
		
		for (int i = 0; i < population.size(); i++) {
			for (int j = removeIndex+1; j < population.size(); j++) {
				fitcomp[i][j-1] = fitcomp[i][j];
			}
			
			if (i > removeIndex) {
				fitcomp[i-1] = fitcomp[i];
			}
		}

		population.remove(removeIndex);
	}

	/**
	 * Returns the indicator value relative to the two solutions.
	 * 
	 * @param solution1 the first solution
	 * @param solution2 the second solution
	 * @return the indicator value relative to the two solutions
	 */
	protected abstract double calculateIndicator(Solution solution1,
			Solution solution2);

}

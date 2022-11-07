/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.core.operator.real;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Retryable;
import org.moeaframework.TestThresholds;
import org.moeaframework.CIRunner;
import org.moeaframework.algorithm.jmetal.JMetalUtils;
import org.moeaframework.algorithm.jmetal.ProblemAdapter;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.MeanCentricVariationTest;
import org.moeaframework.core.operator.ParentImmutabilityTest;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.variable.RealVariable;
import org.uma.jmetal.util.JMetalException;

/**
 * Tests the {@link DifferentialEvolutionVariation} class.
 */
@RunWith(CIRunner.class)
public class DifferentialEvolutionVariationTest extends MeanCentricVariationTest {
	
	/**
	 * Tests if the MOEA Framework and JMetal implementations of differential
	 * evolution are equivalent.
	 */
	@Test
	@Retryable
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testDistribution() throws ClassNotFoundException, JMetalException {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		ProblemAdapter adapter = JMetalUtils.createProblemAdapter(problem);
		
		DifferentialEvolutionVariation myDE = new DifferentialEvolutionVariation(0.1, 0.5);
		org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover theirDE =
				new org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover(0.1, 0.5, "rand/1/bin");
		
		for (int i = 0; i < 10; i++) {
			Solution[] myParents = new Solution[4];
			List<org.uma.jmetal.solution.DoubleSolution> theirParents = new ArrayList<org.uma.jmetal.solution.DoubleSolution>();

			Solution[] myOffspring = new Solution[TestThresholds.SAMPLES];
			Solution[] theirOffspring = new Solution[TestThresholds.SAMPLES];
			
			for (int j = 0; j < 4; j++) {
				org.uma.jmetal.solution.DoubleSolution parent = (org.uma.jmetal.solution.DoubleSolution)adapter.createSolution();
				theirParents.add(parent);
				myParents[j] = adapter.convert(parent);
			}
			
			for (int j = 0; j < TestThresholds.SAMPLES; j++) {
				myOffspring[j] = myDE.evolve(myParents)[0];
				
				theirDE.setCurrentSolution(theirParents.get(0));
				theirOffspring[j] = adapter.convert(theirDE.execute(theirParents.subList(1, 4)).get(0));
			}
			
			check(theirOffspring, myOffspring);
		}
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		DifferentialEvolutionVariation de = new DifferentialEvolutionVariation(1.0, 1.0);

		Solution s1 = new Solution(2, 0);
		s1.setVariable(0, new RealVariable(2.0, -10.0, 10.0));
		s1.setVariable(1, new RealVariable(2.0, -10.0, 10.0));

		Solution s2 = new Solution(2, 0);
		s2.setVariable(0, new RealVariable(-2.0, -10.0, 10.0));
		s2.setVariable(1, new RealVariable(-2.0, -10.0, 10.0));

		Solution s3 = new Solution(2, 0);
		s3.setVariable(0, new RealVariable(-2.0, -10.0, 10.0));
		s3.setVariable(1, new RealVariable(2.0, -10.0, 10.0));

		Solution s4 = new Solution(2, 0);
		s4.setVariable(0, new RealVariable(2.0, -10.0, 10.0));
		s4.setVariable(1, new RealVariable(-2.0, -10.0, 10.0));

		Solution[] parents = new Solution[] { s1, s2, s3, s4 };

		ParentImmutabilityTest.test(parents, de);
	}

}

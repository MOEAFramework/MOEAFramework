/* Copyright 2009-2019 David Hadka
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

import java.util.HashMap;

import jmetal.util.JMException;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.RetryOnTravis;
import org.moeaframework.TestThresholds;
import org.moeaframework.TravisRunner;
import org.moeaframework.algorithm.jmetal.JMetalProblemAdapter;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.MeanCentricVariationTest;
import org.moeaframework.core.operator.ParentImmutabilityTest;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.variable.RealVariable;

/**
 * Tests the {@link DifferentialEvolutionVariation} class.
 */
@RunWith(TravisRunner.class)
public class DifferentialEvolutionVariationTest extends MeanCentricVariationTest {
	
	/**
	 * Tests if the MOEA Framework and JMetal implementations of differential
	 * evolution are equivalent.
	 */
	@Test
	@RetryOnTravis
	public void testDistribution() throws ClassNotFoundException, JMException {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		JMetalProblemAdapter adapter = new JMetalProblemAdapter(problem);
		DifferentialEvolutionVariation myDE = new DifferentialEvolutionVariation(0.1, 0.5);
		jmetal.operators.crossover.DifferentialEvolutionCrossover theirDE =
				new jmetal.operators.crossover.DifferentialEvolutionCrossover(
						new HashMap<String, Object>());
		
		theirDE.CR_ = 0.1;
		theirDE.F_ = 0.5;
		
		for (int i = 0; i < 10; i++) {
			Solution[] myParents = new Solution[4];
			jmetal.core.Solution[] theirParents = new jmetal.core.Solution[4];
			
			Solution[] myOffspring = new Solution[TestThresholds.SAMPLES];
			Solution[] theirOffspring = new Solution[TestThresholds.SAMPLES];
			
			for (int j = 0; j < 4; j++) {
				theirParents[j] = new jmetal.core.Solution(adapter);
				myParents[j] = adapter.translate(theirParents[j]);
			}
			
			for (int j = 0; j < TestThresholds.SAMPLES; j++) {
				myOffspring[j] = myDE.evolve(myParents)[0];
				theirOffspring[j] = adapter.translate((jmetal.core.Solution)
						theirDE.execute(new Object[] {
							theirParents[0], 
							ArrayUtils.subarray(theirParents, 1,
									theirParents.length)
						}));
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

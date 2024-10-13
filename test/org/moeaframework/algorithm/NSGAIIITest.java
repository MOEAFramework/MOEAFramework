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
package org.moeaframework.algorithm;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Problem;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.indicator.InvertedGenerationalDistance;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.DTLZ.DTLZ1;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.DTLZ.DTLZ3;
import org.moeaframework.problem.DTLZ.DTLZ4;
import org.moeaframework.problem.ScaledProblem;
import org.moeaframework.util.weights.NormalBoundaryDivisions;

public class NSGAIIITest {
	
	@Test
	public void testDefaults() {
		Problem problem = new MockRealProblem(2);
		NormalBoundaryDivisions divisions = NormalBoundaryDivisions.forProblem(problem);
		
		NSGAIII algorithm = new NSGAIII(problem);
		
		Assert.assertEquals(divisions, algorithm.getPopulation().getDivisions());
	}
	
	@Test
	public void testConfiguration() {
		Problem problem = new MockRealProblem(2);
		NormalBoundaryDivisions divisions = new NormalBoundaryDivisions(10);
		
		NSGAIII algorithm = new NSGAIII(problem);
		algorithm.applyConfiguration(divisions.toProperties());
		
		Assert.assertEquals(divisions, algorithm.getPopulation().getDivisions());
	}
	
	/**
	 * Replicates the unscaled and scaled DTLZ experiments performed in the original NSGA-III paper.
	 */
	@SuppressWarnings("resource")
	@Test
	@Ignore("Must download reference sets from http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm")
	public void test() throws IOException {
		evaluate(new DTLZ1(3), 400, NondominatedPopulation.loadReferenceSet(new File("DTLZ1(3)-PF.txt")));
		evaluate(new DTLZ1(5), 600, NondominatedPopulation.loadReferenceSet(new File("DTLZ1(5)-PF.txt")));
		evaluate(new DTLZ1(8), 750, NondominatedPopulation.loadReferenceSet(new File("DTLZ1(8)-PF.txt")));
		evaluate(new DTLZ1(10), 1000, NondominatedPopulation.loadReferenceSet(new File("DTLZ1(10)-PF.txt")));
		evaluate(new DTLZ1(15), 1500, NondominatedPopulation.loadReferenceSet(new File("DTLZ1(15)-PF.txt")));

		evaluate(new DTLZ2(3), 250, NondominatedPopulation.loadReferenceSet(new File("DTLZ2(3)-PF.txt")));
		evaluate(new DTLZ2(5), 350, NondominatedPopulation.loadReferenceSet(new File("DTLZ2(5)-PF.txt")));
		evaluate(new DTLZ2(8), 500, NondominatedPopulation.loadReferenceSet(new File("DTLZ2(8)-PF.txt")));
		evaluate(new DTLZ2(10), 750, NondominatedPopulation.loadReferenceSet(new File("DTLZ2(10)-PF.txt")));
		evaluate(new DTLZ2(15), 1000, NondominatedPopulation.loadReferenceSet(new File("DTLZ2(15)-PF.txt")));
		
		evaluate(new DTLZ3(3), 1000, NondominatedPopulation.loadReferenceSet(new File("DTLZ3(3)-PF.txt")));
		evaluate(new DTLZ3(5), 1000, NondominatedPopulation.loadReferenceSet(new File("DTLZ3(5)-PF.txt")));
		evaluate(new DTLZ3(8), 1000, NondominatedPopulation.loadReferenceSet(new File("DTLZ3(8)-PF.txt")));
		evaluate(new DTLZ3(10), 1500, NondominatedPopulation.loadReferenceSet(new File("DTLZ3(10)-PF.txt")));
		evaluate(new DTLZ3(15), 2000, NondominatedPopulation.loadReferenceSet(new File("DTLZ3(15)-PF.txt")));
		
		evaluate(new DTLZ4(3), 600, NondominatedPopulation.loadReferenceSet(new File("DTLZ4(3)-PF.txt")));
		evaluate(new DTLZ4(5), 1000, NondominatedPopulation.loadReferenceSet(new File("DTLZ4(5)-PF.txt")));
		evaluate(new DTLZ4(8), 1250, NondominatedPopulation.loadReferenceSet(new File("DTLZ4(8)-PF.txt")));
		evaluate(new DTLZ4(10), 2000, NondominatedPopulation.loadReferenceSet(new File("DTLZ4(10)-PF.txt")));
		evaluate(new DTLZ4(15), 3000, NondominatedPopulation.loadReferenceSet(new File("DTLZ4(15)-PF.txt")));
		
		new ScaledProblem(new DTLZ1(3), 10).createScaledReferenceSet(new File("DTLZ1(3)-PF.txt"), new File("DTLZ1(3)-PFscaled.txt"));
		new ScaledProblem(new DTLZ1(5), 10).createScaledReferenceSet(new File("DTLZ1(5)-PF.txt"), new File("DTLZ1(5)-PFscaled.txt"));
		new ScaledProblem(new DTLZ1(8), 3).createScaledReferenceSet(new File("DTLZ1(8)-PF.txt"), new File("DTLZ1(8)-PFscaled.txt"));
		new ScaledProblem(new DTLZ1(10), 2).createScaledReferenceSet(new File("DTLZ1(10)-PF.txt"), new File("DTLZ1(10)-PFscaled.txt"));
		new ScaledProblem(new DTLZ1(15), 1.2).createScaledReferenceSet(new File("DTLZ1(15)-PF.txt"), new File("DTLZ1(15)-PFscaled.txt"));
		new ScaledProblem(new DTLZ2(3), 10).createScaledReferenceSet(new File("DTLZ2(3)-PF.txt"), new File("DTLZ2(3)-PFscaled.txt"));
		new ScaledProblem(new DTLZ2(5), 10).createScaledReferenceSet(new File("DTLZ2(5)-PF.txt"), new File("DTLZ2(5)-PFscaled.txt"));
		new ScaledProblem(new DTLZ2(8), 3).createScaledReferenceSet(new File("DTLZ2(8)-PF.txt"), new File("DTLZ2(8)-PFscaled.txt"));
		new ScaledProblem(new DTLZ2(10), 3).createScaledReferenceSet(new File("DTLZ2(10)-PF.txt"), new File("DTLZ2(10)-PFscaled.txt"));
		new ScaledProblem(new DTLZ2(15), 2).createScaledReferenceSet(new File("DTLZ2(15)-PF.txt"), new File("DTLZ2(15)-PFscaled.txt"));
		
		evaluate(new ScaledProblem(new DTLZ1(3), 10), 400, NondominatedPopulation.loadReferenceSet(new File("DTLZ1(3)-PFscaled.txt")));
		evaluate(new ScaledProblem(new DTLZ1(5), 10), 600, NondominatedPopulation.loadReferenceSet(new File("DTLZ1(5)-PFscaled.txt")));
		evaluate(new ScaledProblem(new DTLZ1(8), 3), 750, NondominatedPopulation.loadReferenceSet(new File("DTLZ1(8)-PFscaled.txt")));
		evaluate(new ScaledProblem(new DTLZ1(10), 2), 1000, NondominatedPopulation.loadReferenceSet(new File("DTLZ1(10)-PFscaled.txt")));
		evaluate(new ScaledProblem(new DTLZ1(15), 1.2), 1500, NondominatedPopulation.loadReferenceSet(new File("DTLZ1(15)-PFscaled.txt")));

		evaluate(new ScaledProblem(new DTLZ2(3), 10), 250, NondominatedPopulation.loadReferenceSet(new File("DTLZ2(3)-PFscaled.txt")));
		evaluate(new ScaledProblem(new DTLZ2(5), 10), 350, NondominatedPopulation.loadReferenceSet(new File("DTLZ2(5)-PFscaled.txt")));
		evaluate(new ScaledProblem(new DTLZ2(8), 3), 500, NondominatedPopulation.loadReferenceSet(new File("DTLZ2(8)-PFscaled.txt")));
		evaluate(new ScaledProblem(new DTLZ2(10), 3), 750, NondominatedPopulation.loadReferenceSet(new File("DTLZ2(10)-PFscaled.txt")));
		evaluate(new ScaledProblem(new DTLZ2(15), 2), 1000, NondominatedPopulation.loadReferenceSet(new File("DTLZ2(15)-PFscaled.txt")));
	}
	
	private void evaluate(Problem problem, int maxGen, NondominatedPopulation referenceSet) {
		int trials = 20;
		double[] igdValues = new double[trials];
		
		InvertedGenerationalDistance igd = new InvertedGenerationalDistance(problem, referenceSet, 2.0);
		
		for (int i = 0; i < trials; i++) {
			int populationSize = 0;
			
			if (problem.getNumberOfObjectives() == 3) {
				populationSize = 92;
			} else if (problem.getNumberOfObjectives() == 5) {
				populationSize = 212;
			} else if (problem.getNumberOfObjectives() == 8) {
				populationSize = 156;
			} else if (problem.getNumberOfObjectives() == 10) {
				populationSize = 276;
			} else if (problem.getNumberOfObjectives() == 15) {
				populationSize = 136;
			}
			
			if (populationSize <= 0) {
				Assert.fail("Unsupported number of objectives");
			}
			
			TypedProperties properties = new TypedProperties();
			properties.setDouble("sbx.rate", 1.0);
			properties.setDouble("sbx.distributionIndex", 30.0);
			properties.setDouble("pm.distributionIndex", 20.0);
			properties.setBoolean("sbx.swap", false);
			properties.setDouble("populationSize", populationSize);
			
			if (problem.getNumberOfObjectives() == 3) {
				properties.setInt("divisions", 12);
			} else if (problem.getNumberOfObjectives() == 5) {
				properties.setInt("divisions", 6);
			} else if (problem.getNumberOfObjectives() == 8) {
				properties.setInt("divisionsOuter", 3);
				properties.setInt("divisionsInner", 2);
			} else if (problem.getNumberOfObjectives() == 10) {
				properties.setInt("divisionsOuter", 3);
				properties.setInt("divisionsInner", 2);
			} else if (problem.getNumberOfObjectives() == 15) {
				properties.setInt("divisionsOuter", 2);
				properties.setInt("divisionsInner", 1);
			}
			
			Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("NSGAIII", properties, problem);
			algorithm.run(maxGen*populationSize);
			
			NondominatedPopulation result = algorithm.getResult();
			igdValues[i] = igd.evaluate(result);
		}
		
		System.out.println(problem.getName() + " " + problem.getNumberOfObjectives());
		System.out.println("  Min: " + new Min().evaluate(igdValues));
		System.out.println("  Med: " + new Median().evaluate(igdValues));
		System.out.println("  Max: " + new Max().evaluate(igdValues));
	}
	
}

package org.moeaframework.algorithm;

import java.io.File;
import java.io.IOException;

import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.InvertedGenerationalDistance;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.problem.ScaledProblem;
import org.moeaframework.problem.DTLZ.DTLZ1;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.DTLZ.DTLZ3;
import org.moeaframework.problem.DTLZ.DTLZ4;
import org.moeaframework.util.TypedProperties;

public class TestNSGAIII {
	
	@Test
	@Ignore("Must download reference sets from http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm")
	public void test() throws IOException {
		evaluate(new DTLZ1(3), 400, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ1(3)-PF.txt"))));
		evaluate(new DTLZ1(5), 600, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ1(5)-PF.txt"))));
		evaluate(new DTLZ1(8), 750, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ1(8)-PF.txt"))));
		evaluate(new DTLZ1(10), 1000, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ1(10)-PF.txt"))));
		evaluate(new DTLZ1(15), 1500, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ1(15)-PF.txt"))));

		evaluate(new DTLZ2(3), 250, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ2(3)-PF.txt"))));
		evaluate(new DTLZ2(5), 350, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ2(5)-PF.txt"))));
		evaluate(new DTLZ2(8), 500, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ2(8)-PF.txt"))));
		evaluate(new DTLZ2(10), 750, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ2(10)-PF.txt"))));
		evaluate(new DTLZ2(15), 1000, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ2(15)-PF.txt"))));
		
		evaluate(new DTLZ3(3), 1000, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ3(3)-PF.txt"))));
		evaluate(new DTLZ3(5), 1000, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ3(5)-PF.txt"))));
		evaluate(new DTLZ3(8), 1000, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ3(8)-PF.txt"))));
		evaluate(new DTLZ3(10), 1500, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ3(10)-PF.txt"))));
		evaluate(new DTLZ3(15), 2000, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ3(15)-PF.txt"))));
		
		evaluate(new DTLZ4(3), 600, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ4(3)-PF.txt"))));
		evaluate(new DTLZ4(5), 1000, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ4(5)-PF.txt"))));
		evaluate(new DTLZ4(8), 1250, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ4(8)-PF.txt"))));
		evaluate(new DTLZ4(10), 2000, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ4(10)-PF.txt"))));
		evaluate(new DTLZ4(15), 3000, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ4(15)-PF.txt"))));
		
		new ScaledProblem(new DTLZ1(3), 10).scaleReferenceSet(new File("DTLZ1(3)-PF.txt"), new File("DTLZ1(3)-PFscaled.txt"));
		new ScaledProblem(new DTLZ1(5), 10).scaleReferenceSet(new File("DTLZ1(5)-PF.txt"), new File("DTLZ1(5)-PFscaled.txt"));
		new ScaledProblem(new DTLZ1(8), 3).scaleReferenceSet(new File("DTLZ1(8)-PF.txt"), new File("DTLZ1(8)-PFscaled.txt"));
		new ScaledProblem(new DTLZ1(10), 2).scaleReferenceSet(new File("DTLZ1(10)-PF.txt"), new File("DTLZ1(10)-PFscaled.txt"));
		new ScaledProblem(new DTLZ1(15), 1.2).scaleReferenceSet(new File("DTLZ1(15)-PF.txt"), new File("DTLZ1(15)-PFscaled.txt"));
		new ScaledProblem(new DTLZ2(3), 10).scaleReferenceSet(new File("DTLZ2(3)-PF.txt"), new File("DTLZ2(3)-PFscaled.txt"));
		new ScaledProblem(new DTLZ2(5), 10).scaleReferenceSet(new File("DTLZ2(5)-PF.txt"), new File("DTLZ2(5)-PFscaled.txt"));
		new ScaledProblem(new DTLZ2(8), 3).scaleReferenceSet(new File("DTLZ2(8)-PF.txt"), new File("DTLZ2(8)-PFscaled.txt"));
		new ScaledProblem(new DTLZ2(10), 3).scaleReferenceSet(new File("DTLZ2(10)-PF.txt"), new File("DTLZ2(10)-PFscaled.txt"));
		new ScaledProblem(new DTLZ2(15), 2).scaleReferenceSet(new File("DTLZ2(15)-PF.txt"), new File("DTLZ2(15)-PFscaled.txt"));
		
		evaluate(new ScaledProblem(new DTLZ1(3), 10), 400, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ1(3)-PFscaled.txt"))));
		evaluate(new ScaledProblem(new DTLZ1(5), 10), 600, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ1(5)-PFscaled.txt"))));
		evaluate(new ScaledProblem(new DTLZ1(8), 3), 750, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ1(8)-PFscaled.txt"))));
		evaluate(new ScaledProblem(new DTLZ1(10), 2), 1000, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ1(10)-PFscaled.txt"))));
		evaluate(new ScaledProblem(new DTLZ1(15), 1.2), 1500, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ1(15)-PFscaled.txt"))));

		evaluate(new ScaledProblem(new DTLZ2(3), 10), 250, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ2(3)-PFscaled.txt"))));
		evaluate(new ScaledProblem(new DTLZ2(5), 10), 350, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ2(5)-PFscaled.txt"))));
		evaluate(new ScaledProblem(new DTLZ2(8), 3), 500, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ2(8)-PFscaled.txt"))));
		evaluate(new ScaledProblem(new DTLZ2(10), 3), 750, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ2(10)-PFscaled.txt"))));
		evaluate(new ScaledProblem(new DTLZ2(15), 2), 1000, new NondominatedPopulation(PopulationIO.readObjectives(new File("DTLZ2(15)-PFscaled.txt"))));
		
	}
	
	public void evaluate(Problem problem, int maxGen, NondominatedPopulation referenceSet) {
		int trials = 20;
		double[] igdValues = new double[trials];
		
		InvertedGenerationalDistance igd = new InvertedGenerationalDistance(
				problem, referenceSet);
		
		for (int i = 0; i < trials; i++) {
			int populationSize;
			
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
			} else {
				throw new IllegalArgumentException();
			}
			
			TypedProperties properties = new TypedProperties();
			properties.setDouble("sbx.rate", 1.0);
			properties.setDouble("sbx.distributionIndex", 30.0);
			properties.setDouble("pm.distributionIndex", 20.0);
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
			
			Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
					"NSGAIII", properties.getProperties(), problem);
			
			while (algorithm.getNumberOfEvaluations() < maxGen*populationSize) {
				algorithm.step();
			}
			
			NondominatedPopulation result = algorithm.getResult();
			igdValues[i] = igd.evaluate(result);
		}
		
		System.out.println(problem.getName() + " " + problem.getNumberOfObjectives());
		System.out.println("  Min: " + new Min().evaluate(igdValues));
		System.out.println("  Med: " + new Median().evaluate(igdValues));
		System.out.println("  Max: " + new Max().evaluate(igdValues));
	}

}

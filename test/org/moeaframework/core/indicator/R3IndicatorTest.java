package org.moeaframework.core.indicator;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link R3Indicator} class.  The raw values used here were produced
 * using the PISA r-ind.exe program.
 */
public class R3IndicatorTest {
	
	@Test
	public void testZero() throws IOException {
		NondominatedPopulation referenceSet = new NondominatedPopulation(
				PopulationIO.readObjectives(new File("./pf/DTLZ2.2D.pf")));
		
		R3Indicator indicator = new R3Indicator(new MockRealProblem(), 500, referenceSet);
		Assert.assertEquals(0.0, indicator.evaluate(referenceSet), 0.000001);
	}
	
	@Test
	public void testDominance() {
		NondominatedPopulation referenceSet = new NondominatedPopulation();
		referenceSet.add(TestUtils.newSolution(0.0, 1.0));
		referenceSet.add(TestUtils.newSolution(1.0, 0.0));
		
		R3Indicator indicator = new R3Indicator(new MockRealProblem(), 2, referenceSet);
		
		NondominatedPopulation population1 = new NondominatedPopulation();
		population1.add(TestUtils.newSolution(0.75, 0.25));
		population1.add(TestUtils.newSolution(0.25, 0.75));
		
		NondominatedPopulation population2 = new NondominatedPopulation();
		population2.add(TestUtils.newSolution(0.5, 0.5));
		
		Assert.assertTrue(indicator.evaluate(population1) < indicator.evaluate(population2));
	}

	@Test
	public void testCase() throws IOException {
		NondominatedPopulation referenceSet = new NondominatedPopulation(
				PopulationIO.readObjectives(new File("./pf/DTLZ2.2D.pf")));
		
		R3Indicator indicator = new R3Indicator(new MockRealProblem(), 500, referenceSet);
		
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(TestUtils.newSolution(0.75, 0.25));
		population.add(TestUtils.newSolution(0.25, 0.75));
		
		Assert.assertEquals(2.878560650e-002, indicator.evaluate(population), 0.000001);
	}
	
}

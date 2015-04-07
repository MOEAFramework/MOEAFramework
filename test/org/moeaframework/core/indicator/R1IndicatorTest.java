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
 * Tests the {@link R1Indicator} class.  The raw values used here were produced
 * using the PISA r-ind.exe program.
 */
public class R1IndicatorTest {

	@Test
	public void testZero() throws IOException {
		NondominatedPopulation referenceSet = new NondominatedPopulation(
				PopulationIO.readObjectives(new File("./pf/DTLZ2.2D.pf")));
		
		R1Indicator indicator = new R1Indicator(new MockRealProblem(), 500, referenceSet);
		Assert.assertEquals(0.5, indicator.evaluate(referenceSet), 0.000001);
	}
	
	@Test
	public void testSet() throws IOException {
		NondominatedPopulation referenceSet = new NondominatedPopulation(
				PopulationIO.readObjectives(new File("./pf/DTLZ2.2D.pf")));
		
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(TestUtils.newSolution(0.75, 0.25));
		population.add(TestUtils.newSolution(0.25, 0.75));
		
		R1Indicator indicator = new R1Indicator(new MockRealProblem(), 500, referenceSet);
		
		Assert.assertEquals(5.269461078e-001, indicator.evaluate(population), 0.000001);
	}
	
}

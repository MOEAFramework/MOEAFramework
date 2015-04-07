package org.moeaframework.core.indicator;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link RIndicator} class.  The raw values used here were produced
 * using the PISA r-ind.exe program.
 */
public class RIndicatorTest {
	
	@Test
	public void testChebychev() throws IOException {
		testUtility("./pf/DTLZ2.2D.pf", new RIndicator.ChebychevUtility(), 0.782451);
	}
	
	@Test
	public void testLinearWeights() throws IOException {
		testUtility("./pf/DTLZ2.2D.pf", new RIndicator.LinearWeightedSumUtility(), 0.750497);
	}
	
	public void testUtility(String file, RIndicator.UtilityFunction utilityFunction, double expectedUtility) throws IOException {
		NondominatedPopulation referenceSet = new NondominatedPopulation(
				PopulationIO.readObjectives(new File(file)));
		
		RIndicator indicator = new RIndicator(new MockRealProblem(), 500, referenceSet, utilityFunction) {

			@Override
			public double evaluate(NondominatedPopulation approximationSet) {
				throw new UnsupportedOperationException();
			}
			
		};
		
		Assert.assertEquals(expectedUtility, indicator.expectedUtility(referenceSet), 0.000001);
	}

}

package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.Vector;

public class ZCAT2Test {

	@Test
	public void test() {
		Problem problem = new ZCAT2(3);
		
		Assert.assertArrayEquals(new double[] { 0.899010, 3.319721, 9.681510 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.068842, 13.617866, 39.546382 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.557605, 12.335284, 28.544560 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.510119, 6.700950, 8.757382 }, 
				TestUtils.evaluateAt(problem, 0.409519, -0.399485, -0.598600, 1.475202, 2.129033, 0.389652, 1.588484, 2.721019, 2.726736, -0.989393, 0.971742, 0.208670, -4.267586, -1.728728, 5.776280, 7.685571, -3.674203, -6.267180, -3.172444, 4.419399, -5.321201, -10.188576, -7.062947, 1.830685, -12.292856, -2.882929, 4.314947, 12.395295, 12.192725, 3.839024).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		TestUtils.assertGeneratedSolutionsAreNondominated(new ZCAT2(3), 1000);
	}
	
	@Test
	public void testProvider() {
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("ZCAT2_2"));
		Assert.assertNotNull(ProblemFactory.getInstance().getReferenceSet("ZCAT2_2"));
		Assert.assertEquals(2, ProblemFactory.getInstance().getProblem("ZCAT2_2").getNumberOfObjectives());
		
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("ZCAT2_3"));
		Assert.assertNull(ProblemFactory.getInstance().getReferenceSet("ZCAT2_3"));
		Assert.assertEquals(3, ProblemFactory.getInstance().getProblem("ZCAT2_3").getNumberOfObjectives());
	}
	
}

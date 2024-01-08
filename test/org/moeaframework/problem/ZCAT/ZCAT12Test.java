package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.Vector;

public class ZCAT12Test {

	@Test
	public void test() {
		Problem problem = new ZCAT12(3);
		
		Assert.assertArrayEquals(new double[] { 1.479003, 6.240014, 11.794938 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.231115, 17.230625, 38.794938 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.226891, 17.249402, 29.794938 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.731858, 7.113652, 15.453434 }, 
				TestUtils.evaluateAt(problem, 0.009590, -0.101814, 0.785681, 1.185700, -0.803837, -1.051418, 1.822063, -2.512954, -4.353886, 3.661911, -0.085392, -5.295348, 4.885863, 5.771219, 4.500573, -6.335023, -4.885735, 2.058493, -3.675596, 3.084957, -6.064603, 4.178104, 1.701042, 4.046147, -11.800635, 0.519961, -1.976740, 12.116186, 10.934561, -14.943509).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		TestUtils.assertGeneratedSolutionsAreNondominated(new ZCAT12(3), 1000);
	}
	
	@Test
	public void testProvider() {
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("ZCAT12_2"));
		Assert.assertNotNull(ProblemFactory.getInstance().getReferenceSet("ZCAT12_2"));
		Assert.assertEquals(2, ProblemFactory.getInstance().getProblem("ZCAT12_2").getNumberOfObjectives());
		
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("ZCAT12_3"));
		Assert.assertNull(ProblemFactory.getInstance().getReferenceSet("ZCAT12_3"));
		Assert.assertEquals(3, ProblemFactory.getInstance().getProblem("ZCAT12_3").getNumberOfObjectives());
	}
	
}

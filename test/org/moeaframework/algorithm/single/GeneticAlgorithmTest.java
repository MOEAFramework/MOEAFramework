package org.moeaframework.algorithm.single;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.util.TypedProperties;

public class GeneticAlgorithmTest {
	
	@Test
	public void testDefaultComparator() {
		GeneticAlgorithm ga = new GeneticAlgorithm(new MockRealProblem());
		
		Assert.assertNotNull(ga.getComparator());
		Assert.assertEquals(ga.getComparator(), ga.getSelection().getComparator());
	}
	
	@Test
	public void testComparator() {
		GeneticAlgorithm ga = new GeneticAlgorithm(new MockRealProblem());
		AggregateObjectiveComparator oldComparator = ga.getComparator();
		
		AggregateObjectiveComparator newComparator = new MinMaxDominanceComparator();
		ga.setComparator(newComparator);
		
		Assert.assertNotEquals(oldComparator, ga.getComparator());
		Assert.assertEquals(newComparator, ga.getComparator());
		Assert.assertEquals(newComparator, ga.getSelection().getComparator());
	}
	
	@Test
	public void testApplyConfiguration() {
		GeneticAlgorithm ga = new GeneticAlgorithm(new MockRealProblem());
		AggregateObjectiveComparator oldComparator = ga.getComparator();
		
		ga.applyConfiguration(TypedProperties.withProperty("method", "min-max"));
		
		Assert.assertNotEquals(oldComparator, ga.getComparator());
		Assert.assertTrue(ga.getComparator() instanceof MinMaxDominanceComparator);
		Assert.assertEquals(ga.getComparator(), ga.getSelection().getComparator());
	}

}

package org.moeaframework.problem;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;

/**
 * Tests the {@link ClassLoaderProblems} class.
 */
public class ClassLoaderProblemsTest {
	
	@Test
	public void testClassNotFound() {
		String problem = "org.moeaframework.problem.NonExistantProblem";
		Assert.assertNull(new ClassLoaderProblems().getProblem(problem));
		Assert.assertNull(new ClassLoaderProblems().getReferenceSet(problem));
	}
	
	@Test
	public void testClassFound() {
		String problem = "org.moeaframework.problem.misc.Kita";
		Assert.assertNotNull(new ClassLoaderProblems().getProblem(problem));
		Assert.assertNull(new ClassLoaderProblems().getReferenceSet(problem));
	}
	
	@Test(expected=ProviderNotFoundException.class)
	public void testProblemFactoryClassNotFound() {
		String className = "org.moeaframework.problem.NonExistantProblem";
		ProblemFactory.getInstance().getProblem(className);
	}
	
	@Test
	public void testProblemFactoryClassFound() {
		String problem = "org.moeaframework.problem.misc.Kita";
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem(problem));
		Assert.assertNull(ProblemFactory.getInstance().getReferenceSet(
				problem));
	}

}

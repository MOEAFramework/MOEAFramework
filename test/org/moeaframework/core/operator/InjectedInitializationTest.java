package org.moeaframework.core.operator;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.MockRealProblem;

public class InjectedInitializationTest {
	
	@Test
	public void testInjection() {
		Problem problem = new MockRealProblem();
		Solution solution = problem.newSolution();
		
		InjectedInitialization initialization = new InjectedInitialization(
				problem, 100, solution);
		
		Solution[] solutions = initialization.initialize();
		boolean found = false;
		
		for (Solution s : solutions) {
			if (TestUtils.equals(s, solution)) {
				found = true;
				break;
			}
		}
		
		Assert.assertTrue(found);
	}

}

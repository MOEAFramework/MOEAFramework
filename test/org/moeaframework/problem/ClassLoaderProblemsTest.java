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
package org.moeaframework.problem;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.mock.MockRealProblem;

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
	
	@Test
	public void testInnerClass() {
		String problem = "org.moeaframework.problem.ClassLoaderProblemsTest$MockInnerProblem";
		Assert.assertNotNull(new ClassLoaderProblems().getProblem(problem));
		Assert.assertNull(new ClassLoaderProblems().getReferenceSet(problem));
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testNoConstructor() {
		String problem = "org.moeaframework.problem.ClassLoaderProblemsTest$MockProblemWithoutConstructor";
		Assert.assertNotNull(new ClassLoaderProblems().getProblem(problem));
		Assert.assertNull(new ClassLoaderProblems().getReferenceSet(problem));
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testProblemFactoryClassNotFound() {
		String className = "org.moeaframework.problem.NonExistantProblem";
		ProblemFactory.getInstance().getProblem(className);
	}
	
	public static class MockInnerProblem extends MockRealProblem {
		
	}
	
	public static class MockProblemWithoutConstructor extends MockRealProblem {
		
		private MockProblemWithoutConstructor() {
			super();
		}
		
	}

}

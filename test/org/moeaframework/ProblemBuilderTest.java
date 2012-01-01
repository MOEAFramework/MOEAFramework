/* Copyright 2009-2012 David Hadka
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
package org.moeaframework;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.moeaframework.ProblemBuilder;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.spi.TestProblemFactory;
import org.moeaframework.problem.ZDT.ZDT5;

/**
 * Tests the {@link ProblemBuilder} class.
 */
public class ProblemBuilderTest {
	
	@Test(expected = IllegalArgumentException.class)
	public void testNoProblem1() throws IOException {
		new ProblemBuilder().getProblemInstance();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNoProblem2() throws IOException {
		new ProblemBuilder().getReferenceSet();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNoReferenceSet() throws IOException {
		//ZDT5 has no reference set
		new ProblemBuilder().withProblem("ZDT5").getReferenceSet();
	}
	
	@Test
	public void testProblemFactory() {
		TestProblemFactory problemFactory = new TestProblemFactory();
		ProblemBuilder builder = new ProblemBuilder()
				.usingProblemFactory(problemFactory)
				.withProblem("DTLZ2_2");
		
		builder.getReferenceSet();
		builder.getProblemInstance().close();
		
		Assert.assertEquals(1, problemFactory.getCloseCount());
	}
	
	@Test
	public void testGetProblemInstance() throws ClassNotFoundException {
		ProblemBuilder builder = new ProblemBuilder();
		
		Assert.assertNotNull(builder.withProblem("ZDT5").getProblemInstance());
		
		Assert.assertNotNull(builder.withProblemClass(ZDT5.class)
				.getProblemInstance());
		
		Assert.assertNotNull(builder.withProblemClass(
				"org.moeaframework.problem.ZDT.ZDT5").getProblemInstance());
	}
	
	@Test
	public void testReferenceSetFromProblemFactory() throws IOException {
		NondominatedPopulation actual = new ProblemBuilder()
				.withEpsilon(0.01)
				.withProblem("DTLZ2_2")
				.getReferenceSet();

		NondominatedPopulation expected = new EpsilonBoxDominanceArchive(0.01,
				PopulationIO.readObjectives(new File("./pf/DTLZ2.2D.pf")));
		
		TestUtils.assertEquals(expected, actual);
	}
	
	@Test
	public void testReferenceSetFromFile() throws IOException {
		//purposely load DTLZ1 reference set
		NondominatedPopulation actual = new ProblemBuilder()
				.withProblem("DTLZ2_2")
				.withReferenceSet(new File("./pf/DTLZ1.2D.pf"))
				.getReferenceSet();
		
		NondominatedPopulation expected = new NondominatedPopulation(
				PopulationIO.readObjectives(new File("./pf/DTLZ1.2D.pf")));
		
		TestUtils.assertEquals(expected, actual);
	}

}

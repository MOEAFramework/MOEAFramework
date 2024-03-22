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
package org.moeaframework.algorithm;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Retryable;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.mock.MockRealProblem;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.CIRunner;

/**
 * The MOEA Framework's implementation tends to outperform the JMetal implementation.
 */
@RunWith(CIRunner.class)
@Retryable
public class PAESTest extends AlgorithmTest {
	
	@Test
	public void testDTLZ1() throws IOException {
		assumeJMetalExists();
		test("DTLZ1_2", "PAES", "PAES-JMetal", true);
	}
	
	@Test
	public void testDTLZ2() throws IOException {
		assumeJMetalExists();
		test("DTLZ2_2", "PAES", "PAES-JMetal", true);
	}
	
	@Test
	public void testDTLZ7() throws IOException {
		assumeJMetalExists();
		test("DTLZ7_2", "PAES", "PAES-JMetal", true);
	}
	
	@Test
	public void testUF1() throws IOException {
		assumeJMetalExists();
		test("UF1", "PAES", "PAES-JMetal", true);
	}
	
	@Test
	public void testConfiguration() {
		Problem problem = new MockRealProblem(2);	
		PAES algorithm = new PAES(problem);
		
		Assert.assertEquals(algorithm.getArchive().getCapacity(), algorithm.getConfiguration().getInt("archiveSize"));
		Assert.assertEquals(algorithm.getArchive().getBisections(), algorithm.getConfiguration().getInt("bisections"));
		
		TypedProperties properties = new TypedProperties();
		properties.setInt("archiveSize", 200);
		properties.setInt("bisections", 3);
		
		algorithm.applyConfiguration(properties);
		Assert.assertEquals(200, algorithm.getArchive().getCapacity());
		Assert.assertEquals(3, algorithm.getArchive().getBisections());
	}

}

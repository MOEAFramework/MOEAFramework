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

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Assume;

/**
 * Methods for comparing against the JMetal implementation.  This performs a statistical comparison of the algorithms
 * using end-of-run performance indicators.  We typically want to see the algorithms produce statistically similar
 * results, but in specific cases we allow better performance.
 */
@Ignore("Abstract test class")
public abstract class JMetalAlgorithmTest extends AlgorithmTest {
	
	protected final String algorithmName;
	
	protected final boolean allowBetterPerformance;
	
	public JMetalAlgorithmTest(String algorithmName) {
		this(algorithmName, false);
	}
	
	public JMetalAlgorithmTest(String algorithmName, boolean allowBetterPerformance) {
		super();
		this.algorithmName = algorithmName;
		this.allowBetterPerformance = allowBetterPerformance;
	}
	
	@Test
	public void testDTLZ1() throws IOException {
		Assume.assumeJMetalExists();
		test("DTLZ1_2", algorithmName, algorithmName + "-JMetal", allowBetterPerformance);
	}
	
	@Test
	public void testDTLZ2() throws IOException {
		Assume.assumeJMetalExists();
		test("DTLZ2_2", algorithmName, algorithmName + "-JMetal", allowBetterPerformance);
	}
	
	@Test
	public void testDTLZ7() throws IOException {
		Assume.assumeJMetalExists();
		test("DTLZ7_2", algorithmName, algorithmName + "-JMetal", allowBetterPerformance);
	}
	
	@Test
	public void testUF1() throws IOException {
		Assume.assumeJMetalExists();
		test("UF1", algorithmName, algorithmName + "-JMetal", allowBetterPerformance);
	}

}

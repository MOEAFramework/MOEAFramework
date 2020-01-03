/* Copyright 2009-2019 David Hadka
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.CIRunner;
import org.moeaframework.Flaky;
import org.moeaframework.Retryable;

/**
 * Tests the {@link GDE3} class.
 */
@RunWith(CIRunner.class)
@Retryable
public class GDE3Test extends AlgorithmTest {
	
	@Test
	@Flaky("need to investigate - differences showing up after upgrading to JMetal 5.9")
	public void testDTLZ1() throws IOException {
		test("DTLZ1_2", "GDE3", "GDE3-JMetal");
	}
	
	@Test
	@Flaky("need to investigate - differences showing up after upgrading to JMetal 5.9")
	public void testDTLZ2() throws IOException {
		test("DTLZ2_2", "GDE3", "GDE3-JMetal");
	}
	
	@Test
	public void testDTLZ7() throws IOException {
		test("DTLZ7_2", "GDE3", "GDE3-JMetal");
	}
	
	@Test
	public void testUF1() throws IOException {
		test("UF1", "GDE3", "GDE3-JMetal");
	}

}

/* Copyright 2009-2011 David Hadka
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
import java.util.Properties;

import org.junit.Test;
import org.moeaframework.algorithm.jmetal.JMetalAlgorithms;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;

/**
 * Tests the {@link GDE3} class.
 */
public class GDE3Test extends AlgorithmTest {
	
	private static class GDE3Factory extends AlgorithmFactory {

		@Override
		public synchronized Algorithm getAlgorithm(String name,
				Properties properties, Problem problem) {
			if (name.equalsIgnoreCase("GDE3-JMetal")) {
				return new JMetalAlgorithms().getAlgorithm("GDE3", properties, problem);
			} else {
				return super.getAlgorithm(name, properties, problem);
			}
		}
		
	}
	
	@Test
	public void testDTLZ1() throws IOException {
		test("DTLZ1_2", "GDE3", "GDE3-JMetal", new GDE3Factory());
	}
	
	@Test
	public void testDTLZ2() throws IOException {
		test("DTLZ2_2", "GDE3", "GDE3-JMetal", new GDE3Factory());
	}
	
	@Test
	public void testUF1() throws IOException {
		test("UF1", "GDE3", "GDE3-JMetal", new GDE3Factory());
	}

}

/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.algorithm.extension;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.mock.MockAlgorithm;
import org.moeaframework.mock.MockAlgorithmWithExtensions;

public class PeriodicExtensionTest {
	
	public static class TestPeriodicExtension extends PeriodicExtension {
		
		private int numberOfActions;

		public TestPeriodicExtension(Frequency frequency) {
			super(frequency);
		}

		@Override
		public void doAction(Algorithm algorithm) {
			numberOfActions++;
		}

		public int getNumberOfActions() {
			return numberOfActions;
		}
		
	}

	@Test
	public void testEvaluationsExact() {
		test(Frequency.ofEvaluations(10), 1000);
	}
	
	@Test
	public void testEvaluationsLarger() {
		test(Frequency.ofEvaluations(100), 100);
	}
	
	@Test
	public void testEvaluationsSmaller() {
		test(Frequency.ofEvaluations(1), 1000);
	}
	
	@Test
	public void testEvaluationsMax() {
		test(Frequency.ofEvaluations(10000), 1);
	}
	
	@Test
	public void testEvaluationsUneven() {
		test(Frequency.ofEvaluations(34), 250);
	}
	
	@Test
	public void testStepsExact() {
		test(Frequency.ofIterations(1), 1000);
	}
	
	@Test
	public void testStepsLarger() {
		test(Frequency.ofIterations(10), 100);
	}
	
	@Test
	public void testStepsMax() {
		test(Frequency.ofIterations(1000), 1);
	}
	
	public void test(Frequency frequency, int expected) {
		MockAlgorithm algorithm = new MockAlgorithmWithExtensions();
		
		TestPeriodicExtension extension = new TestPeriodicExtension(frequency);
		algorithm.addExtension(extension);
		
		
		for (int i=0; i<1000; i++) {
			algorithm.step();
		}
		
		Assert.assertEquals(expected, extension.getNumberOfActions());
	}
	
}

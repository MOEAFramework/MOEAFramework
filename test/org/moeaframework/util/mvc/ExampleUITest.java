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
package org.moeaframework.util.mvc;

import java.time.Duration;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.Wait;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Settings;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

public class ExampleUITest {
	
	static class TestExampleUI extends ExampleUI<NSGAII> {
		
		private static final long serialVersionUID = 9061186330605849493L;
		
		private int updateCalls;

		public TestExampleUI(String title, NSGAII algorithm) {
			super(title, algorithm);
		}

		@Override
		public void update(NSGAII algorithm, int iteration) {
			updateCalls++;
		}
		
		public int getUpdateCalls() {
			return updateCalls;
		}
		
	}
	
	@Test
	public void test() throws InterruptedException {
		Assume.assumeHasDisplay();
		
		Problem problem = new UF1();
		NSGAII algorithm = new NSGAII(problem);
		
		TestExampleUI example = new TestExampleUI("Test", algorithm);
		example.start();
		
		Wait.sleepFor(Duration.ofMillis(100));
		
		example.stop();
		
		Assert.assertGreaterThan(example.getUpdateCalls(), 0);
		Assert.assertEquals(algorithm.getNumberOfEvaluations(), Settings.DEFAULT_POPULATION_SIZE * example.getUpdateCalls());
	}
	
}

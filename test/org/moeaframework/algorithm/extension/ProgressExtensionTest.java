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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Wait;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.extension.ProgressExtension.ProgressEvent;
import org.moeaframework.core.termination.MaxFunctionEvaluations;
import org.moeaframework.mock.MockAlgorithm;

public class ProgressExtensionTest {
	
	@Test
	public void test() {
		final List<ProgressEvent> events = new ArrayList<>();
		
		ProgressExtension extension = new ProgressExtension();
		extension.addListener(event -> events.add(event));
		
		Algorithm algorithm = new MockAlgorithm();
		
		extension.onRegister(algorithm);
		extension.onRun(algorithm, new MaxFunctionEvaluations(40));
		Wait.spinFor(Duration.ofMillis(50));
		algorithm.step();
		extension.onInitialize(algorithm);
		Wait.spinFor(Duration.ofMillis(50));
		algorithm.step();
		extension.onStep(algorithm);
		Wait.spinFor(Duration.ofMillis(50));
		algorithm.step();
		extension.onStep(algorithm);
		Wait.spinFor(Duration.ofMillis(50));
		algorithm.step();
		extension.onStep(algorithm);
		extension.onTerminate(algorithm);
		
		Assert.assertSize(5, events);
		
		Assert.assertEquals(25.0, events.get(0).getPercentComplete());
		Assert.assertGreaterThan(events.get(0).getElapsedTime().toMillis(), 0L);
		Assert.assertGreaterThan(events.get(0).getRemainingTime().toMillis(), 0L);
		
		Assert.assertEquals(50.0, events.get(1).getPercentComplete());
		Assert.assertGreaterThan(events.get(1).getElapsedTime().toMillis(), events.get(0).getElapsedTime().toMillis());
		
		Assert.assertEquals(75.0, events.get(2).getPercentComplete());
		Assert.assertGreaterThan(events.get(2).getElapsedTime().toMillis(), events.get(1).getElapsedTime().toMillis());
		
		Assert.assertEquals(100.0, events.get(3).getPercentComplete());
		Assert.assertGreaterThan(events.get(3).getElapsedTime().toMillis(), events.get(2).getElapsedTime().toMillis());
		Assert.assertEquals(0L, events.get(3).getRemainingTime().toMillis());
	}

}

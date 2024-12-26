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
package org.moeaframework.analysis.sensitivity;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.sensitivity.SobolSensitivityAnalysis.SobolSensitivityResult;

public class SobolSensitivityAnalysisTest {
	
	@Test
	public void testX() {
		SobolSensitivityResult result = test(1000, (x, y, z) -> x);
		
		Assert.assertEquals(1.0, result.getFirstOrder("x").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getFirstOrder("y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getFirstOrder("z").getSensitivity(), TestThresholds.LOW_PRECISION);
		
		Assert.assertEquals(1.0, result.getTotalOrder("x").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getTotalOrder("y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getTotalOrder("z").getSensitivity(), TestThresholds.LOW_PRECISION);
		
		Assert.assertEquals(0.0, result.getSecondOrder("x", "y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getSecondOrder("x", "z").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getSecondOrder("y", "z").getSensitivity(), TestThresholds.LOW_PRECISION);
	}
	
	@Test
	public void testXplusY() {
		SobolSensitivityResult result = test(1000, (x, y, z) -> x + y);
		
		Assert.assertEquals(0.5, result.getFirstOrder("x").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.5, result.getFirstOrder("y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getFirstOrder("z").getSensitivity(), TestThresholds.LOW_PRECISION);
		
		Assert.assertEquals(0.5, result.getTotalOrder("x").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.5, result.getTotalOrder("y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getTotalOrder("z").getSensitivity(), TestThresholds.LOW_PRECISION);
		
		Assert.assertEquals(0.0, result.getSecondOrder("x", "y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getSecondOrder("x", "z").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getSecondOrder("y", "z").getSensitivity(), TestThresholds.LOW_PRECISION);
	}
	
	@Test
	public void testXplusYplusZ() {
		SobolSensitivityResult result = test(1000, (x, y, z) -> x + y + z);
		
		Assert.assertEquals(0.333, result.getFirstOrder("x").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.333, result.getFirstOrder("y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.333, result.getFirstOrder("z").getSensitivity(), TestThresholds.LOW_PRECISION);
		
		Assert.assertEquals(0.333, result.getTotalOrder("x").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.333, result.getTotalOrder("y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.333, result.getTotalOrder("z").getSensitivity(), TestThresholds.LOW_PRECISION);
		
		Assert.assertEquals(0.0, result.getSecondOrder("x", "y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getSecondOrder("x", "z").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getSecondOrder("y", "z").getSensitivity(), TestThresholds.LOW_PRECISION);
	}
	
	@Test
	public void testXtimesYplusZ() {
		SobolSensitivityResult result = test(1000, (x, y, z) -> x * y + z);
		
		Assert.assertEquals(0.159, result.getFirstOrder("x").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.159, result.getFirstOrder("y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.634, result.getFirstOrder("z").getSensitivity(), TestThresholds.LOW_PRECISION);
		
		Assert.assertEquals(0.211, result.getTotalOrder("x").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.211, result.getTotalOrder("y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.634, result.getTotalOrder("z").getSensitivity(), TestThresholds.LOW_PRECISION);
		
		Assert.assertEquals(0.052, result.getSecondOrder("x", "y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getSecondOrder("x", "z").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.0, result.getSecondOrder("y", "z").getSensitivity(), TestThresholds.LOW_PRECISION);
	}
	
	@Test
	public void testXtimesYtimesZ() {
		SobolSensitivityResult result = test(1000, (x, y, z) -> x * y * z);
		
		Assert.assertEquals(0.246, result.getFirstOrder("x").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.246, result.getFirstOrder("y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.246, result.getFirstOrder("z").getSensitivity(), TestThresholds.LOW_PRECISION);
		
		Assert.assertEquals(0.408, result.getTotalOrder("x").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.408, result.getTotalOrder("y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.408, result.getTotalOrder("z").getSensitivity(), TestThresholds.LOW_PRECISION);
		
		Assert.assertEquals(0.081, result.getSecondOrder("x", "y").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.081, result.getSecondOrder("x", "z").getSensitivity(), TestThresholds.LOW_PRECISION);
		Assert.assertEquals(0.081, result.getSecondOrder("y", "z").getSensitivity(), TestThresholds.LOW_PRECISION);
	}
	
	protected SobolSensitivityResult test(int N, TestFunction function) {
		Parameter<Double> x = Parameter.named("x").asDecimal().sampledBetween(0.0, 1.0);
		Parameter<Double> y = Parameter.named("y").asDecimal().sampledBetween(0.0, 1.0);
		Parameter<Double> z = Parameter.named("z").asDecimal().sampledBetween(0.0, 1.0);
		
		ParameterSet parameterSet = new ParameterSet(x, y, z);
		SobolSensitivityAnalysis analysis = new SobolSensitivityAnalysis(parameterSet, N);
		Samples samples = analysis.generateSamples();
		
		Assert.assertEquals(N * (2 * parameterSet.size() + 2), samples.size());
		
		// evaluate the function on each sample
		double[] responses = new double[samples.size()];
		
		for (int i = 0; i < samples.size(); i++) {
			Sample sample = samples.get(i);
			responses[i] = function.eval(x.readValue(sample), y.readValue(sample), z.readValue(sample));
		}
		
		// assign the responses and return the result for testing
		return analysis.evaluate(responses);
	}
	
	@FunctionalInterface
	public interface TestFunction {
		
		public double eval(double x, double y, double z);
		
	}

}

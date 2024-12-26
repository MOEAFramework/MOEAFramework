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
package org.moeaframework.util.weights;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;

public class FixedWeightsTest extends AbstractWeightGeneratorTest<FixedWeights> {
	
	@Override
	public FixedWeights createInstance(int numberOfObjectives) {
		List<double[]> weights = new ArrayList<>();
		
		for (int i = 0; i < numberOfObjectives; i++) {
			double[] weight = new double[numberOfObjectives];
			weight[i] = 1.0;
			weights.add(weight);
		}
		
		return new FixedWeights(weights);
	}
	
	@Override
	public int getExpectedNumberOfSamples(int numberOfObjectives) {
		return numberOfObjectives;
	}
	
	@Test
	public void testInvalidWeights() {
		Assert.assertThrows(IllegalArgumentException.class, () -> new FixedWeights(new double[][] {{ 0.0, 1.1, 0.0 }}));
		Assert.assertThrows(IllegalArgumentException.class, () -> new FixedWeights(new double[][] {{ 0.9, -0.1, 0.0 }}));
		Assert.assertThrows(IllegalArgumentException.class, () -> new FixedWeights(new double[][] {{ 0.1, 0.9, 0.1 }}));
		Assert.assertThrows(IllegalArgumentException.class, () -> new FixedWeights(new double[][] {{ 0.2, 0.4, 0.2 }}));
		Assert.assertThrows(IllegalArgumentException.class, () -> new FixedWeights(new double[][] {{ 1.0, 0.0 }, { 1.0 }}));
	}
	
	@Test
	public void testSaveLoad() throws IOException {
		FixedWeights expected = createInstance(3);
		
		try (StringWriter writer = new StringWriter()) {
			expected.save(writer);
			
			try (StringReader reader = new StringReader(writer.toString())) {
				FixedWeights actual = FixedWeights.load(reader);
				
				List<double[]> expectedWeights = expected.generate();
				List<double[]> actualWeights = actual.generate();
				Assert.assertEquals(expectedWeights.size(), actualWeights.size());
				Assert.assertEquals(expectedWeights.stream().toArray(double[][]::new), actualWeights.stream().toArray(double[][]::new));
			}
		}
	}

}

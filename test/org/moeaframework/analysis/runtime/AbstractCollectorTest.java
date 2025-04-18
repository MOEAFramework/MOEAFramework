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
package org.moeaframework.analysis.runtime;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.DefaultAlgorithms;
import org.moeaframework.algorithm.extension.Frequency;
import org.moeaframework.analysis.series.IndexedResult;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.RegisteredAlgorithmProvider;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

/**
 * Helper functions for testing collectors.  This primarily ensures that the collectors attach to the correct
 * algorithms, and that a single collector does not attach to multiple objects.
 */
@Ignore("Abstract test class")
public abstract class AbstractCollectorTest<T extends Collector> {

	/**
	 * Creates a new instance of the collector being tested.
	 * 
	 * @return the collector
	 */
	public abstract T createInstance();

	/**
	 * Validates the results produced in this test.  This should, at a minimum, attempt to read / parse the results
	 * being collected.
	 * 
	 * @param algorithm the algorithm instance
	 * @param result the result to validate
	 */
	public abstract void validate(Algorithm algorithm, ResultEntry result);

	/**
	 * Returns {@code true} if the collector should be attached to the given algorithm.
	 * 
	 * @param algorithm the algorithm instance
	 * @return {@code true} if the collector should be attached to the given algorithm
	 */
	public abstract boolean shouldAttach(Algorithm algorithm);

	@Test
	public void testAll() {
		testAll(new DefaultAlgorithms());
	}

	@Test
	public void testDefinedAttachPoint() {
		Assert.assertNotNull(createInstance().getAttachPoint());
	}

	protected void testAll(RegisteredAlgorithmProvider provider) {
		for (String algorithmName : provider.getRegisteredAlgorithms()) {
			if (algorithmName.equalsIgnoreCase("RSO")) {
				continue;
			}

			test(algorithmName, createInstance());
		}
	}

	private static class TestCollector implements Collector {

		private final Collector collector;

		private int numberOfAttachments;

		public TestCollector(Collector collector) {
			super();
			this.collector = collector;
		}

		@Override
		public AttachPoint getAttachPoint() {
			return collector.getAttachPoint();
		}

		@Override
		public Collector attach(Object object) {
			numberOfAttachments++;
			return collector.attach(object);
		}

		@Override
		public void collect(ResultEntry result) {
			collector.collect(result);
		}

		public int getNumberOfAttachments() {
			return numberOfAttachments;
		}

	}

	protected void test(String algorithmName, Collector collector) {
		int numberOfEvaluations = 1000;
		Problem problem = new DTLZ2(2);
		
		TestCollector testCollector = new TestCollector(collector);

		Instrumenter instrumenter = new Instrumenter()
				.withFrequency(Frequency.ofEvaluations(100))
				.attach(testCollector);

		TypedProperties properties = new TypedProperties();
		properties.setInt("maxEvaluations", numberOfEvaluations);

		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, properties, problem);
		boolean shouldAttach = shouldAttach(algorithm);

		InstrumentedAlgorithm<?> instrumentedAlgorithm = instrumenter.instrument(algorithm);

		while (instrumentedAlgorithm.getNumberOfEvaluations() < numberOfEvaluations) {
			instrumentedAlgorithm.step();
		}

		ResultSeries series = instrumentedAlgorithm.getSeries();
		instrumentedAlgorithm.terminate();

		Assert.assertEquals(algorithmName + ": incorrect number of attachments",
				shouldAttach ? 1 : 0, testCollector.getNumberOfAttachments());

		Assert.assertNotNull(algorithmName + ": series is null", series);

		if (shouldAttach) {
			Assert.assertTrue(series.size() > 0);

			for (IndexedResult result : series) {
				try {
					validate(algorithm, result.getEntry());
				} catch (Exception e) {
					Assert.fail(algorithmName + ": validation failed - " + e.getMessage());
				}
			}
		}
	}

}

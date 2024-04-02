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
package org.moeaframework.analysis.collector;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Instrumenter;
import org.moeaframework.algorithm.DefaultAlgorithms;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.RegisteredAlgorithmProvider;
import org.moeaframework.util.TypedProperties;

/**
 * Helper functions for testing collectors.  This primarily ensures that the collectors attach to the correct
 * algorithms, and that a single collector does not attach to multiple objects.
 */
public abstract class AbstractCollectorTest<T extends Collector> {
	
	/**
	 * Creates a new instance of the collector being tested.
	 * 
	 * @return the collector
	 */
	public abstract T createInstance();
	
	/**
	 * Validates the observations produced in this test.  This should, at a minimum, attempt to read / parse
	 * the observation being collected.
	 * 
	 * @param observation the observation to validate
	 */
	public abstract void validate(Observation observation);
	
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
		for (String algorithmName : provider.getDiagnosticToolAlgorithms()) {
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
		public void collect(Observation observation) {
			collector.collect(observation);
		}

		public int getNumberOfAttachments() {
			return numberOfAttachments;
		}
		
	}
	
	protected void test(String algorithmName, Collector collector) {
		Observations observations = null;
		boolean shouldAttach = false;
		int numberOfEvaluations = 1000;
		String problemName = "DTLZ2_2";
		TestCollector testCollector = new TestCollector(collector);
		
		try (Problem problem = ProblemFactory.getInstance().getProblem(problemName)) {
			Instrumenter instrumenter = new Instrumenter()
					.withFrequency(100)
					.attach(testCollector);

			TypedProperties properties = new TypedProperties();
			properties.setInt("maxEvaluations", numberOfEvaluations);

			Algorithm algorithm = null;

			try {
				algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, properties, problem);
				shouldAttach = shouldAttach(algorithm);

				InstrumentedAlgorithm instrumentedAlgorithm = instrumenter.instrument(algorithm);

				while (instrumentedAlgorithm.getNumberOfEvaluations() < numberOfEvaluations) {
					instrumentedAlgorithm.step();
				}
				
				observations = instrumentedAlgorithm.getObservations();
			} finally {
				if (algorithm != null) {
					algorithm.terminate();
				}
			}
		}
		
		Assert.assertEquals(algorithmName + ": incorrect number of attachments",
				shouldAttach ? 1 : 0, testCollector.getNumberOfAttachments());
		
		Assert.assertNotNull(algorithmName + ": operations is null", observations);
		
		if (shouldAttach) {
			Assert.assertTrue(observations.size() > 0);
			
			for (Observation observation : observations) {
				try {
					validate(observation);
				} catch (Exception e) {
					Assert.fail(algorithmName + ": validation failed - " + e.getMessage());
				}
			}
		}
	}

}

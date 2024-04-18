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
package org.moeaframework.analysis.diagnostics;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.analysis.collector.Observation;
import org.moeaframework.analysis.collector.Observations;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockSolution;

/**
 * GUI tests have limited scope and, in general, do not validate the content being displayed.
 */
public class ApproximationSetViewerTest {
	
	private Observations observations;
	private NondominatedPopulation referenceSet;
	
	@Before
	public void setUp() {
		Assume.assumeHasDisplay();
		
		observations = new Observations();
		
		for (int i = 0; i < 10; i++) {
			ArrayList<Solution> solutions = new ArrayList<>(List.of(MockSolution.of().withObjectives(0.5, 0.5)));
			
			Observation observation = new Observation(i);
			observation.set("Approximation Set", solutions);
			observations.add(observation);
		}
		
		referenceSet = new NondominatedPopulation();
		referenceSet.add(MockSolution.of().withObjectives(0.0, 1.0));
		referenceSet.add(MockSolution.of().withObjectives(1.0, 0.0));
	}
	
	@After
	public void tearDown() {
		observations = null;
		referenceSet = null;
	}
	
	@Test
	public void testWithReferenceSet() {
		ApproximationSetViewer viewer = new ApproximationSetViewer("Viewer", List.of(observations), referenceSet);
		viewer.setVisible(true);
		viewer.dispose();
	}
	
	@Test
	public void testNoReferenceSet() {
		ApproximationSetViewer viewer = new ApproximationSetViewer("Viewer", List.of(observations), null);
		viewer.setVisible(true);
		viewer.dispose();
	}
	
	@Test
	public void testLocalization() {
		Assert.assertLocalized(new ApproximationSetViewer("Viewer", List.of(observations), referenceSet),
				Assert::isLocalized);
	}

}

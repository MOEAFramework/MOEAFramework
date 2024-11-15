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
package org.moeaframework.analysis.viewer;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.analysis.runtime.Observation;
import org.moeaframework.analysis.runtime.Observations;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.mock.MockSolution;

/**
 * GUI tests have limited scope and, in general, do not validate the content being displayed.
 */
public class RuntimeViewerTest {
	
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
		RuntimeViewer viewer = new RuntimeViewer("Viewer");
		viewer.getController().setReferenceSet(referenceSet);
		viewer.getController().addSeries("Sample", observations);
		viewer.setVisible(true);
		viewer.dispose();
	}
	
	@Test
	public void testNoReferenceSet() {
		RuntimeViewer viewer = new RuntimeViewer("Viewer");
		viewer.getController().addSeries("Sample", observations);
		viewer.setVisible(true);
		viewer.dispose();
	}
	
	@Test
	public void testLocalization() {
		Assert.assertLocalized(new RuntimeViewer("Viewer"), Assert::isLocalized);
	}

}

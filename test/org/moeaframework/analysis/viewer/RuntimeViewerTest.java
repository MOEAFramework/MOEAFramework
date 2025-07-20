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
package org.moeaframework.analysis.viewer;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.analysis.series.IndexType;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.population.Population;
import org.moeaframework.mock.MockSolution;

/**
 * GUI tests have limited scope and, in general, do not validate the content being displayed.
 */
public class RuntimeViewerTest {
	
	@Before
	public void setUp() {
		Assume.assumeHasDisplay();
	}
	
	private void test(IndexType indexType, boolean includeReferenceSet) {
		ResultSeries series = new ResultSeries(indexType);
		
		for (int i = 0; i < 10; i++) {
			Population population = new Population(List.of(MockSolution.of().withObjectives(0.5, 0.5)));
			TypedProperties properties = new TypedProperties();
			
			if (indexType.equals(IndexType.NFE)) {
				properties.setInt(ResultEntry.NFE, 100 * (i + 1));
			}
			
			series.add(new ResultEntry(population, properties));
		}
		
		NondominatedPopulation referenceSet = null;
		
		if (includeReferenceSet) {
			referenceSet = new NondominatedPopulation();
			referenceSet.add(MockSolution.of().withObjectives(0.0, 1.0));
			referenceSet.add(MockSolution.of().withObjectives(1.0, 0.0));
		}
		
		RuntimeViewer viewer = RuntimeViewer.show("Test", referenceSet, series);
		Assert.assertNotNull(viewer);
		Assert.assertTrue(viewer.isVisible());
		
		Assert.assertEquals(indexType, viewer.getController().getIndexType());
		Assert.assertEquals(0, viewer.getController().getCurrentIndex());
		Assert.assertEquals(0, viewer.getController().getStartingIndex());
		Assert.assertEquals(indexType.equals(IndexType.NFE) ? 1000 : 9, viewer.getController().getEndingIndex());
		Assert.assertEquals(indexType.equals(IndexType.NFE) ? 100 : 1, viewer.getController().getStepSize());
		
		viewer.getController().setCurrentIndex(indexType.equals(IndexType.NFE) ? 1000 : 9);
		Assert.assertEquals(indexType.equals(IndexType.NFE) ? 1000 : 9, viewer.getController().getCurrentIndex());
		
		if (includeReferenceSet) {
			Assert.assertNotNull(viewer.getController().getReferenceSet());
			Assert.assertEquals(referenceSet, viewer.getController().getReferenceSet().getSeries().at(0).getPopulation());
		} else {
			Assert.assertNull(viewer.getController().getReferenceSet());
		}
		
		Assert.assertSize(1, viewer.getController().getSeries());
		Assert.assertSize(10, viewer.getController().getSeries().get(0).getSeries());
		viewer.dispose();
	}
	
	@Test
	public void testIndex() {
		test(IndexType.Index, true);
	}
	
	@Test
	public void testWithReferenceSet() {
		test(IndexType.NFE, true);
	}
	
	@Test
	public void testNoReferenceSet() {
		test(IndexType.NFE, false);
	}
	
	@Test
	public void testAnimation() {
		RuntimeViewer viewer = new RuntimeViewer("Test");
		Assert.assertFalse(viewer.getController().isRunning());
		
		viewer.getController().play();
		Assert.assertTrue(viewer.getController().isRunning());
		
		viewer.getController().play();
		Assert.assertTrue(viewer.getController().isRunning());
		
		viewer.getController().stop();
		Assert.assertFalse(viewer.getController().isRunning());
	}
	
	@Test
	public void testLocalization() {
		Assert.walkUI(new RuntimeViewer("Test"), Assert::assertLocalized);
	}

}

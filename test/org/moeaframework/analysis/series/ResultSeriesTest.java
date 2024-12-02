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
package org.moeaframework.analysis.series;

import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.population.Population;

public class ResultSeriesTest {
	
	private ResultSeries series;
	
	@Before
	public void setUp() {
		series = new ResultSeries(IndexType.NFE);
		
		IndexedResult result1 = new IndexedResult(IndexType.NFE, 100, new Population());
		result1.getProperties().setInt("test", 5);
		
		IndexedResult result2 = new IndexedResult(IndexType.NFE, 200, new Population());
		result2.getProperties().setInt("test", 2);
		
		series.add(result1);
		series.add(result2);
	}
	
	@After
	public void tearDown() {
		series = null;
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidIndexType() {
		series.add(new IndexedResult(IndexType.Index, 300, new Population()));
	}
	
	@Test
	public void test() {
		Assert.assertSize(2, series);	
		Assert.assertFalse(series.isEmpty());
		Assert.assertNotEquals(series.first(), series.last());
		
		for (IndexedResult result : series) {
			if (result.getIndex() == 100) {
				Assert.assertEquals(5, result.getProperties().getInt("test"));
			} else if (result.getIndex() == 200) {
				Assert.assertEquals(2, result.getProperties().getInt("test"));
			} else {
				Assert.fail();
			}
		}
	}
	
	@Test
	public void testEmpty() {
		series = new ResultSeries(IndexType.NFE);
		
		Assert.assertSize(0, series);
		Assert.assertTrue(series.isEmpty());
		Assert.assertFalse(series.iterator().hasNext());
		
		Assert.assertThrows(NoSuchElementException.class, () -> series.first());
		Assert.assertThrows(NoSuchElementException.class, () -> series.last());
		Assert.assertThrows(NoSuchElementException.class, () -> series.at(0));
	}
	
	@Test
	public void testAt() {
		Assert.assertEquals(100, series.at(50).getIndex());
		Assert.assertEquals(100, series.at(100).getIndex());
		Assert.assertEquals(200, series.at(150).getIndex());
		Assert.assertEquals(200, series.at(200).getIndex());
		Assert.assertThrows(NoSuchElementException.class, () -> series.at(250));
	}

}

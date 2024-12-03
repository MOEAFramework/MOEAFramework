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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.population.Population;

public class ResultSeriesTest {
	
	@Test(expected = IllegalArgumentException.class)
	public void testMissingNFE() {
		ResultSeries series = createSeries(IndexType.NFE, 2);
		series.add(new ResultEntry(new Population()));
	}
	
	@Test
	public void testPropertiesNFE() {
		ResultSeries series = createSeries(IndexType.NFE, 2);
		
		Assert.assertEquals(IndexType.NFE, series.getIndexType());
		Assert.assertEquals(2, series.size());	
		Assert.assertFalse(series.isEmpty());
		Assert.assertEquals(100, series.first().getIndex());
		Assert.assertEquals(200, series.last().getIndex());
		Assert.assertEquals(0, series.first().getProperties().getInt("test"));
		Assert.assertEquals(1, series.last().getProperties().getInt("test"));
		Assert.assertEquals(0, series.getStartingIndex());
		Assert.assertEquals(200, series.getEndingIndex());
	}
	
	@Test
	public void testPropertiesIndex() {
		ResultSeries series = createSeries(IndexType.Index, 2);
		
		Assert.assertEquals(IndexType.Index, series.getIndexType());
		Assert.assertEquals(2, series.size());	
		Assert.assertFalse(series.isEmpty());
		Assert.assertEquals(0, series.first().getIndex());
		Assert.assertEquals(1, series.last().getIndex());
		Assert.assertEquals(0, series.first().getProperties().getInt("test"));
		Assert.assertEquals(1, series.last().getProperties().getInt("test"));
		Assert.assertEquals(0, series.getStartingIndex());
		Assert.assertEquals(1, series.getEndingIndex());
	}
	
	@Test
	public void testPropertiesSingleton() {
		ResultSeries series = createSeries(IndexType.Singleton, 1);
		
		Assert.assertEquals(IndexType.Singleton, series.getIndexType());
		Assert.assertEquals(1, series.size());	
		Assert.assertFalse(series.isEmpty());
		Assert.assertEquals(0, series.first().getIndex());
		Assert.assertEquals(0, series.last().getIndex());
		Assert.assertEquals(0, series.first().getProperties().getInt("test"));
		Assert.assertEquals(0, series.last().getProperties().getInt("test"));
		Assert.assertEquals(0, series.getStartingIndex());
		Assert.assertEquals(0, series.getEndingIndex());
	}
	
	@Test
	public void testAddNFE() {
		ResultSeries series = createSeries(IndexType.NFE, 2);
		
		ResultEntry entry1 = new ResultEntry(new Population());
		entry1.getProperties().setInt(ResultEntry.NFE, 300);
		series.add(entry1);
		
		Assert.assertEquals(3, series.size());	
		Assert.assertSame(entry1, series.last().getEntry());
		
		ResultEntry entry2 = new ResultEntry(new Population());
		entry2.getProperties().setInt(ResultEntry.NFE, 150);
		series.add(entry2);
		
		Assert.assertEquals(4, series.size());	
		Assert.assertSame(entry2, series.first().next().getEntry());
		
		ResultEntry entry3 = new ResultEntry(new Population());
		entry2.getProperties().setInt(ResultEntry.NFE, 200);
		Assert.assertThrows(IllegalArgumentException.class, () -> series.add(entry3));
		
		Assert.assertEquals(4, series.size());
	}
	
	@Test
	public void testAddIndex() {
		ResultSeries series = createSeries(IndexType.Index, 2);
		
		ResultEntry entry = new ResultEntry(new Population());
		series.add(entry);
		
		Assert.assertEquals(3, series.size());	
		Assert.assertSame(entry, series.last().getEntry());
	}
	
	@Test
	public void testAddSingleton() {
		ResultSeries series = createSeries(IndexType.Singleton, 1);
		
		ResultEntry entry = new ResultEntry(new Population());
		Assert.assertThrows(IllegalArgumentException.class, () -> series.add(entry));
		
		Assert.assertEquals(1, series.size());	
	}
	
	@Test
	public void testIterator() {
		ResultSeries series = createSeries(IndexType.NFE, 2);
		Iterator<IndexedResult> it = series.iterator();
		IndexedResult result = null;
		
		Assert.assertTrue(it.hasNext());
		result = it.next();
		Assert.assertEquals(100, result.getIndex());
		Assert.assertEquals(0, result.getProperties().getInt("test"));
		
		Assert.assertTrue(it.hasNext());
		result = it.next();
		Assert.assertEquals(200, result.getIndex());
		Assert.assertEquals(1, result.getProperties().getInt("test"));
		
		Assert.assertFalse(it.hasNext());
		Assert.assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testEmpty() {
		ResultSeries series = createSeries(IndexType.NFE, 0);
		
		Assert.assertEquals(0, series.size());
		Assert.assertTrue(series.isEmpty());
		Assert.assertFalse(series.iterator().hasNext());
		
		Assert.assertThrows(NoSuchElementException.class, () -> series.first());
		Assert.assertThrows(NoSuchElementException.class, () -> series.last());
		Assert.assertThrows(NoSuchElementException.class, () -> series.at(0));
	}
	
	@Test
	public void testAtNFE() {
		ResultSeries series = createSeries(IndexType.NFE, 2);
		
		Assert.assertEquals(100, series.at(50).getIndex());
		Assert.assertEquals(100, series.at(100).getIndex());
		Assert.assertEquals(200, series.at(150).getIndex());
		Assert.assertEquals(200, series.at(200).getIndex());
		Assert.assertThrows(NoSuchElementException.class, () -> series.at(250));
	}
	
	@Test
	public void testAtIndex() {
		ResultSeries series = createSeries(IndexType.Index, 2);
		
		Assert.assertEquals(0, series.at(0).getIndex());
		Assert.assertEquals(1, series.at(1).getIndex());
		Assert.assertThrows(NoSuchElementException.class, () -> series.at(-1));
		Assert.assertThrows(NoSuchElementException.class, () -> series.at(2));
	}
	
	@Test
	public void testAtSingleton() {
		ResultSeries series = createSeries(IndexType.Singleton, 1);
		
		Assert.assertEquals(0, series.at(0).getIndex());
		Assert.assertEquals(1, series.at(1).getIndex());
		Assert.assertEquals(2, series.at(2).getIndex());
	}
	
	@Test
	public void testNextPrevious() {
		ResultSeries series = createSeries(IndexType.NFE, 2);
		
		IndexedResult entry = series.first();
		Assert.assertEquals(100, entry.getIndex());
		
		Assert.assertTrue(entry.hasNext());
		entry = entry.next();
		Assert.assertEquals(200, entry.getIndex());
		
		Assert.assertFalse(entry.hasNext());
		
		Assert.assertTrue(entry.hasPrevious());
		entry = entry.previous();
		Assert.assertEquals(100, entry.getIndex());
		
		Assert.assertFalse(entry.hasPrevious());
		
		Assert.assertThrows(NoSuchElementException.class, () -> series.last().next());
		Assert.assertThrows(NoSuchElementException.class, () -> series.first().previous());
	}
	
	@Test
	public void testDefinedProperties() {
		ResultSeries series = createSeries(IndexType.NFE, 2);
		
		ResultEntry entry = new ResultEntry(new Population());
		entry.getProperties().setInt(ResultEntry.NFE, 300);
		entry.getProperties().setInt("foo", 100);
		entry.getProperties().setInt("test", 100);
		
		series.add(entry);
		
		Assert.assertEquals(Set.of(ResultEntry.NFE, "test"), series.getDefinedProperties());
	}
	
	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		ResultSeries series = createSeries(IndexType.NFE, 2);
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				oos.writeObject(series);
			}
			
			try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
				try (ObjectInputStream ois = new ObjectInputStream(bais)) {
					ResultSeries cloneSeries = (ResultSeries)ois.readObject();
					
					Assert.assertEquals(2, cloneSeries.size());
					Assert.assertEquals(100, cloneSeries.first().getIndex());
					Assert.assertEquals(0, cloneSeries.first().getProperties().getInt("test"));
					Assert.assertEquals(200, cloneSeries.last().getIndex());
					Assert.assertEquals(1, cloneSeries.last().getProperties().getInt("test"));
				}
			}
		}
	}
	
	private ResultSeries createSeries(IndexType indexType, int size) {
		ResultSeries series = new ResultSeries(indexType);
		
		for (int i = 0; i < size; i++) {
			ResultEntry entry = new ResultEntry(new Population());
			entry.getProperties().setInt("test", i);
			
			if (indexType.equals(IndexType.NFE)) {
				entry.getProperties().setInt(ResultEntry.NFE, 100 * (i + 1));
			}
			
			series.add(entry);
		}
		
		return series;
	}

}

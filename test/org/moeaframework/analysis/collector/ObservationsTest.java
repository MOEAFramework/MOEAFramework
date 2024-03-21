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

import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ObservationsTest {
	
	private Observations observations;
	
	@Before
	public void setUp() {
		observations = new Observations();
		
		Observation observation1 = new Observation(100);
		observation1.set("test", 5);
		
		Observation observation2 = new Observation(200);
		observation2.set("test", 2);
		
		observations.add(observation1);
		observations.add(observation2);
	}
	
	@After
	public void tearDown() {
		observations = null;
	}
	
	@Test
	public void test() {
		Assert.assertEquals(1, observations.keys().size());
		Assert.assertTrue(observations.keys().contains("test"));
		
		Assert.assertNotEquals(observations.first(), observations.last());
		
		Assert.assertEquals(2, observations.size());
		Assert.assertFalse(observations.isEmpty());
		
		for (Observation observation : observations) {
			if (observation.getNFE() == 100) {
				Assert.assertEquals(5, observation.get("test"));
			} else if (observation.getNFE() == 200) {
				Assert.assertEquals(2, observation.get("test"));
			} else {
				Assert.fail();
			}
		}
	}
	
	@Test
	public void testEmptyObservations() {
		Observations observations = new Observations();
		
		Assert.assertEquals(0, observations.keys().size());
		Assert.assertEquals(0, observations.size());
		Assert.assertTrue(observations.isEmpty());
		
		Assert.assertFalse(observations.iterator().hasNext());
		
		Assert.assertThrows(NoSuchElementException.class, () -> observations.first());
		Assert.assertThrows(NoSuchElementException.class, () -> observations.last());
		
		Assert.assertNull(observations.at(0));
	}
	
	@Test
	public void testAt() {
		Assert.assertEquals(5, observations.at(50).get("test"));
		Assert.assertEquals(5, observations.at(100).get("test"));
		Assert.assertEquals(2, observations.at(150).get("test"));
		Assert.assertEquals(2, observations.at(200).get("test"));
		Assert.assertNull(observations.at(250));
	}
	
	@Test
	public void testCompareTo() {
		Observation observation1 = new Observation(100);
		Observation observation2 = new Observation(200);
		Observation observation3 = new Observation(100);
		
		Assert.assertEquals(-1, observation1.compareTo(observation2));
		Assert.assertEquals(1, observation2.compareTo(observation3));
		Assert.assertEquals(0, observation1.compareTo(observation3));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetIllegalKey() {
		observations.first().get("foo");
	}

}

/* Copyright 2009-2022 David Hadka
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
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetIllegalKey() {
		observations.first().get("foo");
	}

}

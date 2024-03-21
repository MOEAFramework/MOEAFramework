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
package org.moeaframework.core;

import org.junit.Assert;
import org.junit.Test;

public class EpsilonsTest {
	
	@Test
	public void testEqual() {
		Assert.assertEquals(new Epsilons(0.1), new Epsilons(0.1));
		Assert.assertEquals(new Epsilons(new double[] { 0.1, 0.2 }), new Epsilons(new double[] { 0.1, 0.2 }));
		
		Assert.assertNotEquals(new Epsilons(0.1), new Epsilons(0.2));
		Assert.assertNotEquals(new Epsilons(new double[] { 0.1, 0.2 }), new Epsilons(new double[] { 0.1, 0.1 }));
		Assert.assertNotEquals(new Epsilons(new double[] { 0.1, 0.2 }), new Epsilons(new double[] { 0.1, 0.2, 0.2 }));
		
		Epsilons epsilons = new Epsilons(0.1);
		Assert.assertEquals(epsilons, epsilons);
	}
	
	@Test
	public void testHashCode() {
		Assert.assertEquals(new Epsilons(0.1).hashCode(), new Epsilons(0.1).hashCode());
		Assert.assertEquals(new Epsilons(new double[] { 0.1, 0.2 }).hashCode(), new Epsilons(new double[] { 0.1, 0.2 }).hashCode());
		
		Assert.assertNotEquals(new Epsilons(0.1).hashCode(), new Epsilons(0.2).hashCode());
		Assert.assertNotEquals(new Epsilons(new double[] { 0.1, 0.2 }.hashCode()), new Epsilons(new double[] { 0.1, 0.1 }).hashCode());
		Assert.assertNotEquals(new Epsilons(new double[] { 0.1, 0.2 }.hashCode()), new Epsilons(new double[] { 0.1, 0.2, 0.2 }).hashCode());
	}
	
	@Test
	public void testSize() {
		Assert.assertEquals(1, new Epsilons(0.1).size());
		Assert.assertEquals(2, new Epsilons(new double[] { 0.1, 0.2 }).size());
	}
	
	@Test
	public void testEpsilonExtension() {
		Epsilons epsilons = new Epsilons(new double[] { 0.1, 0.2 });

		Assert.assertEquals(0.1, epsilons.get(0), Settings.EPS);
		Assert.assertEquals(0.2, epsilons.get(1), Settings.EPS);
		Assert.assertEquals(0.2, epsilons.get(2), Settings.EPS);
		Assert.assertEquals(0.2, epsilons.get(Integer.MAX_VALUE), Settings.EPS);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testEmptyArray() {
		new Epsilons(new double[0]);
	}
	
	@Test
	public void testInvalidValue() {
		Assert.assertThrows(IllegalArgumentException.class, () -> new Epsilons(0.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Epsilons.of(0.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Epsilons.of(-1.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Epsilons.of(1.0, 2.0, 0.0, 4.0));
	}
	
	@Test
	public void testOf() {
		Assert.assertEquals(new Epsilons(0.1), Epsilons.of(0.1));
		Assert.assertEquals(new Epsilons(new double[] { 0.1, 0.2 }), Epsilons.of(0.1, 0.2));
	}
	
	@Test
	public void testMutability() {
		double[] original = new double[] { 0.1, 0.2 };
		
		Epsilons epsilons = new Epsilons(original);
		double[] array = epsilons.toArray();
		
		Assert.assertEquals(2, array.length);
		Assert.assertArrayEquals(original, array, Settings.EPS);
		
		array[0] = 0.5;
		Assert.assertEquals(0.1, epsilons.get(0), Settings.EPS);
		
		original[0] = 0.5;
		Assert.assertEquals(0.1, epsilons.get(0), Settings.EPS);
	}

}

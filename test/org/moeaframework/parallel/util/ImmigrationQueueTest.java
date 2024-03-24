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
package org.moeaframework.parallel.util;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockSolution;

public class ImmigrationQueueTest {
	
	@Test
	public void test() throws InterruptedException {
		ImmigrationQueue queue = new ImmigrationQueue();
		Solution original = MockSolution.of().withObjectives(0.0, 1.0);
		
		Assert.assertEquals(0, queue.size());
		Assert.assertTrue(queue.isEmpty());
		Assert.assertThrows(NoSuchElementException.class, () -> queue.pop());
		Assert.assertTrue(queue.popAll().isEmpty());
		
		queue.add(original);
		
		Assert.assertEquals(1, queue.size());
		Assert.assertFalse(queue.isEmpty());
		
		Solution copy = queue.pop();
		Assert.assertNotNull(copy);
		Assert.assertTrue(TestUtils.equals(original, copy));
		Assert.assertNotSame(original, copy);
		Assert.assertEquals(0, queue.size());
		Assert.assertTrue(queue.isEmpty());
		
		queue.addAll(List.of(original, original, original));
		
		copy = queue.pop();
		Assert.assertNotNull(copy);

		List<Solution> copies = queue.popAll();
		Assert.assertNotNull(copies);
		Assert.assertEquals(2, copies.size());
		Assert.assertNotSame(original, copies.get(0));
		Assert.assertNotSame(original, copies.get(1));
		Assert.assertEquals(0, queue.size());
		Assert.assertTrue(queue.isEmpty());
	}
	
}

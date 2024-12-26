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
package org.moeaframework.core.comparator;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Solution;

public class ChainedComparatorTest {

	/**
	 * A comparator used for testing that always returns a fixed value.
	 */
	private static class FixedComparator implements DominanceComparator {

		private final int value;

		public FixedComparator(int value) {
			this.value = value;
		}

		@Override
		public int compare(Solution solution1, Solution solution2) {
			return value;
		}

	}

	@Test
	public void testFirstComparator() {
		ChainedComparator cc = new ChainedComparator(new FixedComparator(1), new FixedComparator(-1));

		Assert.assertEquals(1, cc.compare(null, null));
	}

	@Test
	public void testSecondComparator() {
		ChainedComparator cc = new ChainedComparator(new FixedComparator(0), new FixedComparator(-1));

		Assert.assertEquals(-1, cc.compare(null, null));
	}

	@Test
	public void testNoComparators() {
		ChainedComparator cc = new ChainedComparator(new FixedComparator(0), new FixedComparator(0));

		Assert.assertEquals(0, cc.compare(null, null));
	}

}

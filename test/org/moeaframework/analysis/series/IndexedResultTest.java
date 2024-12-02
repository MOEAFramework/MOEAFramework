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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.population.Population;

public class IndexedResultTest {
	
	@Test
	public void testCompareTo() {
		IndexedResult result1 = new IndexedResult(IndexType.NFE, 100, new Population());
		IndexedResult result2 = new IndexedResult(IndexType.NFE, 200, new Population());
		IndexedResult result3 = new IndexedResult(IndexType.NFE, 100, new Population());
		
		Assert.assertEquals(-1, result1.compareTo(result2));
		Assert.assertEquals(1, result2.compareTo(result3));
		Assert.assertEquals(0, result1.compareTo(result3));
	}

}

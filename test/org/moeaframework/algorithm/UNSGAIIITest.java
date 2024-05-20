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
package org.moeaframework.algorithm;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Solution;
import org.moeaframework.core.attribute.Niche;
import org.moeaframework.core.attribute.NicheDistance;
import org.moeaframework.core.attribute.Rank;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.mock.MockSolution;

public class UNSGAIIITest {
	
	@Test
	public void testComparator() {
		DominanceComparator comparator = new UNSGAIII.UnifiedDominanceComparator();
		
		Solution s1 = MockSolution.of().withConstraints(-1.0);
		Solution s2 = MockSolution.of()
				.withAttribute(Niche.ATTRIBUTE_NAME, 1)
				.withAttribute(Rank.ATTRIBUTE_NAME, 1);
		Solution s3 = MockSolution.of()
				.withAttribute(Niche.ATTRIBUTE_NAME, 1)
				.withAttribute(Rank.ATTRIBUTE_NAME, 2);
		Solution s4 = MockSolution.of()
				.withAttribute(Niche.ATTRIBUTE_NAME, 1)
				.withAttribute(Rank.ATTRIBUTE_NAME, 1)
				.withAttribute(NicheDistance.ATTRIBUTE_NAME, 0.1);
		Solution s5 = MockSolution.of()
				.withAttribute(Niche.ATTRIBUTE_NAME, 1)
				.withAttribute(Rank.ATTRIBUTE_NAME, 1)
				.withAttribute(NicheDistance.ATTRIBUTE_NAME, 0.5);
		
		Assert.assertEquals(1, comparator.compare(s1, s2));
		Assert.assertEquals(-1, comparator.compare(s3, s1));
		Assert.assertEquals(-1, comparator.compare(s2, s3));
		Assert.assertEquals(-1, comparator.compare(s4, s5));
		Assert.assertEquals(1, comparator.compare(s3, s5));
	}
	
}

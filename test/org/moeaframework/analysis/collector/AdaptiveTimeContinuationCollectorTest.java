/* Copyright 2009-2019 David Hadka
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

import org.junit.Test;

/**
 * Tests the {@link AdaptiveTimeContinuationCollector} class.
 */
public class AdaptiveTimeContinuationCollectorTest extends CollectorTest {
	
	@Test
	public void testNSGAII() {
		test("NSGAII", new AdaptiveTimeContinuationCollector(), false);
	}
	
	@Test
	public void testeNSGAII() {
		test("eNSGAII", new AdaptiveTimeContinuationCollector(), true);
	}
	
	@Test
	public void testeMOEA() {
		test("eMOEA", new AdaptiveTimeContinuationCollector(), false);
	}
	
	@Test
	public void testGDE3() {
		test("GDE3", new AdaptiveTimeContinuationCollector(), false);
	}
	
	@Test
	public void testMOEAD() {
		test("MOEAD", new AdaptiveTimeContinuationCollector(), false);
	}
	
	@Test
	public void testRandom() {
		test("Random", new AdaptiveTimeContinuationCollector(), false);
	}

}

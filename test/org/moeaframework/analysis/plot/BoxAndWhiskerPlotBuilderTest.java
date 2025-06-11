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
package org.moeaframework.analysis.plot;

import java.util.stream.IntStream;

import org.junit.Test;
import org.moeaframework.core.PRNG;

public class BoxAndWhiskerPlotBuilderTest extends AbstractPlotTest {
	
	@Test
	public void testEmpty() {
		new BoxAndWhiskerPlotBuilder().show();
	}

	@Test
	public void test() {
		new BoxAndWhiskerPlotBuilder()
				.add("Set 1", IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray())
				.add("Set 2", IntStream.range(0, 50).mapToDouble(i -> 2 * PRNG.nextDouble()).toArray())
				.add("Set 3", IntStream.range(0, 100).mapToDouble(i -> PRNG.nextDouble()).toArray())
				.show();
	}
	
	@Test
	public void testStyle() {
		new BoxAndWhiskerPlotBuilder()
				.add("Set 1", IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray(), SeriesPaint.green())
				.show();
	}
	
	public static void main(String[] args) throws Exception {
		new BoxAndWhiskerPlotBuilderTest().runAll();
	}

}

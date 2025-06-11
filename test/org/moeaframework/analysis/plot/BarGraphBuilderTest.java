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

public class BarGraphBuilderTest extends AbstractPlotTest {
	
	@Test
	public void testEmpty() {
		new BarGraphBuilder().show();
	}

	@Test
	public void testSingleSeries() {
		double[] x = IntStream.range(0, 10).mapToDouble(i -> (double)i).toArray();
		double[] y = IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray();
		
		new BarGraphBuilder()
				.bars("Test", x, y)
				.show();
	}
	
	@Test
	public void testTwoSeries() {
		double[] x = IntStream.range(0, 10).mapToDouble(i -> (double)i).toArray();
		double[] y1 = IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray();
		double[] y2 = IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray();
		
		new BarGraphBuilder()
				.bars("Set 1", x, y1)
				.bars("Set 2", x, y2)
				.show();
	}
	
	@Test
	public void testStyle() {
		double[] x = IntStream.range(0, 10).mapToDouble(i -> (double)i).toArray();
		double[] y = IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray();
		
		new BarGraphBuilder()
				.bars("Test", x, y, SeriesPaint.black())
				.show();
	}
	
	public static void main(String[] args) throws Exception {
		new BarGraphBuilderTest().runAll();
	}

}

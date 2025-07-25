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

public class BarGraphBuilderTest extends AbstractPlotBuilderTest {
	
	@Test
	public void barGraph() {
		double[] x = IntStream.range(0, 10).mapToDouble(i -> i).toArray();
		double[] y1 = IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray();
		double[] y2 = IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray();
		
		new BarGraphBuilder()
				.bars("Set1", x, y1)
				.bars("Set2", x, y2)
				.show();
	}

}

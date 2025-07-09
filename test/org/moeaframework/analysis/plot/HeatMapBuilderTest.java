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

public class HeatMapBuilderTest extends AbstractPlotBuilderTest {
	
	@Test
	public void heatMap() {
		double[] x = IntStream.range(0, 100).mapToDouble(i -> i / 100.0).toArray();
		double[] y = IntStream.range(0, 200).mapToDouble(i -> i / 100.0).toArray();
		double[][] z = new double[x.length][y.length];
		
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < y.length; j++) {
				z[i][j] = i * Math.sqrt(j);
			}
		}
		
		new HeatMapBuilder()
				.xCoords(x)
				.yCoords(y)
				.zData(z)
				.style(Style.showToolTips(), Style.red())
				.xLabel("X")
				.yLabel("Y")
				.zLabel("Value")
				.show();
	}
	
	public static void main(String[] args) {
		new HeatMapBuilderTest().heatMap();
	}

}

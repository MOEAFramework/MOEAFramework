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

import java.awt.Color;
import java.io.PrintStream;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.chart.StandardChartTheme;
import org.junit.Test;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sensitivity.FirstOrderSensitivity;
import org.moeaframework.analysis.sensitivity.SecondOrderSensitivity;
import org.moeaframework.analysis.sensitivity.Sensitivity;
import org.moeaframework.analysis.sensitivity.SensitivityResult;
import org.moeaframework.analysis.sensitivity.TotalOrderSensitivity;

public class SensitivityPlotBuilderTest extends AbstractPlotBuilderTest {
	
	public static class TestSensitivityResult implements SensitivityResult, FirstOrderSensitivity,
	TotalOrderSensitivity, SecondOrderSensitivity {
		
		private static final int N = 5;
		
		private double getValue(Parameter<?> key) {
			int index = Integer.parseInt(key.getName().substring(9));
			return (double)index / N;
		}

		@Override
		public ParameterSet getParameterSet() {
			return new ParameterSet(IntStream.range(0, N)
					.mapToObj(i -> Parameter.named("Parameter" + (i + 1)).asDouble().sampledBetween(0.0, 1.0))
					.toArray(Parameter[]::new));
		}

		@Override
		public Sensitivity<Parameter<?>> getFirstOrder(Parameter<?> key) {
			return new Sensitivity<>(key, 0.75 * getValue(key), 0.0);
		}

		@Override
		public Sensitivity<Pair<Parameter<?>, Parameter<?>>> getSecondOrder(Parameter<?> left, Parameter<?> right) {
			return new Sensitivity<>(Pair.of(left, right), 0.75 * getValue(left), 0.0);
		}

		@Override
		public Sensitivity<Parameter<?>> getTotalOrder(Parameter<?> key) {
			return new Sensitivity<>(key, getValue(key), 0.0);
		}
		
		@Override
		public void display(PrintStream out) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	private SensitivityResult getSensitivityResult() {
		return new TestSensitivityResult();
	}
	
	@Test
	public void sensitivityPlot() {
		StandardChartTheme theme = new StandardChartTheme("Sensitivity");
		theme.setPlotBackgroundPaint(Color.WHITE);
		
		SensitivityResult sensitivityResult = getSensitivityResult();
		
		new SensitivityPlotBuilder()
				.data(sensitivityResult)
				.theme(theme)
				.show();
	}
	
	public static void main(String[] args) {
		new SensitivityPlotBuilderTest().sensitivityPlot();
	}

}

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
package org.moeaframework.analysis.collector;

import org.moeaframework.Assert;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Indicator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Settings;

public class IndicatorCollectorTest extends AbstractCollectorTest<IndicatorCollector> {
	
	@Override
	public void validate(Observation observation) {
		Assert.assertEquals(1.0, IndicatorCollector.getIndicatorValue(observation, MockIndicator.class), Settings.EPS);
	}
	
	@Override
	public IndicatorCollector createInstance() {
		return new IndicatorCollector(new MockIndicator());
	}
	
	@Override
	public boolean shouldAttach(Algorithm algorithm) {
		return true;
	}
	
	private static class MockIndicator implements Indicator {

		@Override
		public double evaluate(NondominatedPopulation approximationSet) {
			return 1.0;
		}
		
	}

}

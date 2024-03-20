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

import org.junit.Assert;
import org.moeaframework.algorithm.AdaptiveTimeContinuation;
import org.moeaframework.core.Algorithm;

public class AdaptiveTimeContinuationCollectorTest extends AbstractCollectorTest {
	
	@Override
	public void validate(Observation observation) {
		Assert.assertTrue(AdaptiveTimeContinuationCollector.getNumberOfRestarts(observation) >= 0);
	}
	
	@Override
	public Collector createInstance() {
		return new AdaptiveTimeContinuationCollector();
	}
	
	@Override
	public boolean shouldAttach(Algorithm algorithm) {
		return algorithm instanceof AdaptiveTimeContinuation;
	}

}

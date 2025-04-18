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
package org.moeaframework.analysis.runtime;

import org.moeaframework.Assert;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.algorithm.EpsilonNSGAII;
import org.moeaframework.algorithm.EvolutionaryAlgorithm;
import org.moeaframework.analysis.series.ResultEntry;

public class EpsilonProgressCollectorTest extends AbstractCollectorTest<EpsilonProgressCollector> {
	
	@Override
	public void validate(Algorithm algorithm, ResultEntry result) {
		if (algorithm instanceof EvolutionaryAlgorithm ea && ea.getArchive() != null) {
			Assert.assertTrue(EpsilonProgressCollector.getNumberOfImprovements(result) >= 0);
			Assert.assertTrue(EpsilonProgressCollector.getNumberOfDominatingImprovements(result) >= 0);
		}
	}
	
	@Override
	public EpsilonProgressCollector createInstance() {
		return new EpsilonProgressCollector();
	}
	
	@Override
	public boolean shouldAttach(Algorithm algorithm) {
		return algorithm instanceof EpsilonBoxEvolutionaryAlgorithm || algorithm instanceof EpsilonNSGAII;
	}

}

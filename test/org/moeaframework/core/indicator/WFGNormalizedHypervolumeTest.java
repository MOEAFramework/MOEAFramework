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
package org.moeaframework.core.indicator;

import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;

public class WFGNormalizedHypervolumeTest extends AbstractHypervolumeTest<WFGNormalizedHypervolume> {
	
	@Override
	public WFGNormalizedHypervolume createInstance(Problem problem, NondominatedPopulation referenceSet) {
		return new WFGNormalizedHypervolume(problem, referenceSet);
	}
	
	@Override
	public WFGNormalizedHypervolume createInstance(Problem problem, Normalizer normalizer) {
		return new WFGNormalizedHypervolume(problem, normalizer);
	}
	
	@Override
	public boolean includesBetterSolutions() {
		return true;
	}

}

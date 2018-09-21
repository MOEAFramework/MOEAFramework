/* Copyright 2009-2018 David Hadka
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

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;

/**
 * Collects the number of &epsilon;-progress improvements detected in an
 * {@link EpsilonBoxEvolutionaryAlgorithm}.
 */
public class EpsilonProgressCollector implements Collector {

	/**
	 * The algorithm instance used by this collector; or {@code null} if this 
	 * collector has not yet been attached.
	 */
	private final EpsilonBoxEvolutionaryAlgorithm algorithm;

	/**
	 * Constructs an unattached collector for recording the number of 
	 * &epsilon;-progress improvements detected in an 
	 * {@code EpsilonBoxEvolutionaryAlgorithm}.
	 */
	public EpsilonProgressCollector() {
		this(null);
	}
	
	/**
	 * Constructs a collector for recording the number of &epsilon;-progress
	 * improvements detected in the specified 
	 * {@code EpsilonBoxEvolutionaryAlgorithm}.
	 * 
	 * @param algorithm the algorithm this collector records data from
	 */
	public EpsilonProgressCollector(
			EpsilonBoxEvolutionaryAlgorithm algorithm) {
		super();
		this.algorithm = algorithm;
	}

	@Override
	public void collect(Accumulator accumulator) {
		EpsilonBoxDominanceArchive archive = algorithm.getArchive();

		if (archive != null) {
			accumulator.add("Number of Improvements", archive
					.getNumberOfImprovements());
			accumulator.add("Number of Dominating Improvements", archive
					.getNumberOfDominatingImprovements());
		}
	}

	@Override
	public AttachPoint getAttachPoint() {
		return AttachPoint.isSubclass(EpsilonBoxEvolutionaryAlgorithm.class);
	}

	@Override
	public Collector attach(Object object) {
		return new EpsilonProgressCollector(
				(EpsilonBoxEvolutionaryAlgorithm)object);
	}

}

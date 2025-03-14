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

import org.moeaframework.algorithm.EvolutionaryAlgorithm;
import org.moeaframework.analysis.series.ResultEntry;

/**
 * Collects the population size and archive size, if available, from an {@link EvolutionaryAlgorithm}.
 */
public class PopulationSizeCollector implements Collector {
	
	/**
	 * The algorithm instance used by this collector; or {@code null} if this collector has not yet been attached.
	 */
	private final EvolutionaryAlgorithm algorithm;
	
	/**
	 * Constructs an unattached collector for recording the population size and archive size, if available, from an
	 * {@code EvolutionaryAlgorithm}.
	 */
	public PopulationSizeCollector() {
		this(null);
	}
	
	/**
	 * Constructs a collector for recording the population size and archive size, if available, from the specified
	 * {@code EvolutionaryAlgorithm}.
	 * 
	 * @param algorithm the algorithm this collector records data from
	 */
	public PopulationSizeCollector(EvolutionaryAlgorithm algorithm) {
		super();
		this.algorithm = algorithm;
	}

	@Override
	public AttachPoint getAttachPoint() {
		return AttachPoint.isSubclass(EvolutionaryAlgorithm.class).and(
				AttachPoint.not(AttachPoint.isNestedIn(EvolutionaryAlgorithm.class)));
	}

	@Override
	public Collector attach(Object object) {
		return new PopulationSizeCollector((EvolutionaryAlgorithm)object);
	}

	@Override
	public void collect(ResultEntry result) {
		result.getProperties().setInt("Population Size", algorithm.getPopulation().size());
		
		if (algorithm.getArchive() != null) {
			result.getProperties().setInt("Archive Size", algorithm.getArchive().size());
		}
	}
	
	/**
	 * Reads the population size from the result.
	 * 
	 * @param result the result
	 * @return the population size
	 */
	public static int getPopulationSize(ResultEntry result) {
		return result.getProperties().getInt("Population Size");
	}
	
	/**
	 * Reads the archive size from the result.
	 * 
	 * @param result the result
	 * @return the archive size
	 */
	public static int getArchiveSize(ResultEntry result) {
		return result.getProperties().getInt("Archive Size");
	}

}

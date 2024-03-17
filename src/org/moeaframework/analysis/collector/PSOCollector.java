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

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.algorithm.pso.AbstractPSOAlgorithm;
import org.moeaframework.core.Solution;

/**
 * Collects the population from an {@link AbstractPSOAlgorithm}.
 */
public class PSOCollector implements Collector {
	
	/**
	 * The algorithm instance used by this collector; or {@code null} if this collector has not yet been attached.
	 */
	private final AbstractPSOAlgorithm algorithm;
	
	/**
	 * Constructs an unattached collector for recording the population from an {@code AbstractPSOAlgorithm}.
	 */
	public PSOCollector() {
		this(null);
	}
	
	/**
	 * Constructs a collector for recording the population from the specified {@code AbstractPSOAlgorithm}.
	 * 
	 * @param algorithm the algorithm this collector records data from
	 */
	public PSOCollector(AbstractPSOAlgorithm algorithm) {
		super();
		this.algorithm = algorithm;
	}

	@Override
	public AttachPoint getAttachPoint() {
		return AttachPoint.isSubclass(AbstractPSOAlgorithm.class).and(
				AttachPoint.not(AttachPoint.isNestedIn(AbstractPSOAlgorithm.class)));
	}

	@Override
	public Collector attach(Object object) {
		return new PSOCollector((AbstractPSOAlgorithm)object);
	}

	@Override
	public void collect(Observation observation) {
		observation.set("Particles", new ArrayList<Solution>(algorithm.getParticles()));
		observation.set("LocalBestParticles", new ArrayList<Solution>(algorithm.getLocalBestParticles()));
		observation.set("Leaders", new ArrayList<Solution>(algorithm.getLeaders()));
	}
	
	/**
	 * Reads the list of particles from the observation.
	 * 
	 * @param observation the observation
	 * @return the list of particles
	 */
	@SuppressWarnings("unchecked")
	public static List<Solution> getParticles(Observation observation) {
		return (List<Solution>)observation.get("Population");
	}
	
	/**
	 * Reads the list of local-best particles from the observation.
	 * 
	 * @param observation the observation
	 * @return the list of local-best particles
	 */
	@SuppressWarnings("unchecked")
	public static List<Solution> getLocalBestParticles(Observation observation) {
		return (List<Solution>)observation.get("LocalBestParticles");
	}
	
	/**
	 * Reads the list of leaders from the observation.
	 * 
	 * @param observation the observation
	 * @return the list of leaders
	 */
	@SuppressWarnings("unchecked")
	public static List<Solution> getLeaders(Observation observation) {
		return (List<Solution>)observation.get("Leaders");
	}

}

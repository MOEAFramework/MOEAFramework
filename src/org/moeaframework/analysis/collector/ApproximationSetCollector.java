/* Copyright 2009-2011 David Hadka
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

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Solution;

/**
 * Collects approximation sets from an {@link Algorithm}.
 */
public class ApproximationSetCollector implements Collector {
	
	/**
	 * The algorithm instance used by this collector; or {@code null} if this 
	 * collector has not yet been attached.
	 */
	private final Algorithm algorithm;
	
	/**
	 * Constructs an unattached collector for recording approximation sets from
	 * an algorithm.
	 */
	public ApproximationSetCollector() {
		this(null);
	}
	
	/**
	 * Constructs a collector for recording approximation sets from the
	 * specified algorithm.
	 * 
	 * @param algorithm the algorithm this collector records data from
	 */
	public ApproximationSetCollector(Algorithm algorithm) {
		super();
		this.algorithm = algorithm;
	}

	@Override
	public AttachPoint getAttachPoint() {
		return AttachPoint.isSubclass(Algorithm.class).and(
				AttachPoint.not(AttachPoint.isNestedIn(Algorithm.class)));
	}

	@Override
	public Collector attach(Object object) {
		return new ApproximationSetCollector((Algorithm)object);
	}

	@Override
	public void collect(Accumulator accumulator) {
		ArrayList<Solution> list = new ArrayList<Solution>();
		
		for (Solution solution : algorithm.getResult()) {
			list.add(solution);
		}
		
		accumulator.add("Approximation Set", list);
	}

}

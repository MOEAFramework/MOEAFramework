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

/**
 * Collects information from an object.  In general, the object will be an
 * algorithm or an object stored within an algorithm.  Collectors have two
 * states: attached and unattached.  Collectors are initially unattached,
 * and are attached to an appropriate object, called the attach point, by
 * invoking {@link #attach(Object)}.  Once attached, the 
 * {@link #collect(Accumulator)} may be invoked.
 */
public interface Collector {
	
	/**
	 * Returns the attach point describing where this collector is attached.
	 * The matched object should be unique.
	 * 
	 * @return the attach point describing where this collector is attached
	 */
	public AttachPoint getAttachPoint();
	
	/**
	 * Returns a new instance of this collector which has been attached to the
	 * specified object as identified by the attach point returned through
	 * {@link #getAttachPoint()}.
	 * 
	 * @param object the matching object
	 * @return a new instance of this collector which has been attached to the
	 *         specified object
	 */
	public Collector attach(Object object);

	/**
	 * Collects the necessary information from the object, storing the data
	 * to the specified accumulator.  This method must only be invoked after
	 * this collector has been attached to an appropriate object.
	 * 
	 * @param accumulator the accumulator to which the collected data is stored
	 */
	public void collect(Accumulator accumulator);

}

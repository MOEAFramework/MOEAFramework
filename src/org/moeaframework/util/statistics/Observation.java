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
package org.moeaframework.util.statistics;

/**
 * Stores an observed value and the group from which the observation belongs.
 */
public class Observation {

	/**
	 * The value of this observation.
	 */
	private final double value;

	/**
	 * The group from which this observation belongs.
	 */
	private final int group;

	/**
	 * Constructs an observation with the specified value and group.
	 * 
	 * @param value the value of this observation
	 * @param group the group from which this observation belongs
	 */
	public Observation(double value, int group) {
		super();
		this.value = value;
		this.group = group;
	}

	/**
	 * Returns the value of this observation.
	 * 
	 * @return the value of this observation
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Returns the group from which this observation belongs.
	 * 
	 * @return the group from which this observation belongs
	 */
	public int getGroup() {
		return group;
	}

}

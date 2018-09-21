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
 * An observation with a rank. Rank-based statistics use this type of
 * observation to manage ranks.
 */
public class RankedObservation extends Observation {

	/**
	 * The rank of this observation.
	 */
	private double rank;

	/**
	 * Constructs a ranked observation with the specified value and group. The
	 * rank of this observation is default to 0.0.
	 * 
	 * @param value the value of this observation
	 * @param group the group from which this observation belongs
	 */
	public RankedObservation(double value, int group) {
		super(value, group);
	}

	/**
	 * Returns the rank of this observation.
	 * 
	 * @return the rank of this observation
	 */
	public double getRank() {
		return rank;
	}

	/**
	 * Sets the rank of this observation.
	 * 
	 * @param rank the new rank for this observation
	 */
	public void setRank(double rank) {
		this.rank = rank;
	}

}

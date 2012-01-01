/* Copyright 2009-2012 David Hadka
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
package org.moeaframework.analysis.diagnostics;

import java.io.Serializable;

/**
 * The key for a result entry, allowing distinct algorithm and problem pairings
 * in sets and maps.
 */
public class ResultKey implements Comparable<ResultKey>, Serializable {

	private static final long serialVersionUID = 8819746159439155038L;

	/**
	 * The algorithm represented by this result key.
	 */
	private final String algorithm;
	
	/**
	 * The problem represented by this result key.
	 */
	private final String problem;
	
	/**
	 * Constructs a new result key with the specified algorithm and problem.
	 * 
	 * @param algorithm the algorithm represented by this result key
	 * @param problem the problem represented by this result key
	 */
	public ResultKey(String algorithm, String problem) {
		super();
		this.algorithm = algorithm;
		this.problem = problem;
	}

	/**
	 * Returns the algorithm represented by this result key.
	 * 
	 * @return the algorithm represented by this result key
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * Returns the problem represented by this result key.
	 * 
	 * @return the problem represented by this result key.
	 */
	public String getProblem() {
		return problem;
	}

	@Override
	public int compareTo(ResultKey key) {
		int result = algorithm.compareTo(key.algorithm);
		
		if (result == 0) {
			result = problem.compareTo(key.problem);
		}
		
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((problem == null) ? 0 : problem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultKey other = (ResultKey)obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (problem == null) {
			if (other.problem != null)
				return false;
		} else if (!problem.equals(other.problem))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return algorithm + " " + problem;
	}

}

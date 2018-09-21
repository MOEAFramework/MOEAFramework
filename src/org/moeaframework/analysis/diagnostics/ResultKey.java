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
package org.moeaframework.analysis.diagnostics;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
	public int compareTo(ResultKey rhs) {
		return new CompareToBuilder()
				.append(algorithm, rhs.algorithm)
				.append(problem, rhs.problem)
				.toComparison();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(algorithm)
				.append(problem)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			ResultKey rhs = (ResultKey)obj;
			
			return new EqualsBuilder()
					.append(algorithm, rhs.algorithm)
					.append(problem, rhs.problem)
					.isEquals();
		}
	}
	
	@Override
	public String toString() {
		return algorithm + " " + problem;
	}

}

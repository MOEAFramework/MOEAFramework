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
package org.moeaframework.core.comparator;

import java.io.Serializable;

/**
 * Compares solutions first using the {@link AggregateConstraintComparator}
 * followed by the {@link EpsilonBoxDominanceComparator}.
 * 
 * @deprecated Will be removed in version 2.0; use EpsilonBoxDominanceComparator
 *             instead
 */
@Deprecated
public class EpsilonBoxConstraintComparator extends
EpsilonBoxDominanceComparator implements Serializable {

	private static final long serialVersionUID = -7470779078938449587L;

	/**
	 * Constructs a dominance comparator for comparing solutions using the
	 * {@link EpsilonBoxDominanceComparator}.
	 * 
	 * @param epsilon the epsilon value used by this comparator
	 * @deprecated Will be removed in version 2.0; use
	 *             EpsilonBoxDominanceComparator instead
	 */
	@Deprecated
	public EpsilonBoxConstraintComparator(double epsilon) {
		super(epsilon);
	}

	/**
	 * Constructs a dominance comparator for comparing solutions using the
	 * {@link EpsilonBoxDominanceComparator}.
	 * 
	 * @param epsilons the epsilon values used by this comparator
	 * @deprecated Will be removed in version 2.0; use
	 *             EpsilonBoxDominanceComparator instead
	 */
	@Deprecated
	public EpsilonBoxConstraintComparator(double[] epsilons) {
		super(epsilons);
	}

}

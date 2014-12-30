/* Copyright 2009-2015 David Hadka
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
package org.moeaframework.core;

import java.io.Serializable;

/**
 * Interface for decision variables. This interface ensures independent copies
 * of decision variables can be constructed.
 */
public interface Variable extends Serializable {

	/**
	 * Returns an independent copy of this decision variable. It is required
	 * that {@code x.copy()} is completely independent from {@code x}. This
	 * means any method invoked on {@code x.copy()} in no way alters the state
	 * of {@code x} and vice versa. It is typically the case that
	 * {@code x.copy().getClass() == x.getClass()} and
	 * {@code x.copy().equals(x)}.
	 * 
	 * @return an independent copy of this decision variable
	 */
	public Variable copy();

}

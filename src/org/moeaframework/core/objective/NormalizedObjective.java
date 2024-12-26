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
package org.moeaframework.core.objective;

/**
 * A normalized objective.  By convention, normalized objectives are minimized.  This implementation is distinct from
 * {@link Minimize} since some methods are either redundant or unsupported on a normalized objective.  Such methods
 * will throw a {@link UnsupportedOperationException} if used.
 */
public class NormalizedObjective extends Minimize {
	
	private static final long serialVersionUID = 1030777861399215909L;
	
	/**
	 * Constructs a new normalized objective.
	 * 
	 * @param name the objective name
	 */
	public NormalizedObjective(String name) {
		super(name);
	}
	
	/**
	 * Constructs a new normalized objective.
	 * 
	 * @param name the objective name
	 * @param value the objective value
	 */
	public NormalizedObjective(String name, double value) {
		super(name);
		this.value = value;
	}

	@Override
	public NormalizedObjective copy() {
		return new NormalizedObjective(name, value);
	}
	
	/**
	 * This method is not supported for normalized objectives.
	 * 
	 * @throws UnsupportedOperationException as this operation is not permitted on normalized objectives
	 */
	@Override
	public NormalizedObjective normalize(double minimum, double maximum) {
		// Prevent normalizing an already normalized objective
		throw unsupportedOperation("normalize");
	}
	
	/**
	 * This method is not supported for normalized objectives.
	 * 
	 * @throws UnsupportedOperationException as this operation is not permitted on normalized objectives
	 */
	@Override
	public int getEpsilonIndex(double epsilon) {
		// Not allowed since epsilon is not scaled
		throw unsupportedOperation("getEpsilonIndex");
	}
	
	/**
	 * This method is not supported for normalized objectives.
	 * 
	 * @throws UnsupportedOperationException as this operation is not permitted on normalized objectives
	 */
	@Override
	public double getEpsilonDistance(double epsilon) {
		// Not allowed since epsilon is not scaled
		throw unsupportedOperation("getEpsilonDistance");
	}
	
	private UnsupportedOperationException unsupportedOperation(String methodName) {
		return new UnsupportedOperationException(methodName + " is not supported on a normalized objective");
	}

}

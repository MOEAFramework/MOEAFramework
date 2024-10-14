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
package org.moeaframework.core.objective;

public class NormalizedObjective extends Minimize {
	
	private static final long serialVersionUID = 1030777861399215909L;
	
	public NormalizedObjective(double value) {
		super(value);
	}

	@Override
	public NormalizedObjective copy() {
		return new NormalizedObjective(getValue());
	}
	
	@Override
	public NormalizedObjective normalize(double minimum, double maximum) {
		// Prevent normalizing an already normalized objective
		throw unsupportedOperation("normalize");
	}
	
	@Override
	public int getEpsilonIndex(double epsilon) {
		// Not allowed since epsilon is not scaled
		throw unsupportedOperation("getEpsilonIndex");
	}
	
	@Override
	public double getEpsilonDistance(double epsilon) {
		// Not allowed since epsilon is not scaled
		throw unsupportedOperation("getEpsilonDistance");
	}
	
	private UnsupportedOperationException unsupportedOperation(String methodName) {
		return new UnsupportedOperationException(methodName + " is not supported on a normalized objective");
	}

}

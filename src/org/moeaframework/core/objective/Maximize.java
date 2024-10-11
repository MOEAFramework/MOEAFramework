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

public class Maximize extends AbstractObjective {
	
	private static final long serialVersionUID = -7464482549220819352L;

	public Maximize() {
		super();
	}
	
	protected Maximize(double value) {
		super(value);
	}
	
	@Override
	public double getCanonicalValue() {
		return -getValue();
	}

	@Override
	public int compareTo(double value) {
		return -Double.compare(getValue(), value);
	}

	@Override
	public Maximize copy() {
		return new Maximize(getValue());
	}
	
	@Override
	public NormalizedObjective normalize(double minimum, double maximum) {
		return new NormalizedObjective((minimum - getValue()) / (maximum - minimum), minimum, maximum);
	}
	
	@Override
	public int getEpsilonIndex(double epsilon) {
		return (int)Math.floor(-getValue() / epsilon);
	}
	
	@Override
	public double getEpsilonDistance(double epsilon) {
		return Math.abs(-getValue() - getEpsilonIndex(epsilon) * epsilon);
	}
	
	@Override
	public double applyWeight(double weight) {
		return weight * -getValue();
	}
	
	@Override
	public double getIdealValue() {
		return Double.POSITIVE_INFINITY;
	}
	
}

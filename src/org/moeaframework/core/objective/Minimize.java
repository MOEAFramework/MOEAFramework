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
 * A minimized objective.
 */
public class Minimize extends AbstractObjective {
	
	private static final long serialVersionUID = -7464482549220819352L;

	/**
	 * Constructs a new, anonymous minimized objective.
	 */
	public Minimize() {
		super();
	}
	
	/**
	 * Constructs a new minimized objective.
	 * 
	 * @param name the objective name
	 */
	public Minimize(String name) {
		super(name);
	}
	
	/**
	 * Constructs a new minimized objective.
	 * 
	 * @param name the objective name
	 * @param value the objective value
	 */
	protected Minimize(String name, double value) {
		super(name);
		setValue(value);
	}
	
	@Override
	public double getCanonicalValue() {
		return getValue();
	}
	
	@Override
	public int compareTo(double value) {
		return Double.compare(getValue(), value);
	}

	@Override
	public Minimize copy() {
		Minimize copy = new Minimize(name);
		copy.value = value;
		return copy;
	}
	
	@Override
	public NormalizedObjective normalize(double minimum, double maximum) {
		return new NormalizedObjective(name, (getValue() - minimum) / (maximum - minimum));
	}
	
	@Override
	public double getIdealValue() {
		return Double.NEGATIVE_INFINITY;
	}
	
	/**
	 * Constructs a new, anonymous minimized objective.
	 * 
	 * @return the objective
	 */
	public static Minimize value() {
		return new Minimize();
	}
	
	/**
	 * Constructs a new minimized objective.
	 * 
	 * @param name the objective name
	 * @return the objective
	 */
	public static Minimize value(String name) {
		return new Minimize(name);
	}
	
}

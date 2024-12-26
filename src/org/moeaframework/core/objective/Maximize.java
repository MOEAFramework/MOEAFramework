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
 * A maximized objective.
 */
public class Maximize extends AbstractObjective {
	
	private static final long serialVersionUID = -7464482549220819352L;

	/**
	 * Constructs a new, anonymous maximized objective.
	 */
	public Maximize() {
		super();
	}
	
	/**
	 * Constructs a new maximized objective.
	 * 
	 * @param name the objective name
	 */
	public Maximize(String name) {
		super(name);
	}
	
	/**
	 * Constructs a new maximized objective.
	 * 
	 * @param name the objective name
	 * @param value the objective value
	 */
	protected Maximize(String name, double value) {
		this(name);
		setValue(value);
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
		Maximize copy = new Maximize(name);
		copy.value = value;
		return copy;
	}
	
	@Override
	public NormalizedObjective normalize(double minimum, double maximum) {
		return new NormalizedObjective(name, 1.0 - (getValue() - minimum) / (maximum - minimum));
	}
	
	@Override
	public double getIdealValue() {
		return Double.POSITIVE_INFINITY;
	}
	
	/**
	 * Constructs a new, anonymous maximized objective.
	 * 
	 * @return the objective
	 */
	public static Maximize value() {
		return new Maximize();
	}
	
	/**
	 * Constructs a new maximized objective.
	 * 
	 * @param name the objective name
	 * @return the objective
	 */
	public static Maximize value(String name) {
		return new Maximize(name);
	}
	
}

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
package org.moeaframework.util.mvc;

/**
 * A setting representing a binary state (on/off, true/false, enabled/disabled).
 */
public class Toggle extends Setting<Boolean> {

	/**
	 * Constructs a toggle setting with the default value.
	 * 
	 * @param defaultValue the default value
	 */
	public Toggle(boolean defaultValue) {
		super(defaultValue);
	}
	
	/**
	 * Flips the value of this toggle.
	 */
	public void flip() {
		set(!get());
	}
	
}
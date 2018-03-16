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
package org.moeaframework.core.operator.real;

/**
 * This class has been renamed to {@link DifferentialEvolutionVariation}.  This
 * class is provided for backwards compatibility, but will be removed in
 * version 3.0.
 * 
 * @deprecated renamed to {@link DifferentialEvolutionVariation}
 */
@Deprecated
public class DifferentialEvolution extends DifferentialEvolutionVariation {

	/**
	 * See {@link DifferentialEvolutionVariation#DifferentialEvolutionVariation(double, double)}
	 * for details.
	 */
	@Deprecated
	public DifferentialEvolution(double CR, double F) {
		super(CR, F);
	}

}

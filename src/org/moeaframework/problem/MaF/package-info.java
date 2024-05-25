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

/**
 * The MaF test problem suite for many-objective optimization.  These problems, often variants of problems found in
 * other test suites, are intended to represent real-world scenarios.
 * <p>
 * The PlatEMO implementation, which is where the authors originally developed this test suite, was referenced while
 * developing this Java implementation.  A few discrepancies have been observed between the cited paper and the PlatEMO
 * implementation, including:
 * <ul>
 *   <li>MaF5 - Equation 9 in the paper includes {@code ^4} on each objective function, but missing from PlatEMO
 *   <li>MaF6 - Equation 12 uses {@code 1} in the numerator on the second line, but PlatEMO uses {@code PI}
 *   <li>MaF13 - Equation 19 uses {@code n} but is undefined, based on PlatEMO this value is the same as {@code D}
 * </ul>
 * For consistency, we have adopted the PlatEMO implementation.
 * <p>
 * References:
 * <ol>
 *   <li>Cheng, Li, Tian, Zhang, Yang, Jin, and Yao.  "A benchmark test suite for evolutionary many-objective
 *       optimization."  Complex Intell. Syst., 3:67-81, 2017.
 *   <li>https://github.com/BIMK/PlatEMO
 * </ol>
 */
package org.moeaframework.problem.MaF;
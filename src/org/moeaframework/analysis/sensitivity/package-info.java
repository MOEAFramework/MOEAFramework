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

/**
 * Utilities for analyzing an algorithm using control maps and sensitivity
 * analysis.  These utilities are currently only configured to work on
 * real-valued problems.  Academic works which use this package should cite
 * the referenced article below.
 * <p>
 * References:
 * <ol>
 *   <li>Hadka, D. and Reed, P.  "Diagnostic Assessment of Search Controls and
 *       Failure Modes in Many-Objective Evolutionary Optimization."
 *       Evolutionary Computation, 20(3):423-452.
 * </ol>
 */
package org.moeaframework.analysis.sensitivity;
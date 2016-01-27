/* Copyright 2009-2016 David Hadka
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
 * Package containing the BBOB 2016 bi-objective test problems.  These problems
 * will be tested in the Workshop on Real-Parameter Black-Box Optimization
 * Benchmarking at GECCO 2016.  The authors combine various single-objective
 * functions to form the bi-objective test suite.  Each of the
 * single-objective functions has well-known problem properties, including
 * looking at the conditioning number.  Conditioning looks at the effect of
 * small changes to the function's inputs on its output.  For example, small
 * changes to a low conditioned function causes only small changes in the
 * output.
 * <p>
 * Note: this implementation does not provide all BBOB test functions.  It
 * currently only implements the 55 test functions studied in the bbob-biobj
 * suite.
 * <p>
 * References:
 * <ol>
 *   <li><a href="https://github.com/numbbo/coco">Coco Github page</a>
 *   <li>Finck, S., N. Hansen, R. Ros, and A. Auger.  "Real-Parameter Black-Box
 *       Optimization Benchmarking 2010: Presentation of the Noiseless
 *       Functions."  Working Paper 2009/20, compiled November 17, 2015.
 *       <a href="http://coco.lri.fr/downloads/download15.03/bbobdocfunctions.pdf">(PDF)</a>
 * </ol>
 */
package org.moeaframework.problem.BBOB2016;
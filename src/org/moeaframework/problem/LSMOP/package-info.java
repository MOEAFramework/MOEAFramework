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
 * Package for the scalable LSMOP test problem suite.
 * <p>
 * These codes are based on the Matlab source code provided by the authors.  Additionally, we use the Matlab source to
 * generate the test results:
 * <pre>
 *   Population = LSMOP('init', 'LSMOP1', 2, 10)
 *   [ps,D] = size(Population)
 *   LSMOP('fitness', 'LSMOP1', 2, 0.5 * ones(1,D))   % Evaluate solution (0.5, 0.5, ..., 0.5)
 * </pre>
 * and the Pareto Fronts:
 * <pre> 
 *   PF = LSMOP('PF', 'LSMOP1', 2, 100)
 *   writematrix(PF,'LSMOP1.2D.pf','Delimiter',' ','FileType','text')
 * </pre>
 * References:
 * <ol>
 *   <li>Ran Cheng, Yaochu Jin, Markus Olhofer and Bernhard Sendhoff. "Test problems for large-scale multiobjective
 *       and many-objective optimization." IEEE Transactions on Cybernetics, 7(12): 4108-4121, 2017.
 *   <li>Supplementary Materials available at http://www.soft-computing.de/jin-pub_year.html
 * </ol>
 */
package org.moeaframework.problem.LSMOP;
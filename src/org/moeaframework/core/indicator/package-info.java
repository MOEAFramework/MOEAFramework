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
 * Collection of unary quality indicators for comparing the quality of
 * non-dominated approximation sets. The table below shows the supported
 * indicators and key properties. <em>Pareto compliant</em> implies that
 * better indicator values correspond to approximation sets that are 
 * preferred by weak Pareto dominance. 
 * <p>
 * <table width="100%" border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="20%" align="left">Indicator</th>
 *     <th width="20%" align="left">Pareto Compliant</th>
 *     <th width="20%" align="left">Reference Set Required</th>
 *     <th width="20%" align="left">Normalized</th>
 *     <th width="20%" align="left">Target</th>
 *   </tr>
 *   <tr>
 *     <td>{@link org.moeaframework.core.indicator.Hypervolume}</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>Maximize &rarr; 1</td>
 *   </tr>
 *   <tr>
 *     <td>{@link org.moeaframework.core.indicator.GenerationalDistance}</td>
 *     <td>No</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>Minimize &rarr; 0</td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link org.moeaframework.core.indicator.InvertedGenerationalDistance}
 *     </td>
 *     <td>No</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>Minimize &rarr; 0</td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link org.moeaframework.core.indicator.AdditiveEpsilonIndicator}
 *     </td>
 *     <td>Yes</td>
 *     <td>No</td>
 *     <td>Yes</td>
 *     <td>Minimize &rarr; 0</td>
 *   </tr>
 *   <tr>
 *     <td>{@link org.moeaframework.core.indicator.MaximumParetoFrontError}</td>
 *     <td>No</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>Minimize &rarr; 0</td>
 *   </tr>
 *   <tr>
 *     <td>{@link org.moeaframework.core.indicator.Spacing}</td>
 *     <td>No</td>
 *     <td>No</td>
 *     <td>No</td>
 *     <td>Minimize &rarr; 0</td>
 *   </tr>
 *   <tr>
 *     <td>{@link org.moeaframework.core.indicator.Contribution}</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>No</td>
 *     <td>Maximize &rarr; 1</td>
 *   </tr>
 *   <tr>
 *     <td>{@link org.moeaframework.core.indicator.R2Indicator}</td>
 *     <td>No</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>Minimize &rarr; -1</td>
 *   </tr>
 * </table>
 * <p>
 * References:
 * <ol>
 *   <li>Knowles, J. and D. Corne.  "On Metrics for Comparing Non-Dominated 
 *       Sets."  Proceedings of the 2002 Congress on Evolutionary Computation, 
 *       pp. 711-716, 2002.
 *   <li>Coello Coello, C.A. et al.  "Evolutionary Algorithms for Solving 
 *       Multi-Objective Problems."  2nd Edition, Springer, 2007.
 * </ol>
 */
package org.moeaframework.core.indicator;
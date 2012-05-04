/* Copyright 2009-2012 David Hadka
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
 * Collectors record information periodically during the execution of an
 * algorithm.  The classes contained in this package implement the various
 * collectors and support utilities, but the preferred method of using
 * collectors is through the {@link org.moeaframework.Instrumenter}.  The table
 * below contains the potential data collected by the instrumenter.  The key
 * column shows the string used to retrieve the data from the
 * {@link org.moeaframework.analysis.collector.Accumulator}.
 * 
 * <table width="100%" border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="25%" align="left">Key</th>
 *     <th width="75%" align="left">Description</th>
 *   </tr>
 *   <tr>
 *     <td>{@code NFE}</td>
 *     <td>
 *       The number of objective function evaluations.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Approximation Set}</td>
 *     <td>
 *       The approximation set of non-dominated solutions discovered by the
 *       algorithm.  This collector consumes large quantities of memory.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Elapsed Time}</td>
 *     <td>
 *       The wall-clock time elapsed since the start of the algorithm.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Number of Restarts}</td>
 *     <td>
 *       The number of time continuation or &epsilon;-continuation restarts.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Number of Improvements}</td>
 *     <td>
 *       The number of &epsilon;-dominance improvements detected.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Number of Dominating Improvements}</td>
 *     <td>
 *       The number of strictly dominating &epsilon;-dominance improvements
 *       detected.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>Indicator Name</td>
 *     <td>
 *       The value of a performance indicator.  Current indicators include
 *       {@code AdditiveEpsilonIndicator}, {@code Contribution},
 *       {@code GenerationalDistance}, {@code Hypervolume},
 *       {@code InvertedGenerationalDistance},
 *       {@code MaximumParetoFrontError} and {@code Spacing}.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Population Size}</td>
 *     <td>
 *       The size of the population.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Archive Size}</td>
 *     <td>
 *       The size of the archive.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>Operator Name</td>
 *     <td>
 *       The probability of an operator being selected by adaptive multimethod
 *       variation.  Use the string name for the operator used by
 *       {@link OperatorFactory}, such as {@code "PCX"} or {@code "SBX+PM"}.
 *     </td>
 *   </tr>
 * </table>
 */
package org.moeaframework.analysis.collector;
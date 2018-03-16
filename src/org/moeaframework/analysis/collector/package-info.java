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
 *     <th width="50%" align="left">Description</th>
 *     <th width="25%" align="left">Collector</th>
 *   </tr>
 *   <tr>
 *     <td>{@code NFE}</td>
 *     <td>
 *       The number of objective function evaluations.
 *     </td>
 *     <td>
 *       None (Always Saved)
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Approximation Set}</td>
 *     <td>
 *       The approximation set of non-dominated solutions discovered by the
 *       algorithm.  This collector consumes large quantities of memory.
 *     </td>
 *     <td>
 *       {@link org.moeaframework.analysis.collector.ApproximationSetCollector}
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Elapsed Time}</td>
 *     <td>
 *       The wall-clock time elapsed since the start of the algorithm.
 *     </td>
 *     <td>
 *       {@link org.moeaframework.analysis.collector.ElapsedTimeCollector}
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Number of Restarts}</td>
 *     <td>
 *       The number of time continuation or &epsilon;-continuation restarts.
 *     </td>
 *     <td>
 *       {@link org.moeaframework.analysis.collector.AdaptiveTimeContinuationCollector}
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Number of Improvements}</td>
 *     <td>
 *       The number of &epsilon;-dominance improvements detected.
 *     </td>
 *     <td>
 *       {@link org.moeaframework.analysis.collector.EpsilonProgressCollector}
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Number of Dominating Improvements}</td>
 *     <td>
 *       The number of strictly dominating &epsilon;-dominance improvements
 *       detected.
 *     </td>
 *     <td>
 *       {@link org.moeaframework.analysis.collector.EpsilonProgressCollector}
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Population Size}</td>
 *     <td>
 *       The size of the population.
 *     </td>
 *     <td>
 *       {@link org.moeaframework.analysis.collector.PopulationSizeCollector}
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code Archive Size}</td>
 *     <td>
 *       The size of the archive.
 *     </td>
 *     <td>
 *       {@link org.moeaframework.analysis.collector.PopulationSizeCollector}
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code AdditiveEpsilonIndicator}</td>
 *     <td>
 *       The value of the additive &epsilon;-indicator performance indicator.
 *     </td>
 *     <td>{@link org.moeaframework.analysis.collector.IndicatorCollector}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code Contribution}</td>
 *     <td>
 *       The value of the contribution/coverage performance indicator.
 *     </td>
 *     <td>{@link org.moeaframework.analysis.collector.IndicatorCollector}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code GenerationalDistance}</td>
 *     <td>
 *       The value of the generational distance performance indicator.
 *     </td>
 *     <td>{@link org.moeaframework.analysis.collector.IndicatorCollector}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code Hypervolume}</td>
 *     <td>
 *       The value of the hypervolume performance indicator.
 *     </td>
 *     <td>{@link org.moeaframework.analysis.collector.IndicatorCollector}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code InvertedGenerationalDistance}</td>
 *     <td>
 *       The value of the inverted generational distance performance indicator.
 *     </td>
 *     <td>{@link org.moeaframework.analysis.collector.IndicatorCollector}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code MaximumParetoFrontError}</td>
 *     <td>
 *       The value of the maximum Pareto front error performance indicator.
 *     </td>
 *     <td>{@link org.moeaframework.analysis.collector.IndicatorCollector}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code Spacing}</td>
 *     <td>
 *       The value of the spacing performance indicator.
 *     </td>
 *     <td>{@link org.moeaframework.analysis.collector.IndicatorCollector}</td>
 *   </tr>
 *   <tr>
 *     <td>Operator Name</td>
 *     <td>
 *       The probability of an operator being selected by adaptive multimethod
 *       variation.  Use the string name for the operator used by
 *       {@link org.moeaframework.core.spi.OperatorFactory}, such as
 *       {@code "PCX"} or {@code "SBX+PM"}.
 *     </td>
 *     <td>
 *       {@link org.moeaframework.analysis.collector.AdaptiveMultimethodVariationCollector}
 *     </td>
 *   </tr>
 * </table>
 */
package org.moeaframework.analysis.collector;
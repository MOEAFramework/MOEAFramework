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
 * Tools for analyzing the runtime and end-of-run behavior of algorithms.
 * 
 * <table style="margin-top: 1em; width: 100%">
 *   <caption>List of subpackages</caption>
 *   <tr>
 *     <th style="width: 25%; text-align: left">Package</th>
 *     <th style="width: 75%; text-align: left">Description</th>
 *   </tr>
 *   <tr>
 *     <td>{@link org.moeaframework.analysis.collector}</td>
 *     <td>
 *       The collector API provides the tools for collecting runtime information from algorithms and their internal
 *       components.  A suite of collectors are provided in this package.  See the
 *       {@link org.moeaframework.Instrumenter} for details on using the collector API.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@link org.moeaframework.analysis.diagnostics}</td>
 *     <td>
 *       Diagnostic tool for analyzing the runtime behavior of algorithms.  This tool requires the 
 *       <a href="http://www.jfree.org/jfreechart/">JFreeChart</a> library and its dependencies to be installed
 *       (which are provided by default in the MOEA Framework distribution).
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@link org.moeaframework.analysis.io}</td>
 *     <td>
 *       Classes for reading and writing various file formats.  This includes parameter configuration files, sample
 *       files, result files, and metric files.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@link org.moeaframework.analysis.plot}</td>
 *     <td>
 *       Generate simple 2D plots.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@link org.moeaframework.analysis.tools}</td>
 *     <td>
 *       Command line tools for solving optimization problems and analyzing the results.  This includes tools for
 *       running large-scale experiments on high performance computers (HPC), comparing results, and performing
 *       sensitivity analysis on parameters.
 *     </td>
 *   </tr>
 * </table>
 */
package org.moeaframework.analysis;
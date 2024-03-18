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
 * File formats used by the MOEA Framework analysis codes.  The main file formats are:
 * <ol>
 *   <li>Parameter configuration files - Stores a list of parameters and their bounds;
 *   <li>Samples file - Stores the sampled parameters, each row corresponding to a set of parameters;
 *   <li>Result file - Stores approximation set outputs from one or more runs; and
 *   <li>Metric file - Stores the standard metrics (hypervolume, generational distance, etc.) corresponding to entries
 *       in a result file.
 * </ol>
 */
package org.moeaframework.analysis.io;
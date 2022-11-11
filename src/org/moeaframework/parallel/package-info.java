/* Copyright 2009-2022 David Hadka
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
 * Provides island model and other parallelization strategies for optimization
 * algorithms.  "Parallel" in this context means running multiple optimization
 * algorithms together in some fashion.
 * 
 * That does not necessarily mean these are "parallel" in the computing sense,
 * where work is distributed across multiple cores or machines.  They can and
 * do support such parallelization, but that typically requires integrating
 * with a third-party compute fabric library like Apache Spark or Apache Ignite.
 * 
 * @preview
 */
package org.moeaframework.parallel;
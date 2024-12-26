/* Copyright 2009-2025 David Hadka
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
 * Support for distributed and parallel computing for optimization algorithms.  Two modes are currently supported:
 * <p>
 * <strong>Distributed evaluations</strong> - Also known as master-slave or master-worker parallelization, this
 * distributes individual function evaluations across multiple processors or computers, either locally or remotely.
 * See {@link DistributedProblem} for details.
 * <p>
 * <strong>Island model</strong> - This parallelization strategy runs multiple instances of an algorithm (the islands),
 * often with some kind of migration strategy to share solutions between the islands.
 */
package org.moeaframework.parallel;
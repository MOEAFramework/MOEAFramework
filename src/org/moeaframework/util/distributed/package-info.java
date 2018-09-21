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
 * Classes to enable distributed computing.  Algorithms that are naturally
 * parallel can be parallelized using this package without altering the 
 * algorithm.  This is feasible using the {@code Future} concept of blocking
 * only when attempting to read the result and the evaluation has not yet
 * completed.  As long as the algorithm submits multiple jobs to the 
 * {@code evaluate} method prior to reading the results, the objectives 
 * and constraints, the algorithm is naturally parallel.
 * <p>
 * To use, an {@code ExecutorService} is required that will distributed the
 * jobs to asynchronous threads, cores or compute nodes.  Java frameworks such
 * as <a href="http://www.jppf.org">JPPF</a> and 
 * <a href="http://www.gridgain.com">GridGain</a> provide 
 * {@code ExecutorService} interfaces out-of-the-box.  Then, the desired
 * {@code Problem} is decorated with the {@code DistributedProblem} to enable
 * parallel execution.
 */
package org.moeaframework.util.distributed;
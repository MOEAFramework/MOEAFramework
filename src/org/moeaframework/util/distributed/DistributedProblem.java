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
package org.moeaframework.util.distributed;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Distributes the {@link #evaluate(Solution)} method across multiple threads, cores or compute nodes using the
 * provided {@link ExecutorService}.  The {@code ExecutorService} defines the type and method of distribution. The
 * problem must be {@link Serializable} if executing on remote nodes.
 * 
 * @deprecated Moved to {@link org.moeaframework.parallel.DistributedProblem}
 */
@Deprecated
public class DistributedProblem extends org.moeaframework.parallel.DistributedProblem {

	public DistributedProblem(Problem problem, ExecutorService executor, boolean shutdownWhenClosed) {
		super(problem, executor, shutdownWhenClosed);
	}

	public DistributedProblem(Problem problem, ExecutorService executor) {
		super(problem, executor);
	}

}

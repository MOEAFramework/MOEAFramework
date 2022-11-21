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
package org.moeaframework.parallel.island.executor;

import java.io.Closeable;

import org.moeaframework.core.NondominatedPopulation;

/**
 * Executes an island model strategy.  This class is responsible for taking the
 * conceptual design of the island model, as described in {@see IslandModel} and
 * executing it on physical hardware, whether that is a single core, multiple cores,
 * or multiple machines.
 */
public interface IslandExecutor extends Closeable {

	/**
	 * Executes this island model for the given number of function evaluations.
	 * 
	 * @param maxEvaluations the maximum number of evaluations across all islands
	 * @return the resulting non-dominated population aggregated across all islands
	 */
	public NondominatedPopulation run(int maxEvaluations);

}
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
package org.moeaframework.parallel.island.executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.parallel.island.Island;
import org.moeaframework.parallel.island.IslandModel;

/**
 * Executes an island model using an {@see ExecutorService}.
 */
public class BasicIslandExecutor implements IslandExecutor {
	
	private final IslandModel model;
	
	private final ExecutorService executorService;
	
	private final List<Island> islands;
	
	/**
	 * Constructs an island model executor using an {@code ExecutorService}.
	 * 
	 * @param model the island model
	 * @param executorService the executor service that drives each island
	 */
	public BasicIslandExecutor(IslandModel model, ExecutorService executorService) {
		super();
		this.model = model;
		this.executorService = executorService;
		
		this.islands = model.getIslands();
	}

	@Override
	public NondominatedPopulation run(int maxEvaluations) {
		final int evaluationsPerIsland = maxEvaluations / islands.size();
			
		//start threads to process each island
		List<Future<NondominatedPopulation>> futures = new ArrayList<>();
			
		for (final Island island : islands) {
			futures.add(executorService.submit(() -> {
				Algorithm algorithm = island.getAlgorithm();
				algorithm.addExtension(new IslandMigrationExtension(island, model));
				algorithm.run(evaluationsPerIsland);
				return algorithm.getResult();
			}));
		}

		//wait for all to complete and aggregate the result
		NondominatedPopulation result = new NondominatedPopulation();

		for (Future<NondominatedPopulation> future : futures) {
			try {
				result.addAll(future.get());
			} catch (InterruptedException e) {
				throw new FrameworkException("Execution of island model was interrupted", e);
			} catch (ExecutionException e) {
				throw new FrameworkException("Execution of island model failed", e);
			}
		}

		return result;
	}

	@Override
	public void close() throws IOException {
		executorService.shutdown();
	}

}

package org.moeaframework.parallel.island.executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.parallel.island.Island;
import org.moeaframework.parallel.island.IslandModel;
import org.moeaframework.parallel.island.migration.IslandMigrationAction;

/**
 * Executes an island model using an {@see ExecutorService}.
 */
public class BasicIslandExecutor implements IslandExecutor {
	
	private final IslandModel model;
	
	private final ExecutorService executorService;
	
	private final List<Island> islands;
	
	public BasicIslandExecutor(IslandModel model, ExecutorService executorService) {
		super();
		this.model = model;
		this.executorService = executorService;
		
		this.islands = model.getIslands();
	}

	@Override
	public NondominatedPopulation run(final int maxEvaluations) {
		final int evaluationsPerIsland = maxEvaluations / islands.size();
			
		//start threads to process each island
		List<Future<NondominatedPopulation>> futures = new ArrayList<Future<NondominatedPopulation>>();
			
		for (Island island : islands) {
			futures.add(executorService.submit(new Callable<NondominatedPopulation>() {

				@Override
				public NondominatedPopulation call() {
					IslandMigrationAction action = new IslandMigrationAction(island, model);
					
					while (action.getNumberOfEvaluations() < evaluationsPerIsland) {
						action.step();
					}
					
					return action.getResult();
				}
				
			}));
		}

		//wait for all to complete and aggregate the result
		NondominatedPopulation result = new NondominatedPopulation();

		for (Future<NondominatedPopulation> future : futures) {
			try {
				result.addAll(future.get());
			} catch (InterruptedException e) {
				throw new FrameworkException("execution was interrupted", e);
			} catch (ExecutionException e) {
				throw new FrameworkException("execution failed", e);
			}
		}

		return result;
	}

	@Override
	public void close() throws IOException {
		executorService.shutdown();
	}

}

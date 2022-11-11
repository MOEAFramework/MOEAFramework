package org.moeaframework.parallel.island.executor;

import java.util.ArrayList;
import java.util.List;
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
	
	/**
	 * Sums the number of function evaluations across all islands.
	 * 
	 * @return the total number of function evaluations
	 */
	public int getNumberOfEvaluations() {
		synchronized (islands) {
			int total = 0;
			
			for (Island island : islands) {
				total += island.getAlgorithm().getNumberOfEvaluations();
			}
			
			return total;
		}
	}

	@Override
	public NondominatedPopulation run(final int maxEvaluations) {
		final int startingEvaluations = getNumberOfEvaluations();
		List<IslandMigrationAction> migrationActions = new ArrayList<IslandMigrationAction>();
		
		//initialize the migration actions
		synchronized (islands) {
			for (Island island : islands) {
				migrationActions.add(new IslandMigrationAction(island, model));
			}
		}
			
		//execute each island
		List<Future<?>> futures = new ArrayList<Future<?>>();
			
		for (final IslandMigrationAction action : migrationActions) {
			futures.add(executorService.submit(new Runnable() {

				@Override
				public void run() {
					while (getNumberOfEvaluations() - startingEvaluations < maxEvaluations) {
						action.step();
					}
				}
				
			}));
		}

		//wait for all to complete
		for (Future<?> future : futures) {
			try {
				future.get();
			} catch (InterruptedException e) {
				throw new FrameworkException("execution was interrupted", e);
			} catch (ExecutionException e) {
				throw new FrameworkException("execution failed", e);
			}
		}
		
		//aggregate the result
		NondominatedPopulation result = new NondominatedPopulation();

		for (IslandMigrationAction action : migrationActions) {
			result.addAll(action.getResult());
		}

		return result;
	}

}

package org.moeaframework.parallel.island.executor;

import java.util.concurrent.Executors;

import org.moeaframework.parallel.island.IslandModel;

/**
 * Executes an island model locally using multiple threads.
 */
public class ThreadedIslandExecutor extends BasicIslandExecutor {
	
	public ThreadedIslandExecutor(IslandModel model) {
		super(model, Executors.newFixedThreadPool(model.getIslands().size()));
	}

}

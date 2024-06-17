package org.moeaframework.experiment;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.moeaframework.experiment.job.Job;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.util.DurationUtils;

public class Experiment {

	private final DataStore dataStore;

	private final ExecutorService executorService;

	private final ExecutorCompletionService<Job> completionService;

	private final Logger logger;

	private final Map<DataReference, Set<Job>> dependencyMap;

	private final Set<Job> pendingJobs;

	private final Set<Job> activeJobs;

	private final List<Throwable> suppressedExceptions;

	private final AtomicBoolean isShutdown;

	private final AtomicBoolean isStale;

	private final AtomicBoolean showErrors;

	private JobDispatchThread thread;

	public Experiment(DataStore dataStore, ExecutorService executorService, Logger logger) {
		super();
		this.dataStore = dataStore;
		this.executorService = executorService;
		this.logger = initLogger(logger);

		completionService = new ExecutorCompletionService<>(executorService);

		dependencyMap = Collections.synchronizedMap(new HashMap<>());
		activeJobs = Collections.synchronizedSet(new HashSet<>());
		pendingJobs = Collections.synchronizedSet(new HashSet<>());
		suppressedExceptions = Collections.synchronizedList(new ArrayList<>());

		isShutdown = new AtomicBoolean();
		isStale = new AtomicBoolean();
		showErrors = new AtomicBoolean(true);

		start();
	}

	private Logger initLogger(Logger logger) {
		if (logger != null) {
			return logger;
		}

		logger = Logger.getLogger(Experiment.class.getName());
		logger.setLevel(Level.OFF);
		return logger;
	}

	private void start() {
		if (thread != null) {
			logger.info("Experiment already started");
		}

		thread = new JobDispatchThread();
		thread.start();
	}

	public void submit(Job job) {
		if (isShutdown()) {
			logger.info("Experiment is shutdown, not accepting new jobs!");
			return;
		}

		if (job.isComplete(dataStore)) {
			if (job.isStale(dataStore)) {
				logger.info("Detected stale job " + job + ", removing stale data");
				job.produces().forEach(x -> dataStore.writer(x).delete());
				isStale.set(true);
			} else {
				logger.info("Skipping " + job + ", already completed!");
				return;
			}
		}

		if (job.isReady(dataStore)) {
			startJob(job);
			return;
		}

		queueJob(job);
	}

	private void startJob(Job job) {
		logger.info("Starting " + job);

		completionService.submit(() -> job.execute(dataStore), job);

		activeJobs.add(job);
		pendingJobs.remove(job);
	}

	private void queueJob(Job job) {
		logger.info("Queueing " + job + ", waiting on one or more dependencies...");
		pendingJobs.add(job);

		for (DataReference dependency : job.requires()) {
			if (!dependencyMap.containsKey(dependency)) {
				dependencyMap.putIfAbsent(dependency, Collections.synchronizedSet(new HashSet<Job>()));
			}

			dependencyMap.get(dependency).add(job);
		}
	}

	private void postShutdown() throws ExperimentException {
		if (isStale.get()) {
			System.err.println("""
					Detected stale data during execution.  Most jobs should automatically clean up and re-evaluate any
					stale data, but we also recommend re-running the experiment until this message no longer appears.
					""");
		}

		throwIfException();
	}

	private void throwIfException() throws ExperimentException {
		if (!suppressedExceptions.isEmpty()) {
			ExperimentException exception = new ExperimentException(
					"Caught " + suppressedExceptions.size() + " exception(s) during experiment",
					suppressedExceptions.get(0));

			for (int i = 1; i < suppressedExceptions.size(); i++) {
				exception.addSuppressed(suppressedExceptions.get(i));
			}

			throw exception;
		}
	}

	public void setShowErrors(boolean showErrors) {
		this.showErrors.set(showErrors);
	}

	public void shutdownAndWait() throws InterruptedException, ExperimentException {
		shutdown();
		awaitShutdown();
	}

	public void shutdownAndWait(Duration duration) throws InterruptedException, ExperimentException {
		shutdown();
		awaitShutdown(duration);
	}

	public void awaitShutdown() throws InterruptedException, ExperimentException {
		thread.join();
		postShutdown();
	}

	public void awaitShutdown(Duration duration) throws InterruptedException, ExperimentException {
		thread.join(DurationUtils.toMilliseconds(duration));
		postShutdown();
	}

	public void shutdown() {
		isShutdown.set(true);
		thread.interrupt();
	}

	public void shutdownNow() {
		shutdown();
		executorService.shutdownNow();
	}

	public boolean isStarted() {
		return thread != null;
	}

	public boolean isShutdown() {
		return isShutdown.get();
	}

	private class JobDispatchThread extends Thread {

		@Override
		public void run() {
			try {
				dispatchJobs();
			} catch (Exception e) {
				handleException(e);
			}
		}

		public void dispatchJobs() {
			while (!isShutdown.get() || !activeJobs.isEmpty() || !pendingJobs.isEmpty()) {
				try {
					// Convert Sets into Arrays so we can iterate over the jobs in a thread-safe way
					Job[] activeJobsArray = activeJobs.toArray(Job[]::new);
					Job[] pendingJobsArray = pendingJobs.toArray(Job[]::new);

					Map<Class<?>, List<Job>> counts = Stream.concat(
							Arrays.stream(activeJobsArray),
							Arrays.stream(pendingJobsArray))
							.collect(Collectors.groupingBy(x -> x.getClass()));

					int totalJobs = counts.values().stream().mapToInt(x -> x.size()).sum();

					logger.info("Processing " + totalJobs + " jobs" + (totalJobs == 0 ? "" :
						counts.keySet().stream().map(x -> counts.get(x).size() + " " + x.getSimpleName())
						.collect(Collectors.joining(", ", " (", ")"))));

					Future<Job> completedFuture = completionService.take();
					Job completedJob = completedFuture.get();
					activeJobs.remove(completedJob);

					logger.info("Completed " + completedJob);

					for (DataReference dependency : completedJob.produces()) {
						Set<Job> enqueuedJobs = new HashSet<Job>();
						Set<Job> dependentJobs = dependencyMap.get(dependency);

						if (dependentJobs != null) {
							for (Job job : dependencyMap.get(dependency)) {
								if (job.isReady(dataStore)) {
									startJob(job);
									enqueuedJobs.add(job);
								}
							}

							for (DataReference otherDependency : dependencyMap.keySet().toArray(DataReference[]::new)) {
								dependencyMap.get(otherDependency).removeAll(enqueuedJobs);
							}
						}
					}
				} catch (ExecutionException e) {
					handleException(e);
				} catch (InterruptedException e) {
					// interrupt indicates we are shutting down, but we continue to process remaining jobs
					continue;
				}
			}

			executorService.shutdown();
			logger.info("All jobs completed!");
		}

		protected void handleException(Throwable t) {
			if (showErrors.get()) {
				t.printStackTrace();
			}

			suppressedExceptions.add(t);
		}

	}

}

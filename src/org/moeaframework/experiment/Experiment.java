package org.moeaframework.experiment;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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

	private final Map<Future<Job>, Job> activeJobs;

	private final List<Throwable> suppressedExceptions;

	private final AtomicBoolean isShutdown;

	private final AtomicBoolean isStale;

	private JobDispatchThread thread;

	public Experiment(DataStore dataStore, ExecutorService executorService, Logger logger) {
		super();
		this.dataStore = dataStore;
		this.executorService = executorService;
		this.logger = initLogger(logger);

		completionService = new ExecutorCompletionService<>(executorService);

		dependencyMap = Collections.synchronizedMap(new HashMap<>());
		activeJobs = Collections.synchronizedMap(new HashMap<>());
		pendingJobs = Collections.synchronizedSet(new HashSet<>());
		suppressedExceptions = Collections.synchronizedList(new ArrayList<>());

		isShutdown = new AtomicBoolean();
		isStale = new AtomicBoolean();

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
			logger.warning("Experiment already started");
		}

		thread = new JobDispatchThread();
		thread.start();
	}

	public void submit(Job job) {
		if (isShutdown()) {
			logger.warning("Experiment is shutdown, not accepting new jobs!");
			return;
		}

		if (job.isComplete(dataStore)) {
			if (job.isStale(dataStore)) {
				logger.info("Detected stale inputs to " + job + ", cleaning up stale output");
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

		Future<Job> future = completionService.submit(() -> {
			job.execute(dataStore);
			return job;
		});

		activeJobs.put(future, job);
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
	
	private void completeJob(Future<Job> future) {
		try {
			Job job = future.get();
			
			logger.info("Completed " + job);
			activeJobs.remove(future);

			for (DataReference dependency : job.produces()) {
				Set<Job> enqueuedJobs = new HashSet<Job>();
				Set<Job> dependentJobs = dependencyMap.get(dependency);

				if (dependentJobs != null) {
					for (Job dependentJob : dependentJobs) {
						if (dependentJob.isReady(dataStore)) {
							startJob(dependentJob);
							enqueuedJobs.add(dependentJob);
						}
					}

					for (DataReference otherDependency : dependencyMap.keySet().toArray(DataReference[]::new)) {
						dependencyMap.get(otherDependency).removeAll(enqueuedJobs);
					}
				}
			}
		} catch (ExecutionException | CancellationException | InterruptedException e) {
			logger.severe("Failed " + activeJobs.get(future) + ": " + e.getMessage());
			activeJobs.remove(future);
			suppressedExceptions.add(e);
		}
	}
	
	private void checkDeadlock() {
		if (!activeJobs.isEmpty()) {
			return;
		}
		
		Job[] pendingJobsArray = pendingJobs.toArray(Job[]::new);
		boolean hasReadyJobs = false;
		
		for (Job job : pendingJobsArray) {
			if (job.isReady(dataStore)) {
				logger.warning("Detected ready job in the queue, likely a bug in the Job Dispatch Thread");
				startJob(job);
				hasReadyJobs = true;
			}
		}
		
		if (!hasReadyJobs && pendingJobsArray.length > 0) {
			logger.warning("Detected potential deadlock! Queue contains " + pendingJobsArray.length +
					" jobs with unsatisfied requirements.");
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
				logger.severe("Unhandled exception in dispatch thread: " + e.getMessage());
				suppressedExceptions.add(e);
			}
		}

		public void dispatchJobs() {
			Duration checkDuration = Duration.ofSeconds(15);
			Instant lastCheck = Instant.now();
			
			while (!isShutdown.get() || !activeJobs.isEmpty() || !pendingJobs.isEmpty()) {
				try {
					// Convert Sets into Arrays so we can iterate over the jobs in a thread-safe way
					Job[] activeJobsArray = activeJobs.values().toArray(Job[]::new);
					Job[] pendingJobsArray = pendingJobs.toArray(Job[]::new);

					Map<Class<?>, List<Job>> counts = Stream.concat(
							Arrays.stream(activeJobsArray),
							Arrays.stream(pendingJobsArray))
							.distinct()
							.collect(Collectors.groupingBy(x -> x.getClass()));

					int totalJobs = counts.values().stream().mapToInt(x -> x.size()).sum();

					logger.info("Processing " + totalJobs + " jobs" + (totalJobs == 0 ? "" :
						counts.keySet().stream().map(x -> counts.get(x).size() + " " + x.getSimpleName())
						.collect(Collectors.joining(", ", " (", ")"))));

					long remainingMilliseconds = Instant.now().until(lastCheck.plus(checkDuration), ChronoUnit.MILLIS);
					Future<Job> completedFuture = completionService.poll(remainingMilliseconds, TimeUnit.MILLISECONDS);
					lastCheck = Instant.now();
					
					if (completedFuture == null) {
						checkDeadlock();
					} else {
						completeJob(completedFuture);
					}
				} catch (InterruptedException e) {
					// interrupt indicates we are shutting down, but we continue to process remaining jobs
					continue;
				}
			}

			executorService.shutdown();
			logger.info("All jobs completed!");
		}

	}

}

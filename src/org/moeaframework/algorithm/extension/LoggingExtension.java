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
package org.moeaframework.algorithm.extension;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.util.DurationUtils;
import org.moeaframework.util.io.OutputHandler;

/**
 * Extension that logs information about the execution, including the current NFE and elapsed time.  Furthermore, the
 * static methods can be used to write log messages if the algorithm has logging enabled.
 */
public class LoggingExtension implements Extension {
	
	/**
	 * The default logging frequency for status messages.
	 */
	public static final Duration DEFAULT_LOG_FREQUENCY = Duration.ofSeconds(1);
	
	private final Logger logger;
	
	private final StopWatch timer;
	
	private final long logFrequency;
	
	private long lastUpdate;
	
	/**
	 * Constructs a new, default logging extension.
	 */
	public LoggingExtension() {
		this("MOEAFramework");
	}

	/**
	 * Constructs a new logger with the given name.
	 * 
	 * @param name the logger name
	 */
	public LoggingExtension(String name) {
		this(name, DEFAULT_LOG_FREQUENCY);
	}
	
	/**
	 * Constructs a new logger with the given name and settings.
	 * 
	 * @param name the logger name
	 * @param logFrequency the frequency to print log messages
	 */
	public LoggingExtension(String name, Duration logFrequency) {
		this(OutputHandler.getLogger(name), logFrequency);
	}
	
	/**
	 * Constructs a new logger with the given name and settings.
	 * 
	 * @param logger the logger
	 * @param logFrequency the frequency to print log messages
	 */
	public LoggingExtension(Logger logger, Duration logFrequency) {
		super();
		this.logger = logger;
		this.logFrequency = DurationUtils.toMilliseconds(logFrequency);
		
		timer = new StopWatch();
	}
	
	@Override
	public void onStep(Algorithm algorithm) {
		if (timer.isStopped()) {
			timer.start();
		}
		
		long elapsedTime = timer.getTime();
		
		if (elapsedTime - lastUpdate >= logFrequency) {
			logger.log(Level.INFO, "{0} running; NFE: {1}; Elapsed Time: {2}", new Object[] {
	                algorithm.getName(),
	                algorithm.getNumberOfEvaluations(),
	                DurationFormatUtils.formatDuration(elapsedTime, "H:mm:ss", true) });
			lastUpdate = elapsedTime;
		}
	}
	
	@Override
	public void onRegister(Algorithm algorithm) {
		timer.start();
		lastUpdate = timer.getTime();
		logger.log(Level.INFO, "{0} starting", algorithm.getName());
	}
	
	@Override
	public void onTerminate(Algorithm algorithm) {
		timer.stop();
		logger.log(Level.INFO, "{0} finished; NFE: {1}; Elapsed Time: {2}", new Object[] {
                algorithm.getName(),
                algorithm.getNumberOfEvaluations(),
                DurationFormatUtils.formatDuration(timer.getTime(), "H:mm:ss", true) });
	}
	
	/**
	 * Returns the logger used by this extension.
	 * 
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Writes a log message using this extension.
	 * 
	 * @param level the log level
	 * @param message the log message
	 * @param params additional parameters used to format the log message
	 */
	public void log(Level level, String message, Object... params) {
		logger.log(level, message, params);
	}
	
	/**
	 * Writes an informational ({@link Level#INFO}) log message if the algorithm has logging enabled.
	 * 
	 * @param algorithm the algorithm
	 * @param message the log message
	 * @param params additional parameters used to format the log message
	 */
	public static void info(Algorithm algorithm, String message, Object... params) {
		log(algorithm, Level.INFO, message, params);
	}
	
	/**
	 * Writes a warning ({@link Level#WARNING}) log message if the algorithm has logging enabled.
	 * 
	 * @param algorithm the algorithm
	 * @param message the log message
	 * @param params additional parameters used to format the log message
	 */
	public static void warning(Algorithm algorithm, String message, Object... params) {
		log(algorithm, Level.WARNING, message, params);
	}
	
	/**
	 * Writes a severe ({@link Level#SEVERE}) log message if the algorithm has logging enabled.
	 * 
	 * @param algorithm the algorithm
	 * @param message the log message
	 * @param params additional parameters used to format the log message
	 */
	public static void severe(Algorithm algorithm, String message, Object... params) {
		log(algorithm, Level.SEVERE, message, params);
	}
	
	/**
	 * Writes a log message if the algorithm has logging enabled.
	 * 
	 * @param algorithm the algorithm
	 * @param level the log level
	 * @param message the log message
	 * @param params additional parameters used to format the log message
	 */
	public static void log(Algorithm algorithm, Level level, String message, Object... params) {
		LoggingExtension extension = algorithm.getExtensions().get(LoggingExtension.class);
		
		if (extension != null) {
			extension.log(level, message, params);
		}
	}

}

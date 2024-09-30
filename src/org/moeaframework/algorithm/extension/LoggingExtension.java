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
package org.moeaframework.algorithm.extension;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.moeaframework.core.Algorithm;
import org.moeaframework.util.io.OutputHandler;

public class LoggingExtension implements Extension {
	
	public static final long DEFAULT_LOG_FREQUENCY = 1000;
	
	private final Logger logger;
	
	private final StopWatch timer;
	
	private final long logFrequency;
	
	private long lastUpdate;
	
	public LoggingExtension() {
		this("MOEAFramework");
	}
	
	public LoggingExtension(long logFrequency) {
		this("MOEAFramework", logFrequency);
	}
	
	public LoggingExtension(Class<?> type) {
		this(type.getName());
	}
	
	public LoggingExtension(Class<?> type, long logFrequency) {
		this(type.getName(), logFrequency);
	}
	
	public LoggingExtension(String name) {
		this(name, DEFAULT_LOG_FREQUENCY);
	}
	
	public LoggingExtension(String name, long logFrequency) {
		this(OutputHandler.getLogger(name), logFrequency);
	}
	
	public LoggingExtension(Logger logger, long logFrequency) {
		super();
		this.logger = logger;
		this.logFrequency = logFrequency;
		
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
	                algorithm.getClass().getSimpleName(),
	                algorithm.getNumberOfEvaluations(),
	                DurationFormatUtils.formatDuration(elapsedTime, "H:mm:ss", true) });
			lastUpdate = elapsedTime;
		}
	}
	
	@Override
	public void onRegister(Algorithm algorithm) {
		timer.start();
		lastUpdate = timer.getTime();
		logger.log(Level.INFO, "{0} starting", algorithm.getClass().getSimpleName());
	}
	
	@Override
	public void onTerminate(Algorithm algorithm) {
		timer.stop();
		logger.log(Level.INFO, "{0} finished; NFE: {1}; Elapsed Time: {2}", new Object[] {
                algorithm.getClass().getSimpleName(),
                algorithm.getNumberOfEvaluations(),
                DurationFormatUtils.formatDuration(timer.getTime(), "H:mm:ss", true) });
	}

}

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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;

import org.apache.commons.lang3.time.StopWatch;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.util.DurationUtils;

/**
 * Extends an algorithm to record the approximation sets at periodic intervals.
 */
public class RuntimeCollectorExtension extends PeriodicExtension {
	
	/**
	 * The result file writer where the runtime information is stored.
	 */
	private final ResultFileWriter writer;

	/**
	 * The elapsed time.
	 */
	private final StopWatch timer;

	/**
	 * Constructs a new wrapper to collect runtime dynamics.
	 * 
	 * @param writer the result file writer where the runtime information is stored
	 * @param frequency the frequency at which the runtime snapshots are recorded
	 * @param frequencyType the type of frequency
	 */
	public RuntimeCollectorExtension(ResultFileWriter writer, int frequency, FrequencyType frequencyType) {
		super(frequency, frequencyType);
		this.writer = writer;
		
		timer = new StopWatch();
	}
	
	@Override
	public void onRegister(Algorithm algorithm) {
		super.onRegister(algorithm);
		timer.start();
	}

	@Override
	public void doAction(Algorithm algorithm) {
		Duration elapsedTime = timer.getDuration();
		NondominatedPopulation result = algorithm.getResult();

		TypedProperties properties = new TypedProperties();
		properties.setInt(ResultEntry.NFE, algorithm.getNumberOfEvaluations());
		properties.setDouble(ResultEntry.ElapsedTime, DurationUtils.toSeconds(elapsedTime));

		try {
			writer.write(new ResultEntry(result, properties));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
}

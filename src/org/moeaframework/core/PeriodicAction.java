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
package org.moeaframework.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Decorates an algorithm to perform some action periodically throughout the execution of the algorithm.  Note that
 * due to the underlying implementation of the algorithm, the action may be invoked less frequently than requested
 * or not at all.
 */
public abstract class PeriodicAction implements Algorithm {
	
	/**
	 * Defines the type of frequency.
	 */
	public enum FrequencyType {
		
		/**
		 * The frequency measures the number of objective function evaluations.
		 */
		EVALUATIONS,
		
		/**
		 * The frequency measures the number of invocations of {@link Algorithm#step()}.
		 */
		STEPS
		
	}
	
	/**
	 * The underlying algorithm being decorated.
	 */
	protected final Algorithm algorithm;
	
	/**
	 * The frequency that the {@link #doAction()} method is invoked.
	 */
	protected int frequency;
	
	/**
	 * The type of frequency.
	 */
	protected final FrequencyType frequencyType;
	
	/**
	 * The number of invocations of the {@link Algorithm#step()} method.  Only used if the frequency type is
	 * {@code STEPS}.
	 */
	protected int iteration;

	/**
	 * The last invocation {@link #doAction()} was invoked, either as iterations or evaluations depending on the
	 * frequency type.  A value of {@code -1} indicates the run hasn't started yet.
	 */
	protected int lastInvocation;
	
	/**
	 * Decorates an algorithm to perform a periodic action.
	 * 
	 * @param algorithm the algorithm being decorated
	 * @param frequency the frequency the {@link #doAction()} method is invoked
	 * @param frequencyType the type of frequency
	 */
	public PeriodicAction(Algorithm algorithm, int frequency, FrequencyType frequencyType) {
		super();
		this.algorithm = algorithm;
		this.frequency = frequency;
		this.frequencyType = frequencyType;
		
		lastInvocation = -1;
	}
	
	/**
	 * Returns the algorithm that is wrapped by this periodic action.
	 * 
	 * @return the inner algorithm
	 */
	protected Algorithm getAlgorithm() {
		return algorithm;
	}

	@Override
	public Problem getProblem() {
		return algorithm.getProblem();
	}

	@Override
	public NondominatedPopulation getResult() {
		return algorithm.getResult();
	}

	@Override
	public void step() {
		if (lastInvocation < 0) {
			switch (frequencyType) {
				case EVALUATIONS -> lastInvocation = algorithm.getNumberOfEvaluations();
				case STEPS -> lastInvocation = 0;
				default -> throw new IllegalStateException();
			}
		}
		
		algorithm.step();

		switch (frequencyType) {
			case EVALUATIONS -> {
				if ((getNumberOfEvaluations() - lastInvocation) >= frequency) {
					doAction();
					lastInvocation = getNumberOfEvaluations();
				}
			}
			case STEPS -> {
				iteration++;
				
				if ((iteration - lastInvocation) >= frequency) {
					doAction();
					lastInvocation = iteration;
				}
			}
			default -> throw new IllegalStateException();
		}
	}

	@Override
	public void evaluate(Solution solution) {
		algorithm.evaluate(solution);
	}

	@Override
	public int getNumberOfEvaluations() {
		return algorithm.getNumberOfEvaluations();
	}
	
	@Override
	public boolean isTerminated() {
		return algorithm.isTerminated();
	}

	@Override
	public void terminate() {
		algorithm.terminate();
	}
	
	/**
	 * Invoked periodically by this class to perform some function.  This function should not depend on the frequency
	 * that it is invoked, since it is not guaranteed that this method is invoked at any specific frequency and, in
	 * some cases, may not be invoked at all.
	 */
	public abstract void doAction();
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		algorithm.saveState(stream);
		stream.writeInt(frequency);
		stream.writeInt(iteration);
		stream.writeInt(lastInvocation);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		algorithm.loadState(stream);
		frequency = stream.readInt();
		iteration = stream.readInt();
		lastInvocation = stream.readInt();
	}

}

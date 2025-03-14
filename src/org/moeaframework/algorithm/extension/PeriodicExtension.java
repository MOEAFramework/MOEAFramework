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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.core.Stateful;
import org.moeaframework.util.validate.Validate;

/**
 * An extension that performs an action at a given {@link Frequency}.
 */
public abstract class PeriodicExtension implements Extension, Stateful {
	
	/**
	 * The frequency that the {@link #doAction(Algorithm)} method is invoked.
	 */
	protected Frequency frequency;
	
	/**
	 * The number of invocations of the {@link Algorithm#step()} method.  Only used if the frequency type is
	 * {@link Frequency.Type#ITERATIONS}.
	 */
	protected int iteration;

	/**
	 * The last invocation {@link #doAction(Algorithm)} was invoked, either as iterations or evaluations depending on
	 * the frequency type.
	 */
	protected int lastInvocation;
	
	/**
	 * Extension that performs an action at a fixed frequency.
	 * 
	 * @param frequency the frequency the {@link #doAction(Algorithm)} method is invoked
	 */
	public PeriodicExtension(Frequency frequency) {
		super();
		this.frequency = frequency;
	}

	/**
	 * The action that is called by this extension.
	 * 
	 * @param algorithm the algorithm associated with this extension
	 */
	public abstract void doAction(Algorithm algorithm);
	
	@Override
	public void onRegister(Algorithm algorithm) {
		switch (frequency.getType()) {
			case EVALUATIONS -> lastInvocation = algorithm.getNumberOfEvaluations();
			case ITERATIONS -> lastInvocation = 0;
			default -> Validate.that("frequency.getType()", frequency.getType()).failUnsupportedOption();
		}
	}
	
	@Override
	public void onStep(Algorithm algorithm) {
		switch (frequency.getType()) {
			case EVALUATIONS -> {
				if ((algorithm.getNumberOfEvaluations() - lastInvocation) >= frequency.getValue()) {
					doAction(algorithm);
					lastInvocation = algorithm.getNumberOfEvaluations();
				}
			}
			case ITERATIONS -> {
				iteration++;
				
				if ((iteration - lastInvocation) >= frequency.getValue()) {
					doAction(algorithm);
					lastInvocation = iteration;
				}
			}
			default -> Validate.that("frequency.getType()", frequency.getType()).failUnsupportedOption();
		}
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		Stateful.writeTypeSafety(stream, this);
		stream.writeInt(frequency.getValue());
		stream.writeObject(frequency.getType());
		stream.writeInt(iteration);
		stream.writeInt(lastInvocation);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		Stateful.checkTypeSafety(stream, this);
		frequency = new Frequency(stream.readInt(), (Frequency.Type)stream.readObject());
		iteration = stream.readInt();
		lastInvocation = stream.readInt();
	}

}

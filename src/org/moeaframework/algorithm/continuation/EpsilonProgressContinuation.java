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
package org.moeaframework.algorithm.continuation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Variation;

/**
 * Extends {@link AdaptiveTimeContinuation} to trigger restarts using &epsilon;-progress.  &epsilon;-progress measures
 * search progress by counting the number of significant improvements, as measured by the number of unoccupied
 * &epsilon;-boxes filled during a fixed time window.  
 * <p>
 * References:
 * <ol>
 *   <li>Hadka, D. and Reed, P.  "Borg: An Auto-Adaptive Many-Objective Evolutionary Computing Framework."
 *       Evolutionary Computation, 21(2):231-259, 2013.
 * </ol>
 */
public class EpsilonProgressContinuation extends AdaptiveTimeContinuation {

	/**
	 * The number of &epsilon;-progress improvements since the last invocation of {@code check}.
	 */
	private int improvementsAtLastCheck;

	/**
	 * Decorates the specified algorithm with &epsilon;-progress triggered time continuation.
	 * 
	 * @param algorithm the algorithm being decorated
	 * @param windowSize the number of iterations between invocations of {@code check}
	 * @param maxWindowSize the maximum number of iterations allowed since the last restart before forcing a restart
	 * @param injectionRate the injection rate
	 * @param minimumPopulationSize the minimum size of the population
	 * @param maximumPopulationSize the maximum size of the population
	 * @param selection the selection operator for selecting solutions from the archive during a restart
	 * @param variation the variation operator for mutating solutions selected from the archive during a restart
	 */
	public EpsilonProgressContinuation(EpsilonBoxEvolutionaryAlgorithm algorithm, int windowSize, int maxWindowSize,
			double injectionRate, int minimumPopulationSize, int maximumPopulationSize, Selection selection,
			Variation variation) {
		super(algorithm, windowSize, maxWindowSize, injectionRate, minimumPopulationSize, maximumPopulationSize,
				selection, variation);
	}

	@Override
	public EpsilonBoxDominanceArchive getArchive() {
		return (EpsilonBoxDominanceArchive)super.getArchive();
	}

	@Override
	protected RestartType check() {
		RestartType superType = super.check();

		if (superType.equals(RestartType.NONE)) {
			if (getArchive().getNumberOfImprovements() <= improvementsAtLastCheck) {
				superType = RestartType.HARD;
			}
		}

		improvementsAtLastCheck = getArchive().getNumberOfImprovements();

		return superType;
	}

	@Override
	protected void restart(RestartType type) {
		super.restart(type);
		improvementsAtLastCheck = getArchive().getNumberOfImprovements();
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);
		stream.writeInt(improvementsAtLastCheck);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		improvementsAtLastCheck = stream.readInt();
	}

}

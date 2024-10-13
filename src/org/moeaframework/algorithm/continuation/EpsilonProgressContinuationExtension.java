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

import org.moeaframework.algorithm.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.operator.Variation;
import org.moeaframework.core.selection.Selection;
import org.moeaframework.util.validate.Validate;

/**
 * Extends {@link AdaptiveTimeContinuationExtension} to trigger restarts using &epsilon;-progress.  &epsilon;-progress
 * measures search progress by counting the number of significant improvements, as measured by the number of unoccupied
 * &epsilon;-boxes filled during a fixed time window.  
 * <p>
 * References:
 * <ol>
 *   <li>Hadka, D. and Reed, P.  "Borg: An Auto-Adaptive Many-Objective Evolutionary Computing Framework."
 *       Evolutionary Computation, 21(2):231-259, 2013.
 * </ol>
 */
public class EpsilonProgressContinuationExtension extends AdaptiveTimeContinuationExtension {

	/**
	 * The number of &epsilon;-progress improvements since the last invocation of {@code check}.
	 */
	private int improvementsAtLastCheck;
	
	/**
	 * Creates the &epsilon;-progress triggered time continuation extension with default settings.
	 */
	public EpsilonProgressContinuationExtension() {
		super();
	}

	/**
	 * Creates the &epsilon;-progress triggered time continuation extension.
	 * 
	 * @param windowSize the number of iterations between invocations of {@code check}
	 * @param maxWindowSize the maximum number of iterations allowed since the last restart before forcing a restart
	 * @param injectionRate the injection rate
	 * @param minimumPopulationSize the minimum size of the population
	 * @param maximumPopulationSize the maximum size of the population
	 * @param selection the selection operator for selecting solutions from the archive during a restart
	 * @param variation the variation operator for mutating solutions selected from the archive during a restart
	 */
	public EpsilonProgressContinuationExtension(int windowSize, int maxWindowSize, double injectionRate,
			int minimumPopulationSize, int maximumPopulationSize, Selection selection, Variation variation) {
		super(windowSize, maxWindowSize, injectionRate, minimumPopulationSize, maximumPopulationSize,
				selection, variation);
	}

	@Override
	public void onRegister(Algorithm algorithm) {
		super.onRegister(algorithm);
		
		EpsilonBoxEvolutionaryAlgorithm ea = Validate.that("algorithm", algorithm).isA(EpsilonBoxEvolutionaryAlgorithm.class);
		Validate.that("archive", ea.getArchive()).isNotNull();
	}

	@Override
	protected RestartType check(Algorithm algorithm) {		
		RestartType superType = super.check(algorithm);
		
		EpsilonBoxEvolutionaryAlgorithm ea = (EpsilonBoxEvolutionaryAlgorithm)algorithm;

		if (superType.equals(RestartType.NONE)) {
			if (ea.getArchive().getNumberOfImprovements() <= improvementsAtLastCheck) {
				superType = RestartType.HARD;
			}
		}

		improvementsAtLastCheck = ea.getArchive().getNumberOfImprovements();

		return superType;
	}

	@Override
	protected void restart(Algorithm algorithm, RestartType type) {		
		super.restart(algorithm, type);
		
		EpsilonBoxEvolutionaryAlgorithm ea = (EpsilonBoxEvolutionaryAlgorithm)algorithm;
		improvementsAtLastCheck = ea.getArchive().getNumberOfImprovements();
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

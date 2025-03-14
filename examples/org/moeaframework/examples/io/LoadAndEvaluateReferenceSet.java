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
package org.moeaframework.examples.io;

import java.io.IOException;

import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

/**
 * Demonstrates loading a reference set and computing the hypervolume.
 */
public class LoadAndEvaluateReferenceSet {

	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(2);
		NondominatedPopulation referenceSet = NondominatedPopulation.load("pf/DTLZ2.2D.pf");
		
		Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
		System.out.println("Reference Set Hypervolume: " + hypervolume.evaluate(referenceSet));
	}

}

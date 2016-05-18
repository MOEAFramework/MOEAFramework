/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.core.termination;

import java.io.NotSerializableException;
import java.io.Serializable;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public class MockAlgorithm implements Algorithm {
	
	private int evaluations;

	@Override
	public Problem getProblem() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NondominatedPopulation getResult() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void step() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void evaluate(Solution solution) {
		throw new UnsupportedOperationException();
	}
	
	public void setNumberOfEvaluations(int evaluations) {
		this.evaluations = evaluations;
	}

	@Override
	public int getNumberOfEvaluations() {
		return evaluations;
	}

	@Override
	public boolean isTerminated() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void terminate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable getState() throws NotSerializableException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setState(Object state) throws NotSerializableException {
		throw new UnsupportedOperationException();
	}

}

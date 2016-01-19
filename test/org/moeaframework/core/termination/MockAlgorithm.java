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

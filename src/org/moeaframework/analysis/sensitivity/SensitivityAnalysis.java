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
package org.moeaframework.analysis.sensitivity;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sample.SampledResults;
import org.moeaframework.analysis.sample.Samples;

/**
 * Interface for sensitivity analysis methods.
 * 
 * @param <T> the type defining what results are produced by this analysis
 */
public interface SensitivityAnalysis<T extends SensitivityResult> {
	
	/**
	 * Returns the parameter set associated with this sensitivity analysis.
	 * 
	 * @return the parameter set
	 */
	public ParameterSet getParameterSet();
	
	/**
	 * Generates and returns the samples required by this sensitivity analysis method.  Typically, but not always,
	 * these samples are random or pseudo-random.
	 * 
	 * @return the samples
	 */
	public Samples generateSamples();
	
	/**
	 * Evaluates the model responses associated with each sample, returning the sensitivity results.  The order of
	 * responses must match the order of the generated samples!
	 * 
	 * @param responses the model responses
	 * @return the sensitivity results
	 */
	public T evaluate(double[] responses);
	
	/**
	 * Extracts the {@link SampledResults} values and calls {@link #evaluate(double[])}.
	 * 
	 * @param results the sampled results
	 * @return the sensitivity results
	 */
	public default T evaluate(SampledResults<Double> results) {
		return evaluate(results.stream().mapToDouble(Pair::getValue).toArray());
	}
	
}

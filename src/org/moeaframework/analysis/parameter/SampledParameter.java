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
package org.moeaframework.analysis.parameter;

import org.moeaframework.analysis.sample.Sample;

/**
 * Interface for parameters that can be sampled randomly or by some sequence.
 * 
 * @param <T> the type of the parameter
 */
public interface SampledParameter<T> extends Parameter<T> {
	
	/**
	 * Samples this parameter and assigns the value to the sample.  The scale is a value between {@code 0.0} and
	 * {@code 1.0}, typically supplied by a {@link org.moeaframework.util.sequence.Sequence}, used to generate the
	 * sampled value.
	 * <p>
	 * When converting the scale to the parameter value, implementations are expected to provide equal weighting to
	 * each possible value, so that a value does not appear more or less often than any other value.
	 * 
	 * @param sample the sample
	 * @param scale value between {@code 0.0} and {@code 1.0} used to generate the sample
	 */
	public void sample(Sample sample, double scale);

}

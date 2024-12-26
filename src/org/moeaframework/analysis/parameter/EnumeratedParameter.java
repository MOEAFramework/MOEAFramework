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
package org.moeaframework.analysis.parameter;

import java.util.List;

import org.moeaframework.analysis.sample.Sample;

/**
 * Interface for enumerated parameters that can either enumerate all possible values or produce a sampling.
 *
 * @param <T> the type of the parameter
 */
public interface EnumeratedParameter<T> extends Parameter<T>, SampledParameter<T> {
	
	/**
	 * Returns all possible values produced by this enumeration.
	 * 
	 * @return the values
	 */
	public List<T> values();
	
	/**
	 * Enumerates the parameters by creating a "cross join" with the existing samples.  If given {@code N} samples as
	 * input, the result will contain {@code N * values().size()} samples.
	 * 
	 * @param samples the input samples
	 * @return the enumerated samples including this parameter
	 */
	public List<Sample> enumerate(List<Sample> samples);

}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.sequence.Sequence;
import org.moeaframework.util.validate.Validate;

public class SampledParameterSet extends ParameterSet<Parameter<?>> {
		
	public SampledParameterSet() {
		super();
	}
	
	public SampledParameterSet(Parameter<?>... parameters) {
		super(parameters);
	}
	
	public SampledParameterSet(Collection<Parameter<?>> parameters) {
		super(parameters);
	}
	
	/**
	 * Throws an exception if this set contains any parameter that is {@link EnumeratedParameter}.  However,
	 * {@link Constant} is allowed as it does not change the number of samples.
	 */
	public void throwIfEnumerated() {
		for (Parameter<?> parameter : this) {
			if (parameter instanceof EnumeratedParameter<?> && !(parameter instanceof Constant<?>)) {
				throw new FrameworkException("sampled parameter set contains enumerated parameter '" +
						parameter.getName() + "'");
			}
		}
	}

	public Samples generate(int numberOfSamples, Sequence sequence) {
		// Identify which parameters are sampled vs enumerated.
		List<SampledParameter<?>> sampledParameters = new ArrayList<>();
		List<EnumeratedParameter<?>> enumeratedParameters = new ArrayList<>();
		
		for (Parameter<?> parameter : parameters) {
			if (parameter instanceof SampledParameter sampledParameter) {
				sampledParameters.add(sampledParameter);
			} else if (parameter instanceof EnumeratedParameter enumeratedParameter) {
				enumeratedParameters.add(enumeratedParameter);
			} else {
				Validate.that("parameter", parameter).fails("Unsupported parameter type " +
						parameter.getClass().getName());
			}
		}
		
		List<Sample> result = new ArrayList<>();
		
		// Expand the sampled parameters using the provided sequence generator.
		if (sampledParameters.size() > 0) {
			double[][] sequences = sequence.generate(numberOfSamples, sampledParameters.size());
			
			for (double[] seq : sequences) {
				Sample sample = new Sample();
				
				for (int i = 0; i < sampledParameters.size(); i++) {
					sampledParameters.get(i).apply(sample, seq[i]);
				}
				
				result.add(sample);
			}
		} else {
			result.add(new Sample());
		}
		
		// Expand the enumerated parameters.
		for (int i = 0; i < enumeratedParameters.size(); i++) {
			result = enumeratedParameters.get(i).enumerate(result);
		}

		return new Samples(this, result);
	}

}

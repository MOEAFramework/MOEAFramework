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

public class EnumeratedParameterSet extends ParameterSet<EnumeratedParameter<?>> {
	
	public EnumeratedParameterSet() {
		super();
	}
	
	public EnumeratedParameterSet(EnumeratedParameter<?>... parameters) {
		super(parameters);
	}
	
	public EnumeratedParameterSet(Collection<EnumeratedParameter<?>> parameters) {
		super(parameters);
	}

	public Samples generate() {
		List<Sample> result = new ArrayList<>();
		result.add(new Sample());
		
		for (EnumeratedParameter<?> parameter : parameters) {
			result = parameter.enumerate(result);
		}

		return new Samples(this, result);
	}

}

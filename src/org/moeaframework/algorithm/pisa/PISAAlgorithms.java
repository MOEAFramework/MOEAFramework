/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.algorithm.pisa;

import java.io.IOException;
import java.util.Properties;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Variation;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProviderLookupException;
import org.moeaframework.core.spi.ProviderNotFoundException;

/**
 * Algorithm provider for PISA selectors. In order to make a PISA selector
 * available for this provider, two steps are required. First, the PISA selector
 * must be downloaded and, if necessary, compiled. Second, the configuration
 * file (typically {@code global.properties}) must be updated with the new
 * PISA selector. As an example, for the HypE selector, add the selector name,
 * {@code hype}, to the list of PISA algorithms:
 * <pre>
 *   org.moeaframework.util.pisa.algorithms = hype, spea2, nsga2
 * </pre>
 * For each algorithm, define its configuration options below.  For the example
 * of {@code hype}, specify the following:
 * <ol>
 *   <li>The executable to run:
 *     <pre>
 *        org.moeaframework.algorithm.pisa.hype.command = ./path/to/hype.exe
 *     </pre>
 *   <li>The list of parameters:
 *     <pre>
 *        org.moeaframework.algorithm.pisa.hype.parameters = seed, tournament, mating, bound, nrOfSamples
 *     </pre>
 *     The order typically matters, so ensure the parameters are listed in the
 *     same order as expected by the executable.
 *   <li>For each parameter, specify its default value:
 *     <pre>
 *        org.moeaframework.algorithm.pisa.hype.parameter.tournament = 5
 *        org.moeaframework.algorithm.pisa.hype.parameter.mating = 1
 *        ...
 *     </pre>
 *     It is not necessary to give a default for the seed parameter as it is
 *     set automatically by the MOEA Framework.
 * </ol>
 * Note: Prior to version 1.14, the MOEA Framework only accepted a static
 * version of the algorithm parameters using the option:
 * <pre>
 *   org.moeaframework.algorithm.pisa.hype.configuration = ./path/to/hype_params.txt
 * </pre>
 * This is still accepted, but would mean the MOEA Framework is unable to
 * change the algorithm parameters.  Once completed, the PISA selector can be
 * used with:
 * <pre>
 *   Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("hype", properties, problem);
 * </pre>
 */
public class PISAAlgorithms extends AlgorithmProvider {

	/**
	 * Constructs an algorithm provider for PISA selectors.
	 */
	public PISAAlgorithms() {
		super();
	}
	
	/**
	 * Returns the case-sensitive version of the PISA algorithm name.
	 * 
	 * @param name the case-insensitive name
	 * @return the case-sensitive name
	 */
	protected String getCaseSensitiveSelectorName(String name) {
		for (String selector : Settings.getPISAAlgorithms()) {
			if (selector.equalsIgnoreCase(name)) {
				return selector;
			}
		}
		
		return null;
	}

	@Override
	public Algorithm getAlgorithm(String name, Properties properties,
			Problem problem) {
		//lookup the case-sensitive version of the PISA algorithm name to 
		//generate the correct property keys
		name = getCaseSensitiveSelectorName(name);

		if (name != null) {
			if (problem.getNumberOfConstraints() > 0) {
				throw new ProviderNotFoundException(name, 
						new ProviderLookupException("constraints not supported"));
			}
			
			try {
				Variation variation = OperatorFactory.getInstance()
						.getVariation(null, properties, problem);
				
				return new PISAAlgorithm(name, problem, variation, properties);
			} catch (IOException e) {
				throw new ProviderNotFoundException(name, e);
			}
		} else {
			return null;
		}
	}
	
}

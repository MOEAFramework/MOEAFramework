/* Copyright 2009-2011 David Hadka
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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Variation;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.util.TypedProperties;

/**
 * Algorithm provider for PISA selectors. In order to make a PISA selector
 * available for this provider, two steps are required. First, the PISA selector
 * must be downloaded and, if necessary, compiled. Second, three entries must be
 * added to the configuration file (typically {@code global.properties}). As an
 * example, for the HypE selector, add the selector name, {@code hype}, to the
 * list of PISA algorithms:
 * <pre>
 *   org.moeaframework.util.pisa.algorithms = hype, spea2, nsga2
 * </pre>
 * Next, the executable and configuration file must be listed:
 * <pre>
 *   org.moeaframework.util.pisa.hype.command = ./pisa/hype_win/hype.exe
 *   org.moeaframework.util.pisa.hype.configuration = ./pisa/hype_win/hype_params.txt
 * </pre>
 * Once completed, the PISA selector can be used with:
 * <pre>
 *   Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm("hype", properties, problem);
 * </pre>
 * Note how the keys contain the name {@code hype}. The name {@code hype} is
 * user-defined, but must be kept consistent as shown above.
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
		TypedProperties typedProperties = new TypedProperties(properties);
		
		//lookup the case-sensitive version of the PISA algorithm name to 
		//generate the correct property keys
		name = getCaseSensitiveSelectorName(name);

		if (name != null) {
			if (problem.getNumberOfConstraints() > 0) {
				throw new ProviderNotFoundException(name, 
						new FrameworkException("constraints not supported"));
			}
			
			try {
				String command = Settings.getPISACommand(name);
				String configuration = Settings.getPISAConfiguration(name);
				int pollRate = Settings.getPISAPollRate();
				
				//This is slightly unsafe since the actual files used by 
				//PISA add the arc, cfg, ini, sel and sta extensions.  This
				//dependency on files for communication is an unfortunate part
				//of PISA's design.
				File prefix = File.createTempFile("pisa", "");
				
				if (command == null) {
					throw new IllegalArgumentException("missing command");
				}
				
				if (configuration == null) {
					throw new IllegalArgumentException("missing configuration");
				}
				
				ProcessBuilder builder = new ProcessBuilder(command, 
						configuration, prefix.getCanonicalPath(), 
						Double.toString(pollRate/(double)1000));
				
				Variation variation = OperatorFactory.getInstance()
						.getVariation(null, properties, problem);

				int alpha = (int)typedProperties.getDouble("populationSize", 100);
				
				while (alpha % variation.getArity() != 0) {
					alpha++;
				}
				
				int mu = (int)typedProperties.getDouble("mu", alpha);
				int lambda = (int)typedProperties.getDouble("lambda", alpha);
				
				return new PISAAlgorithm(prefix.getCanonicalPath(), builder, 
						problem, variation, alpha, mu, lambda);
			} catch (IOException e) {
				throw new ProviderNotFoundException(name, e);
			}
		} else {
			return null;
		}
	}

	
}

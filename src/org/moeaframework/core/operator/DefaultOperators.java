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
package org.moeaframework.core.operator;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.moeaframework.algorithm.single.SelfAdaptiveNormalVariation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.operator.binary.BitFlip;
import org.moeaframework.core.operator.binary.HUX;
import org.moeaframework.core.operator.grammar.GrammarCrossover;
import org.moeaframework.core.operator.grammar.GrammarMutation;
import org.moeaframework.core.operator.permutation.Insertion;
import org.moeaframework.core.operator.permutation.PMX;
import org.moeaframework.core.operator.permutation.Swap;
import org.moeaframework.core.operator.program.PointMutation;
import org.moeaframework.core.operator.program.SubtreeCrossover;
import org.moeaframework.core.operator.real.AdaptiveMetropolis;
import org.moeaframework.core.operator.real.DifferentialEvolutionVariation;
import org.moeaframework.core.operator.real.PCX;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.operator.real.SPX;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.operator.real.UNDX;
import org.moeaframework.core.operator.subset.Add;
import org.moeaframework.core.operator.subset.Remove;
import org.moeaframework.core.operator.subset.Replace;
import org.moeaframework.core.operator.subset.SSX;
import org.moeaframework.core.spi.RegisteredOperatorProvider;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.Program;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Subset;
import org.moeaframework.util.TypedProperties;

/**
 * Default provider of operators.
 */
public class DefaultOperators extends RegisteredOperatorProvider {
	
	/**
	 * Constructs a provider for the default operators.
	 */
	public DefaultOperators() {
		super();
		
		setMutationHint(RealVariable.class, "pm");
		setMutationHint(BinaryVariable.class, "bf");
		setMutationHint(Permutation.class, "insertion+swap");
		setMutationHint(Grammar.class, "gm");
		setMutationHint(Program.class, "ptm");
		setMutationHint(Subset.class, "replace+add+remove");
		
		setVariationHint(RealVariable.class, "sbx+pm");
		setVariationHint(BinaryVariable.class, "hux+bf");
		setVariationHint(Permutation.class, "pmx+insertion+swap");
		setVariationHint(Grammar.class, "gx+gm");
		setVariationHint(Program.class, "stx+ptm");
		setVariationHint(Subset.class, "ssx+replace+add+remove");
		
		// real
		registerConfigurable("sbx", SBX::new);
		registerConfigurable("de", DifferentialEvolutionVariation::new);
		registerConfigurable("pcx", PCX::new);
		registerConfigurable("spx", SPX::new);
		registerConfigurable("undx", UNDX::new);
		registerConfigurable("am", AdaptiveMetropolis::new);
		registerConfigurable("selfadaptive", SelfAdaptiveNormalVariation::new);

		// these are two special cases where we have historically set the default rate to 1/N
		register("pm", (properties, problem) -> new PM(
				properties.getDouble("pm.rate", 1.0 / problem.getNumberOfVariables()), 
				properties.getDouble("pm.distributionIndex", 20.0)));
		
		register("um", (properties, problem) -> new UM(
					properties.getDouble("um.rate", 1.0 / problem.getNumberOfVariables())));
				
		// binary
		registerConfigurable("hux", HUX::new);
		registerConfigurable("bf", BitFlip::new);
		
		// permutation
		registerConfigurable("pmx", PMX::new);
		registerConfigurable("insertion", Insertion::new);
		registerConfigurable("swap", Swap::new);
		
		// generic (any type)
		registerConfigurable("1x", OnePointCrossover::new);
		registerConfigurable("2x", TwoPointCrossover::new);
		registerConfigurable("ux", UniformCrossover::new);
		
		// grammar
		registerConfigurable("gx", GrammarCrossover::new);
		registerConfigurable("gm", GrammarMutation::new);
		
		// program
		registerConfigurable("ptm", PointMutation::new);
		registerConfigurable("stx", SubtreeCrossover::new);
		registerConfigurable("bx", () -> {
			throw new ConfigurationException("use 'stx' instead of 'bx' for subtree crossover");
		});
		
		// subset
		registerConfigurable("replace", Replace::new);
		registerConfigurable("add", Add::new);
		registerConfigurable("remove", Remove::new);
		registerConfigurable("ssx", SSX::new);
	}
	
	private <T extends Variation & Configurable> void registerConfigurable(String name, Supplier<T> constructor) {
		BiFunction<TypedProperties, Problem, Variation> callback = (properties, problem) -> {
			T operator = constructor.get();
			operator.applyConfiguration(properties);
			return operator;
		};
		
		super.register(name, callback);
	}

}

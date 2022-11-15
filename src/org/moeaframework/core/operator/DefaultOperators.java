/* Copyright 2009-2022 David Hadka
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

/**
 * Default provider of operators.
 */
public class DefaultOperators extends RegisteredOperatorProvider {
	
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
		setVariationHint(Program.class, "bx+ptm");
		setVariationHint(Subset.class, "ssx+replace+add+remove");
		
		register("sbx", (properties, problem) -> new SBX(
				properties.getDouble("sbx.rate", 1.0), 
				properties.getDouble("sbx.distributionIndex", 15.0),
				properties.getBoolean("sbx.swap", true),
				properties.getBoolean("sbx.symmetric", false)));
		
		register("pm", (properties, problem) -> new PM(
				properties.getDouble("pm.rate", 1.0 / problem.getNumberOfVariables()), 
				properties.getDouble("pm.distributionIndex", 20.0)));
		
		register("de", (properties, problem) -> new DifferentialEvolutionVariation(
				properties.getDouble("de.crossoverRate", 0.1), 
				properties.getDouble("de.stepSize", 0.5)));
		
		register("pcx", (properties, problem) -> new PCX(
				(int)properties.getDouble("pcx.parents", 10),
				(int)properties.getDouble("pcx.offspring", 2), 
				properties.getDouble("pcx.eta", 0.1), 
				properties.getDouble("pcx.zeta", 0.1)));
		
		register("spx", (properties, problem) -> new SPX(
				(int)properties.getDouble("spx.parents", 10),
				(int)properties.getDouble("spx.offspring", 2),
				properties.getDouble("spx.epsilon", 3)));
		
		register("undx", (properties, problem) -> new UNDX(
				(int)properties.getDouble("undx.parents", 10),
				(int)properties.getDouble("undx.offspring", 2), 
				properties.getDouble("undx.zeta", 0.5), 
				properties.getDouble("undx.eta", 0.35)));
		
		register("um", (properties, problem) -> new UM(
					properties.getDouble("um.rate", 1.0 / problem.getNumberOfVariables())));
		
		register("am", (properties, problem) -> new AdaptiveMetropolis(
				(int)properties.getDouble("am.parents", 10),
				(int)properties.getDouble("am.offspring", 2), 
				properties.getDouble("am.coefficient", 2.4)));
		
		register("hux", (properties, problem) -> new HUX(
				properties.getDouble("hux.rate", 1.0)));
		
		register("bf", (properties, problem) -> new BitFlip(
				properties.getDouble("bf.rate", 0.01)));
		
		register("pmx", (properties, problem) -> new PMX(
				properties.getDouble("pmx.rate", 1.0)));
		
		register("insertion", (properties, problem) -> new Insertion(
				properties.getDouble("insertion.rate", 0.3)));
		
		register("swap", (properties, problem) -> new Swap(
				properties.getDouble("swap.rate", 0.3)));
		
		register("1x", (properties, problem) -> new OnePointCrossover(
				properties.getDouble("1x.rate", 1.0)));
		
		register("2x", (properties, problem) -> new TwoPointCrossover(
				properties.getDouble("2x.rate", 1.0)));
		
		register("ux", (properties, problem) -> new UniformCrossover(
				properties.getDouble("ux.rate", 1.0)));
		
		register("gx", (properties, problem) -> new GrammarCrossover(
				properties.getDouble("gx.rate", 1.0)));
		
		register("gm", (properties, problem) -> new GrammarMutation(
				properties.getDouble("gm.rate", 1.0)));
		
		register("ptm", (properties, problem) -> new PointMutation(
				properties.getDouble("ptm.rate", 0.01)));
		
		register("bx", (properties, problem) -> new SubtreeCrossover(
				properties.getDouble("bx.rate", 0.9)));
		
		register("replace", (properties, problem) -> new Replace(
				properties.getDouble("replace.rate", 0.9)));
		
		register("add", (properties, problem) -> new Add(
				properties.getDouble("add.rate", 0.1)));
		
		register("remove", (properties, problem) -> new Remove(
				properties.getDouble("remove.rate", 0.1)));
		
		register("ssx", (properties, problem) -> new SSX(
				properties.getDouble("ssx.rate", 0.3)));
	}

}

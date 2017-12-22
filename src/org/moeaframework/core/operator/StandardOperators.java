/* Copyright 2009-2016 David Hadka
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

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
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
import org.moeaframework.core.spi.OperatorProvider;
import org.moeaframework.core.spi.ProviderLookupException;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.Program;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Subset;
import org.moeaframework.util.TypedProperties;

/**
 * Default provider of operators.  The name and properties columns show the
 * values accepted by {@link #getVariation(String, Properties, Problem)}.
 * <p>
 * <table width="100%" border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="15%" align="left">Operator</th>
 *     <th width="10%" align="left">Type</th>
 *     <th width="10%" align="left">Name</th>
 *     <th width="65%" align="left">Properties</th>
 *   </tr>
 *   <tr>
 *     <td>{@link SBX}</td>
 *     <td>Real</td>
 *     <td>{@code sbx}</td>
 *     <td>{@code sbx.rate, sbx.distributionIndex}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link PM}</td>
 *     <td>Real</td>
 *     <td>{@code pm}</td>
 *     <td>{@code pm.rate, pm.distributionIndex}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UM}</td>
 *     <td>Real</td>
 *     <td>{@code um}</td>
 *     <td>{@code um.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link DifferentialEvolutionVariation}</td>
 *     <td>Real</td>
 *     <td>{@code de}</td>
 *     <td>{@code de.crossoverRate, de.stepSize}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link PCX}</td>
 *     <td>Real</td>
 *     <td>{@code pcx}</td>
 *     <td>{@code pcx.parents, pcx.offspring, pcx.eta, pcx.zeta}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link SPX}</td>
 *     <td>Real</td>
 *     <td>{@code spx}</td>
 *     <td>{@code spx.parents, spx.offspring, spx.epsilon}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UNDX}</td>
 *     <td>Real</td>
 *     <td>{@code undx}</td>
 *     <td>{@code undx.parents, undx.offspring, undx.eta, undx.zeta}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link AdaptiveMetropolis}</td>
 *     <td>Real</td>
 *     <td>{@code am}</td>
 *     <td>{@code am.parents, am.offspring, am.coefficient}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link HUX}</td>
 *     <td>Binary</td>
 *     <td>{@code hux}</td>
 *     <td>{@code hux.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link BitFlip}</td>
 *     <td>Binary</td>
 *     <td>{@code bf}</td>
 *     <td>{@code bf.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link PMX}</td>
 *     <td>Permutation</td>
 *     <td>{@code pmx}</td>
 *     <td>{@code pmx.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Insertion}</td>
 *     <td>Permutation</td>
 *     <td>{@code insertion}</td>
 *     <td>{@code insertion.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Swap}</td>
 *     <td>Permutation</td>
 *     <td>{@code swap}</td>
 *     <td>{@code swap.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link SSX}</td>
 *     <td>Subset</td>
 *     <td>{@code ssx}</td>
 *     <td>{@code ssx.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Replace}</td>
 *     <td>Subset</td>
 *     <td>{@code replace}</td>
 *     <td>{@code replace.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Add}</td>
 *     <td>Subset</td>
 *     <td>{@code add}</td>
 *     <td>{@code add.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link Remove}</td>
 *     <td>Subset</td>
 *     <td>{@code remove}</td>
 *     <td>{@code remove.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link GrammarCrossover}</td>
 *     <td>Grammar</td>
 *     <td>{@code gx}</td>
 *     <td>{@code gx.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link GrammarMutation}</td>
 *     <td>Grammar</td>
 *     <td>{@code gm}</td>
 *     <td>{@code gm.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link SubtreeCrossover}</td>
 *     <td>Program</td>
 *     <td>{@code bx}</td>
 *     <td>{@code bx.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link PointMutation}</td>
 *     <td>Program</td>
 *     <td>{@code ptm}</td>
 *     <td>{@code ptm.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link OnePointCrossover}</td>
 *     <td>Any</td>
 *     <td>{@code 1x}</td>
 *     <td>{@code 1x.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link TwoPointCrossover}</td>
 *     <td>Any</td>
 *     <td>{@code 2x}</td>
 *     <td>{@code 2x.rate}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UniformCrossover}</td>
 *     <td>Any</td>
 *     <td>{@code ux}</td>
 *     <td>{@code ux.rate}</td>
 *   </tr>
 * </table>
 */
public class StandardOperators extends OperatorProvider {
	
	@Override
	public String getMutationHint(Problem problem) {
		Set<Class<?>> types = new HashSet<Class<?>>();
		Solution solution = problem.newSolution();
		
		for (int i=0; i<solution.getNumberOfVariables(); i++) {
			Variable variable = solution.getVariable(i);
			
			if (variable == null) {
				throw new ProviderLookupException("variable is null");
			} else {
				types.add(variable.getClass());
			}
		}
		
		if (types.isEmpty() || (types.size() > 1)) {
			return null;
		}
		
		Class<?> type = types.iterator().next();
		
		if (RealVariable.class.isAssignableFrom(type)) {
			return "pm";
		} else if (BinaryVariable.class.isAssignableFrom(type)) {
			return "bf";
		} else if (Permutation.class.isAssignableFrom(type)) {
			return "insertion+swap";
		} else if (Grammar.class.isAssignableFrom(type)) {
			return "gm";
		} else if (Program.class.isAssignableFrom(type)) {
			return "ptm";
		} else if (Subset.class.isAssignableFrom(type)) {
			return "replace+add+remove";
		} else {
			return null;
		}
	}
	
	@Override
	public String getVariationHint(Problem problem) {
		Set<Class<?>> types = new HashSet<Class<?>>();
		Solution solution = problem.newSolution();
		
		for (int i=0; i<solution.getNumberOfVariables(); i++) {
			Variable variable = solution.getVariable(i);
			
			if (variable == null) {
				throw new ProviderLookupException("variable is null");
			} else {
				types.add(variable.getClass());
			}
		}
		
		if (types.isEmpty() || (types.size() > 1)) {
			return null;
		}
		
		Class<?> type = types.iterator().next();
		
		if (RealVariable.class.isAssignableFrom(type)) {
			return "sbx+pm";
		} else if (BinaryVariable.class.isAssignableFrom(type)) {
			return "hux+bf";
		} else if (Permutation.class.isAssignableFrom(type)) {
			return "pmx+insertion+swap";
		} else if (Grammar.class.isAssignableFrom(type)) {
			return "gx+gm";
		} else if (Program.class.isAssignableFrom(type)) {
			return "bx+ptm";
		} else if (Subset.class.isAssignableFrom(type)) {
			return "ssx+replace+add+remove";
		} else {
			return null;
		}
	}

	@Override
	public Variation getVariation(String name, Properties properties,
			Problem problem) {
		TypedProperties typedProperties = new TypedProperties(properties);
		
		if (name.equalsIgnoreCase("sbx")) {
			return new SBX(
					typedProperties.getDouble("sbx.rate", 1.0), 
					typedProperties.getDouble("sbx.distributionIndex", 15.0),
					typedProperties.getBoolean("sbx.swap", true),
					typedProperties.getBoolean("sbx.symmetric", false));
		} else if (name.equalsIgnoreCase("pm")) {
			return new PM(
					typedProperties.getDouble("pm.rate", 
							1.0 / problem.getNumberOfVariables()), 
							typedProperties.getDouble("pm.distributionIndex", 20.0));
		} else if (name.equalsIgnoreCase("de")) {	
			return new DifferentialEvolutionVariation(
					typedProperties.getDouble("de.crossoverRate", 0.1), 
					typedProperties.getDouble("de.stepSize", 0.5));
		} else if (name.equalsIgnoreCase("pcx")) {
			return new PCX(
					(int)typedProperties.getDouble("pcx.parents", 10),
					(int)typedProperties.getDouble("pcx.offspring", 2), 
					typedProperties.getDouble("pcx.eta", 0.1), 
					typedProperties.getDouble("pcx.zeta", 0.1));
		} else if (name.equalsIgnoreCase("spx")) {
			return new SPX(
					(int)typedProperties.getDouble("spx.parents", 10),
					(int)typedProperties.getDouble("spx.offspring", 2),
					typedProperties.getDouble("spx.epsilon", 3));
		} else if (name.equalsIgnoreCase("undx")) {
			return new UNDX(
					(int)typedProperties.getDouble("undx.parents", 10),
					(int)typedProperties.getDouble("undx.offspring", 2), 
					typedProperties.getDouble("undx.zeta", 0.5), 
					typedProperties.getDouble("undx.eta", 0.35));
		} else if (name.equalsIgnoreCase("um")) {
			return new UM(
					typedProperties.getDouble("um.rate", 
							1.0 / problem.getNumberOfVariables()));
		} else if (name.equalsIgnoreCase("am")) {
			return new AdaptiveMetropolis(
					(int)typedProperties.getDouble("am.parents", 10),
					(int)typedProperties.getDouble("am.offspring", 2), 
					typedProperties.getDouble("am.coefficient", 2.4));
		} else if (name.equalsIgnoreCase("hux")) {
			return new HUX(
					typedProperties.getDouble("hux.rate", 1.0));
		} else if (name.equalsIgnoreCase("bf")) {
			return new BitFlip(
					typedProperties.getDouble("bf.rate", 0.01));
		} else if (name.equalsIgnoreCase("pmx")) {
			return new PMX(
					typedProperties.getDouble("pmx.rate", 1.0));
		} else if (name.equalsIgnoreCase("insertion")) {
			return new Insertion(
					typedProperties.getDouble("insertion.rate", 0.3));
		} else if (name.equalsIgnoreCase("swap")) {
			return new Swap(
					typedProperties.getDouble("swap.rate", 0.3));
		} else if (name.equalsIgnoreCase("1x")) {
			return new OnePointCrossover(
					typedProperties.getDouble("1x.rate", 1.0));
		} else if (name.equalsIgnoreCase("2x")) {
			return new TwoPointCrossover(
					typedProperties.getDouble("2x.rate", 1.0));
		} else if (name.equalsIgnoreCase("ux")) {
			return new UniformCrossover(
					typedProperties.getDouble("ux.rate", 1.0));
		} else if (name.equalsIgnoreCase("gx")) {
			return new GrammarCrossover(
					typedProperties.getDouble("gx.rate", 1.0));
		} else if (name.equalsIgnoreCase("gm")) {
			return new GrammarMutation(
					typedProperties.getDouble("gm.rate", 1.0));
		} else if (name.equalsIgnoreCase("ptm")) {
			return new PointMutation(
					typedProperties.getDouble("ptm.rate", 0.01));
		} else if (name.equalsIgnoreCase("bx")) {
			return new SubtreeCrossover(
					typedProperties.getDouble("bx.rate", 0.9));
		} else if (name.equalsIgnoreCase("replace")) {
			return new Replace(
					typedProperties.getDouble("replace.rate", 0.9));
		} else if (name.equalsIgnoreCase("add")) {
			return new Add(
					typedProperties.getDouble("add.rate", 0.1));
		} else if (name.equalsIgnoreCase("remove")) {
			return new Remove(
					typedProperties.getDouble("remove.rate", 0.1));
		} else if (name.equalsIgnoreCase("ssx")) {
			return new SSX(
					typedProperties.getDouble("ssx.rate", 0.3));
		} else {
			return null;
		}
	}

}

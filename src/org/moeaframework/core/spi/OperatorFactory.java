/* Copyright 2009-2015 David Hadka
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
package org.moeaframework.core.spi;

import java.util.EnumSet;
import java.util.Properties;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.TwoPointCrossover;
import org.moeaframework.core.operator.UniformCrossover;
import org.moeaframework.core.operator.binary.BitFlip;
import org.moeaframework.core.operator.binary.HUX;
import org.moeaframework.core.operator.grammar.GrammarCrossover;
import org.moeaframework.core.operator.grammar.GrammarMutation;
import org.moeaframework.core.operator.permutation.Insertion;
import org.moeaframework.core.operator.permutation.PMX;
import org.moeaframework.core.operator.permutation.Swap;
import org.moeaframework.core.operator.program.SubtreeCrossover;
import org.moeaframework.core.operator.program.PointMutation;
import org.moeaframework.core.operator.real.AdaptiveMetropolis;
import org.moeaframework.core.operator.real.DifferentialEvolution;
import org.moeaframework.core.operator.real.PCX;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.operator.real.SPX;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.operator.real.UNDX;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.Program;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.TypedProperties;

/**
 * Factory for creating operator instances.  The table below shows the supported
 * operators.  The name and properties columns show the values accepted by
 * {@link #getVariation(String, Properties, Problem)}.
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
 *     <td>{@link DifferentialEvolution}</td>
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
 * <p>
 * Operators can be combined by joining the two operator names with the plus
 * sign, such as {@code "sbx+pm"}.  Not all operators can be joined this way.
 * See {@link CompoundVariation} for the restrictions.
 * <p>
 * This class is thread safe.
 */
public class OperatorFactory {

	/**
	 * The types supported by this operator factory.
	 */
	private static enum Type {
		
		REAL,
		
		BINARY,
		
		PERMUTATION,
		
		GRAMMAR,
		
		PROGRAM,
		
		UNKNOWN

	}
	
	/**
	 * The default operator factory.
	 */
	private static OperatorFactory instance;
	
	/**
	 * Instantiates the static {@code instance} object.
	 */
	static {
		instance = new OperatorFactory();
	}
	
	/**
	 * Returns the default operator factory.
	 * 
	 * @return the default operator factory
	 */
	public static synchronized OperatorFactory getInstance() {
		return instance;
	}

	/**
	 * Sets the default operator factory.
	 * 
	 * @param instance the default operator factory
	 */
	public static synchronized void setInstance(OperatorFactory instance) {
		OperatorFactory.instance = instance;
	}
	
	/**
	 * Constructs a new operator factory.
	 */
	public OperatorFactory() {
		super();
	}
	
	/**
	 * Returns the name of the default variation operator for the given problem.
	 * Mixed types are currently not supported.
	 * 
	 * @param problem the problem
	 * @return the name of the default variation operator for the given problem
	 * @throws ProviderNotFoundException if no default variation operator could
	 *         be determined
	 */
	private String getDefaultVariation(Problem problem) {
		EnumSet<Type> types = EnumSet.noneOf(Type.class);
		Solution solution = problem.newSolution();
		
		for (int i=0; i<solution.getNumberOfVariables(); i++) {
			Variable variable = solution.getVariable(i);
			
			if (variable instanceof RealVariable) {
				types.add(Type.REAL);
			} else if (variable instanceof BinaryVariable) {
				types.add(Type.BINARY);
			} else if (variable instanceof Permutation) {
				types.add(Type.PERMUTATION);
			} else if (variable instanceof Grammar) {
				types.add(Type.GRAMMAR);
			} else if (variable instanceof Program) {
				types.add(Type.PROGRAM);
			} else {
				types.add(Type.UNKNOWN);
			}
		}
		
		if (types.isEmpty()) {
			throw new ProviderNotFoundException("empty type");
		} else if (types.size() > 1) {
			throw new ProviderNotFoundException("mixed type");
		}
		
		Type type = types.iterator().next();
		
		if (type.equals(Type.REAL)) {
			return "sbx+pm";
		} else if (type.equals(Type.BINARY)) {
			return "hux+bf";
		} else if (type.equals(Type.PERMUTATION)) {
			return "pmx+insertion+swap";
		} else if (type.equals(Type.GRAMMAR)) {
			return "gx+gm";
		} else if (type.equals(Type.PROGRAM)) {
			return "bx+ptm";
		} else {
			throw new ProviderNotFoundException("unknown type");
		}
	}

	/**
	 * Equivalent to calling 
	 * {@link #getVariation(String, TypedProperties, Problem)}.
	 * 
	 * @param name the name identifying the variation operator
	 * @param properties the implementation-specific properties
	 * @param problem the problem to be solved
	 * @return an instance of the variation operator with the specified name
	 * @throws ProviderNotFoundException if no provider for the algorithm is 
	 *         available
	 */
	public Variation getVariation(String name, Properties properties, 
			Problem problem) {
		return getVariation(name, new TypedProperties(properties), problem);
	}

	/**
	 * Returns an instance of the variation operator with the specified name.
	 * This method must throw an {@link ProviderNotFoundException} if no 
	 * suitable operator is found.  If {@code name} is null, the factory should
	 * return a default variation operator appropriate for the problem.
	 * 
	 * @param name the name identifying the variation operator
	 * @param properties the implementation-specific properties
	 * @param problem the problem to be solved
	 * @return an instance of the variation operator with the specified name
	 * @throws ProviderNotFoundException if no provider for the algorithm is 
	 *         available
	 */
	public Variation getVariation(String name, TypedProperties properties, 
			Problem problem) {
		if (name == null) {
			String operator = properties.getString("operator", null);
			
			if (operator == null) {
				return getVariation(getDefaultVariation(problem), properties, 
						problem);
			} else {
				return getVariation(operator, properties, problem);
			}
		} else if (name.contains("+")) {
			String[] entries = name.split("\\s*\\+\\s*");
			CompoundVariation variation = new CompoundVariation();
			
			for (String entry : entries) {
				variation.appendOperator(getVariation(entry, properties,
						problem));
			}
			
			return variation;
		} else {
			if (name.equalsIgnoreCase("sbx")) {
				return new SBX(
						properties.getDouble("sbx.rate", 1.0), 
						properties.getDouble("sbx.distributionIndex", 15.0));
			} else if (name.equalsIgnoreCase("pm")) {
				return new PM(
						properties.getDouble("pm.rate", 
								1.0 / problem.getNumberOfVariables()), 
						properties.getDouble("pm.distributionIndex", 20.0));
			} else if (name.equalsIgnoreCase("de")) {	
				return new DifferentialEvolution(
						properties.getDouble("de.crossoverRate", 0.1), 
						properties.getDouble("de.stepSize", 0.5));
			} else if (name.equalsIgnoreCase("pcx")) {
				return new PCX(
						(int)properties.getDouble("pcx.parents", 10),
						(int)properties.getDouble("pcx.offspring", 2), 
						properties.getDouble("pcx.eta", 0.1), 
						properties.getDouble("pcx.zeta", 0.1));
			} else if (name.equalsIgnoreCase("spx")) {
				return new SPX(
						(int)properties.getDouble("spx.parents", 10),
						(int)properties.getDouble("spx.offspring", 2),
						properties.getDouble("spx.epsilon", 3));
			} else if (name.equalsIgnoreCase("undx")) {
				return new UNDX(
						(int)properties.getDouble("undx.parents", 10),
						(int)properties.getDouble("undx.offspring", 2), 
						properties.getDouble("undx.zeta", 0.5), 
						properties.getDouble("undx.eta", 0.35));
			} else if (name.equalsIgnoreCase("um")) {
				return new UM(
						properties.getDouble("um.rate", 
								1.0 / problem.getNumberOfVariables()));
			} else if (name.equalsIgnoreCase("am")) {
				return new AdaptiveMetropolis(
						(int)properties.getDouble("am.parents", 10),
						(int)properties.getDouble("am.offspring", 2), 
						properties.getDouble("am.coefficient", 2.4));
			} else if (name.equalsIgnoreCase("hux")) {
				return new HUX(
						properties.getDouble("hux.rate", 1.0));
			} else if (name.equalsIgnoreCase("bf")) {
				return new BitFlip(
						properties.getDouble("bf.rate", 0.01));
			} else if (name.equalsIgnoreCase("pmx")) {
				return new PMX(
						properties.getDouble("pmx.rate", 1.0));
			} else if (name.equalsIgnoreCase("insertion")) {
				return new Insertion(
						properties.getDouble("insertion.rate", 0.3));
			} else if (name.equalsIgnoreCase("swap")) {
				return new Swap(
						properties.getDouble("swap.rate", 0.3));
			} else if (name.equalsIgnoreCase("1x")) {
				return new OnePointCrossover(
						properties.getDouble("1x.rate", 1.0));
			} else if (name.equalsIgnoreCase("2x")) {
				return new TwoPointCrossover(
						properties.getDouble("2x.rate", 1.0));
			} else if (name.equalsIgnoreCase("ux")) {
				return new UniformCrossover(
						properties.getDouble("ux.rate", 1.0));
			} else if (name.equalsIgnoreCase("gx")) {
				return new GrammarCrossover(
						properties.getDouble("gx.rate", 1.0));
			} else if (name.equalsIgnoreCase("gm")) {
				return new GrammarMutation(
						properties.getDouble("gm.rate", 1.0));
			} else if (name.equalsIgnoreCase("ptm")) {
				return new PointMutation(
						properties.getDouble("ptm.rate", 0.01));
			} else if (name.equalsIgnoreCase("bx")) {
				return new SubtreeCrossover(
						properties.getDouble("bx.rate", 0.9));
			} else {
				throw new ProviderNotFoundException(name);
			}
		}
	}
	
}

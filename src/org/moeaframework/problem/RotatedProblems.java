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
package org.moeaframework.problem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProblemProvider;
import org.moeaframework.util.RotationMatrixBuilder;

/**
 * Problem provider for rotated problems.  Supports any problem available
 * through {@code ProblemProvider#getProblem(String)}.  See {@link 
 * RotatedProblem} for details on how rotation is supported.  Rotated problems
 * are instantiated by providing the problem name prefixed with one of the
 * following prefix patterns.
 * <p>
 * <table width="100%" border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="20%" align="left">Prefix Pattern</th>
 *     <th width="80%" align="left">Result</th>
 *   </tr>
 *   <tr>
 *     <td>{@code UNROT_}</td>
 *     <td>Unrotated problem</td>
 *   </tr>
 *   <tr>
 *     <td>{@code ROT_}</td>
 *     <td>Fully rotated instance, with each plane rotated by 45&deg;</td>
 *   </tr>
 *   <tr>
 *     <td>{@code ROT(ANGLE)_}</td>
 *     <td>Fully rotated instance, with each plane rotated by {@code ANGLE} 
 *         degrees.  Use {@code RAND} for randomized rotations</td>
 *   </tr>
 *   <tr>
 *     <td>{@code ROT(K,ANGLE)_}</td>
 *     <td>{@code K} random rotation planes, each rotated by {@code ANGLE}
 *         degrees.  Use {@code ALL} to rotate all planes</td>
 *   </tr>
 * </table>
 * <p>
 * As an example, rotated 2D DTLZ2 instances can be created with 
 * {@code "UNROT_DTLZ2_2"}, {@code "ROT_DTLZ2_2"}, {@code "ROT(30)_DTLZ2_2"}
 * and {@code "ROT(10,RAND)_DTLZ2_2"}.  Note that multiple calls to
 * {@link #getProblem(String)} may return instances with different rotations.
 * <p>
 * When comparing rotated problems against their unrotated versions, it is
 * important to use the {@code UNROT_} prefix for the unrotated problem.  The
 * act of rotating a problem expands the decision variable ranges slightly
 * (imagine a 1 x 1 square that is rotated 45 degrees; the bounding box of the
 * rotated square is approximately 1.4 x 1.4).  The {@code UNROT_} prefix
 * ensures the unrotated version uses the same expanded decision variable ranges
 * as the rotated variant.
 */
public class RotatedProblems extends ProblemProvider {

	/**
	 * The regular expression pattern for parsing the rotation prefix.
	 */
	private static final Pattern PATTERN = Pattern.compile(
			"^(?:(ROT)(?:\\((?:([0-9]+|ALL),)?(\\-?[0-9]+|RAND)\\))?|UNROT)_(.*)$",
			Pattern.CASE_INSENSITIVE);
	
	/**
	 * Constructs a problem provider for rotated problems.
	 */
	public RotatedProblems() {
		super();
	}

	@Override
	public Problem getProblem(String name) {
		Matcher matcher = PATTERN.matcher(name);
		
		if (matcher.matches()) {
			Problem problem = ProblemFactory.getInstance().getProblem(
					matcher.group(4));
			RotationMatrixBuilder rotationMatrix = new RotationMatrixBuilder(
					problem.getNumberOfVariables());
			
			//if rotated, apply the requested rotations
			if (matcher.group(1) != null) {
				String k = matcher.group(2);
				String angle = matcher.group(3);
				
				if ((k == null) || k.equalsIgnoreCase("ALL")) {
					rotationMatrix.rotateAll();
				} else {
					rotationMatrix.rotateK(Integer.parseInt(k));
				}
				
				if (angle == null) {
					rotationMatrix.withThetas(Math.PI/4.0);
				} else if (angle.equalsIgnoreCase("RAND")) {
					rotationMatrix.withRandomThetas();
				} else {
					rotationMatrix.withThetas(Math.toRadians(
							Double.parseDouble(angle)));
				}
			}
			
			return new RotatedProblem(problem, rotationMatrix.create());
		} else {
			return null;
		}
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		Matcher matcher = PATTERN.matcher(name);
		
		if (matcher.matches()) {
			return ProblemFactory.getInstance().getReferenceSet(
					matcher.group(4));
		} else {
			return null;
		}
	}
	
}

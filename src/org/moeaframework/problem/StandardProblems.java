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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemProvider;
import org.moeaframework.problem.CEC2009.CF1;
import org.moeaframework.problem.CEC2009.CF10;
import org.moeaframework.problem.CEC2009.CF2;
import org.moeaframework.problem.CEC2009.CF3;
import org.moeaframework.problem.CEC2009.CF4;
import org.moeaframework.problem.CEC2009.CF5;
import org.moeaframework.problem.CEC2009.CF6;
import org.moeaframework.problem.CEC2009.CF7;
import org.moeaframework.problem.CEC2009.CF8;
import org.moeaframework.problem.CEC2009.CF9;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.CEC2009.UF10;
import org.moeaframework.problem.CEC2009.UF11;
import org.moeaframework.problem.CEC2009.UF12;
import org.moeaframework.problem.CEC2009.UF13;
import org.moeaframework.problem.CEC2009.UF2;
import org.moeaframework.problem.CEC2009.UF3;
import org.moeaframework.problem.CEC2009.UF4;
import org.moeaframework.problem.CEC2009.UF5;
import org.moeaframework.problem.CEC2009.UF6;
import org.moeaframework.problem.CEC2009.UF7;
import org.moeaframework.problem.CEC2009.UF8;
import org.moeaframework.problem.CEC2009.UF9;
import org.moeaframework.problem.DTLZ.DTLZ1;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.DTLZ.DTLZ3;
import org.moeaframework.problem.DTLZ.DTLZ4;
import org.moeaframework.problem.DTLZ.DTLZ7;
import org.moeaframework.problem.LZ.LZ1;
import org.moeaframework.problem.LZ.LZ2;
import org.moeaframework.problem.LZ.LZ3;
import org.moeaframework.problem.LZ.LZ4;
import org.moeaframework.problem.LZ.LZ5;
import org.moeaframework.problem.LZ.LZ6;
import org.moeaframework.problem.LZ.LZ7;
import org.moeaframework.problem.LZ.LZ8;
import org.moeaframework.problem.LZ.LZ9;
import org.moeaframework.problem.WFG.WFG1;
import org.moeaframework.problem.WFG.WFG2;
import org.moeaframework.problem.WFG.WFG3;
import org.moeaframework.problem.WFG.WFG4;
import org.moeaframework.problem.WFG.WFG5;
import org.moeaframework.problem.WFG.WFG6;
import org.moeaframework.problem.WFG.WFG7;
import org.moeaframework.problem.WFG.WFG8;
import org.moeaframework.problem.WFG.WFG9;
import org.moeaframework.problem.ZDT.ZDT1;
import org.moeaframework.problem.ZDT.ZDT2;
import org.moeaframework.problem.ZDT.ZDT3;
import org.moeaframework.problem.ZDT.ZDT4;
import org.moeaframework.problem.ZDT.ZDT5;
import org.moeaframework.problem.ZDT.ZDT6;
import org.moeaframework.problem.misc.Belegundu;
import org.moeaframework.problem.misc.Binh;
import org.moeaframework.problem.misc.Binh2;
import org.moeaframework.problem.misc.Binh3;
import org.moeaframework.problem.misc.Binh4;
import org.moeaframework.problem.misc.Fonseca;
import org.moeaframework.problem.misc.Fonseca2;
import org.moeaframework.problem.misc.Jimenez;
import org.moeaframework.problem.misc.Kita;
import org.moeaframework.problem.misc.Kursawe;
import org.moeaframework.problem.misc.Laumanns;
import org.moeaframework.problem.misc.Lis;
import org.moeaframework.problem.misc.Murata;
import org.moeaframework.problem.misc.OKA1;
import org.moeaframework.problem.misc.OKA2;
import org.moeaframework.problem.misc.Obayashi;
import org.moeaframework.problem.misc.Osyczka;
import org.moeaframework.problem.misc.Osyczka2;
import org.moeaframework.problem.misc.Poloni;
import org.moeaframework.problem.misc.Quagliarella;
import org.moeaframework.problem.misc.Rendon;
import org.moeaframework.problem.misc.Rendon2;
import org.moeaframework.problem.misc.Schaffer;
import org.moeaframework.problem.misc.Schaffer2;
import org.moeaframework.problem.misc.Srinivas;
import org.moeaframework.problem.misc.Tamaki;
import org.moeaframework.problem.misc.Tanaka;
import org.moeaframework.problem.misc.Viennet;
import org.moeaframework.problem.misc.Viennet2;
import org.moeaframework.problem.misc.Viennet3;
import org.moeaframework.problem.misc.Viennet4;
import org.moeaframework.util.io.CommentedLineReader;

/**
 * Provides a standard set of test problems. The table below details the 
 * problems made available by this {@link ProblemProvider}.  Problems are 
 * identified by a name, which is used as an argument to 
 * {@link #getProblem(String)}.  Names with {@code %D} indicate that the number
 * of objectives must be specified as an integer.  For instance, {@code DTLZ2_2}
 * creates an instance of DTLZ2 with 2 objectives.
 * <p>
 * <table width="100%" border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="10%" align="left">Class</th>
 *     <th width="10%" align="left">Name</th>
 *     <th width="10%" align="left">Type</th>
 *     <th width="70%" align="left">Characteristics</th>
 *   </tr>
 *   <tr>
 *     <td>{@link CF1}</td>
 *     <td>{@code CF1}</td>
 *     <td>Real</td>
 *     <td>Constrained</td>
 *   </tr>
 *   <tr>
 *     <td>{@link CF2}</td>
 *     <td>{@code CF2}</td>
 *     <td>Real</td>
 *     <td>Constrained</td>
 *   </tr>
 *   <tr>
 *     <td>{@link CF3}</td>
 *     <td>{@code CF3}</td>
 *     <td>Real</td>
 *     <td>Constrained</td>
 *   </tr>
 *   <tr>
 *     <td>{@link CF4}</td>
 *     <td>{@code CF4}</td>
 *     <td>Real</td>
 *     <td>Constrained</td>
 *   </tr>
 *   <tr>
 *     <td>{@link CF5}</td>
 *     <td>{@code CF5}</td>
 *     <td>Real</td>
 *     <td>Constrained</td>
 *   </tr>
 *   <tr>
 *     <td>{@link CF6}</td>
 *     <td>{@code CF6}</td>
 *     <td>Real</td>
 *     <td>Constrained</td>
 *   </tr>
 *   <tr>
 *     <td>{@link CF7}</td>
 *     <td>{@code CF7}</td>
 *     <td>Real</td>
 *     <td>Constrained</td>
 *   </tr>
 *   <tr>
 *     <td>{@link CF8}</td>
 *     <td>{@code CF8}</td>
 *     <td>Real</td>
 *     <td>Constrained</td>
 *   </tr>
 *   <tr>
 *     <td>{@link CF9}</td>
 *     <td>{@code CF9}</td>
 *     <td>Real</td>
 *     <td>Constrained</td>
 *   </tr>
 *   <tr>
 *     <td>{@link CF10}</td>
 *     <td>{@code CF10}</td>
 *     <td>Real</td>
 *     <td>Constrained</td>
 *   </tr>
 *   <tr>
 *     <td>{@link DTLZ1}</td>
 *     <td>{@code DTLZ1_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link DTLZ2}</td>
 *     <td>{@code DTLZ2_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link DTLZ3}</td>
 *     <td>{@code DTLZ3_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link DTLZ4}</td>
 *     <td>{@code DTLZ4_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link DTLZ7}</td>
 *     <td>{@code DTLZ7_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link LZ1}</td>
 *     <td>{@code LZ1}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link LZ2}</td>
 *     <td>{@code LZ2}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link LZ3}</td>
 *     <td>{@code LZ3}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link LZ4}</td>
 *     <td>{@code LZ4}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link LZ5}</td>
 *     <td>{@code LZ5}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link LZ6}</td>
 *     <td>{@code LZ6}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link LZ7}</td>
 *     <td>{@code LZ7}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link LZ8}</td>
 *     <td>{@code LZ8}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link LZ9}</td>
 *     <td>{@code LZ9}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF1}</td>
 *     <td>{@code UF1}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF2}</td>
 *     <td>{@code UF2}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF3}</td>
 *     <td>{@code UF3}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF4}</td>
 *     <td>{@code UF4}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF5}</td>
 *     <td>{@code UF5}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF6}</td>
 *     <td>{@code UF6}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF7}</td>
 *     <td>{@code UF7}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF8}</td>
 *     <td>{@code UF8}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF9}</td>
 *     <td>{@code UF9}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF10}</td>
 *     <td>{@code UF10}</td>
 *     <td>Real</td>
 *     <td>Complicated Pareto set</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF11}</td>
 *     <td>{@code UF11}</td>
 *     <td>Real</td>
 *     <td>Rotated instance of {@code DTLZ2_5}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF12}</td>
 *     <td>{@code UF12}</td>
 *     <td>Real</td>
 *     <td>Rotated instance of {@code DTLZ3_5}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link UF13}</td>
 *     <td>{@code UF13}</td>
 *     <td>Real</td>
 *     <td>Instance of {@code WFG_5}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link WFG1}</td>
 *     <td>{@code WFG1_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link WFG2}</td>
 *     <td>{@code WFG2_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link WFG3}</td>
 *     <td>{@code WFG3_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link WFG4}</td>
 *     <td>{@code WFG4_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link WFG5}</td>
 *     <td>{@code WFG5_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link WFG6}</td>
 *     <td>{@code WFG6_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link WFG7}</td>
 *     <td>{@code WFG7_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link WFG8}</td>
 *     <td>{@code WFG8_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link WFG9}</td>
 *     <td>{@code WFG9_%D}</td>
 *     <td>Real</td>
 *     <td>Scalable to any number of objectives</td>
 *   </tr>
 *   <tr>
 *     <td>{@link ZDT1}</td>
 *     <td>{@code ZDT1}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link ZDT2}</td>
 *     <td>{@code ZDT2}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link ZDT3}</td>
 *     <td>{@code ZDT3}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link ZDT4}</td>
 *     <td>{@code ZDT4}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link ZDT5}</td>
 *     <td>{@code ZDT5}</td>
 *     <td>Binary</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link ZDT6}</td>
 *     <td>{@code ZDT6}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Belegundu}</td>
 *     <td>{@code Belegundu}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Binh}</td>
 *     <td>{@code Binh}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Binh2}</td>
 *     <td>{@code Binh2}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Binh3}</td>
 *     <td>{@code Binh3}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Binh4}</td>
 *     <td>{@code Binh4}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fonseca}</td>
 *     <td>{@code Fonseca}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Fonseca2}</td>
 *     <td>{@code Fonseca2}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Jimenez}</td>
 *     <td>{@code Jimenez}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Kita}</td>
 *     <td>{@code Kita}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Kursawe}</td>
 *     <td>{@code Kursawe}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Laumanns}</td>
 *     <td>{@code Laumanns}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Lis}</td>
 *     <td>{@code Lis}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Murata}</td>
 *     <td>{@code Murata}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Obayashi}</td>
 *     <td>{@code Obayashi}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link OKA1}</td>
 *     <td>{@code OKA1}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link OKA2}</td>
 *     <td>{@code OKA2}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Osyczka}</td>
 *     <td>{@code Osyczka}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Osyczka2}</td>
 *     <td>{@code Osyczka2}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Poloni}</td>
 *     <td>{@code Poloni}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Quagliarella}</td>
 *     <td>{@code Quagliarella}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Rendon}</td>
 *     <td>{@code Rendon}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Rendon2}</td>
 *     <td>{@code Rendon2}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Schaffer}</td>
 *     <td>{@code Schaffer}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Schaffer2}</td>
 *     <td>{@code Schaffer2}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Srinivas}</td>
 *     <td>{@code Srinivas}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Tamaki}</td>
 *     <td>{@code Tamaki}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Tanaka}</td>
 *     <td>{@code Tanaka}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Viennet}</td>
 *     <td>{@code Viennet}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Viennet2}</td>
 *     <td>{@code Viennet2}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Viennet3}</td>
 *     <td>{@code Viennet3}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>{@link Viennet4}</td>
 *     <td>{@code Viennet4}</td>
 *     <td>Real</td>
 *     <td></td>
 *   </tr>
 * </table>
 */
public class StandardProblems extends ProblemProvider {

	/**
	 * Constructs a problem provider for the standard set of test problems.
	 */
	public StandardProblems() {
		super();
	}

	@Override
	public Problem getProblem(String name) {
		name = name.toUpperCase();
		
		try {
			if (name.startsWith("DTLZ1")) {
				return new DTLZ1(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("DTLZ2")) {
				return new DTLZ2(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("DTLZ3")) {
				return new DTLZ3(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("DTLZ4")) {
				return new DTLZ4(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("DTLZ7")) {
				return new DTLZ7(Integer.parseInt(name.substring(6)));
			} else if (name.startsWith("WFG1")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG1(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG2")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG2(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG3")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG3(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG4")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG4(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG5")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG5(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG6")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG6(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG7")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG7(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG8")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG8(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.startsWith("WFG9")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				return new WFG9(numberOfObjectives - 1, 10, numberOfObjectives);
			} else if (name.equals("ZDT1")) {
				return new ZDT1();
			} else if (name.equals("ZDT2")) {
				return new ZDT2();
			} else if (name.equals("ZDT3")) {
				return new ZDT3();
			} else if (name.equals("ZDT4")) {
				return new ZDT4();
			} else if (name.equals("ZDT5")) {
				return new ZDT5();
			} else if (name.equals("ZDT6")) {
				return new ZDT6();
			} else if (name.equals("UF1")) {
				return new UF1();
			} else if (name.equals("UF2")) {
				return new UF2();
			} else if (name.equals("UF3")) {
				return new UF3();
			} else if (name.equals("UF4")) {
				return new UF4();
			} else if (name.equals("UF5")) {
				return new UF5();
			} else if (name.equals("UF6")) {
				return new UF6();
			} else if (name.equals("UF7")) {
				return new UF7();
			} else if (name.equals("UF8")) {
				return new UF8();
			} else if (name.equals("UF9")) {
				return new UF9();
			} else if (name.equals("UF10")) {
				return new UF10();
			} else if (name.equals("UF11")) {
				return new UF11();
			} else if (name.equals("UF12")) {
				return new UF12();
			} else if (name.equals("UF13")) {
				return new UF13();
			} else if (name.equals("CF1")) {
				return new CF1();
			} else if (name.equals("CF2")) {
				return new CF2();
			} else if (name.equals("CF3")) {
				return new CF3();
			} else if (name.equals("CF4")) {
				return new CF4();
			} else if (name.equals("CF5")) {
				return new CF5();
			} else if (name.equals("CF6")) {
				return new CF6();
			} else if (name.equals("CF7")) {
				return new CF7();
			} else if (name.equals("CF8")) {
				return new CF8();
			} else if (name.equals("CF9")) {
				return new CF9();
			} else if (name.equals("CF10")) {
				return new CF10();
			} else if (name.equals("LZ1")) {
				return new LZ1();
			} else if (name.equals("LZ2")) {
				return new LZ2();
			} else if (name.equals("LZ3")) {
				return new LZ3();
			} else if (name.equals("LZ4")) {
				return new LZ4();
			} else if (name.equals("LZ5")) {
				return new LZ5();
			} else if (name.equals("LZ6")) {
				return new LZ6();
			} else if (name.equals("LZ7")) {
				return new LZ7();
			} else if (name.equals("LZ8")) {
				return new LZ8();
			} else if (name.equals("LZ9")) {
				return new LZ9();
			} else if (name.equalsIgnoreCase("Belegundu")) {
				return new Belegundu();
			} else if (name.equalsIgnoreCase("Binh")) {
				return new Binh();
			} else if (name.equalsIgnoreCase("Binh2")) {
				return new Binh2();
			} else if (name.equalsIgnoreCase("Binh3")) {
				return new Binh3();
			} else if (name.equalsIgnoreCase("Binh4")) {
				return new Binh4();
			} else if (name.equalsIgnoreCase("Fonseca")) {
				return new Fonseca();
			} else if (name.equalsIgnoreCase("Fonseca2")) {
				return new Fonseca2();
			} else if (name.equalsIgnoreCase("Jimenez")) {
				return new Jimenez();
			} else if (name.equalsIgnoreCase("Kita")) {
				return new Kita();
			} else if (name.equalsIgnoreCase("Kursawe")) {
				return new Kursawe();
			} else if (name.equalsIgnoreCase("Laumanns")) {
				return new Laumanns();
			} else if (name.equalsIgnoreCase("Lis")) {
				return new Lis();
			} else if (name.equalsIgnoreCase("Murata")) {
				return new Murata();
			} else if (name.equalsIgnoreCase("Obayashi")) {
				return new Obayashi();
			} else if (name.equalsIgnoreCase("OKA1")) {
				return new OKA1();
			} else if (name.equalsIgnoreCase("OKA2")) {
				return new OKA2();
			} else if (name.equalsIgnoreCase("Osyczka")) {
				return new Osyczka();
			} else if (name.equalsIgnoreCase("Osyczka2")) {
				return new Osyczka2();
			} else if (name.equalsIgnoreCase("Poloni")) {
				return new Poloni();
			} else if (name.equalsIgnoreCase("Quagliarella")) {
				return new Quagliarella();
			} else if (name.equalsIgnoreCase("Rendon")) {
				return new Rendon();
			} else if (name.equalsIgnoreCase("Rendon2")) {
				return new Rendon2();
			} else if (name.equalsIgnoreCase("Schaffer")) {
				return new Schaffer();
			} else if (name.equalsIgnoreCase("Schaffer2")) {
				return new Schaffer2();
			} else if (name.equalsIgnoreCase("Srinivas")) {
				return new Srinivas();
			} else if (name.equalsIgnoreCase("Tamaki")) {
				return new Tamaki();
			} else if (name.equalsIgnoreCase("Tanaka")) {
				return new Tanaka();
			} else if (name.equalsIgnoreCase("Viennet")) {
				return new Viennet();
			} else if (name.equalsIgnoreCase("Viennet2")) {
				return new Viennet2();
			} else if (name.equalsIgnoreCase("Viennet3")) {
				return new Viennet3();
			} else if (name.equalsIgnoreCase("Viennet4")) {
				return new Viennet4();
			} else {
				return null;
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		String filename = null;
		name = name.toUpperCase();

		try {
			if (name.startsWith("DTLZ1")) {
				int numberOfObjectives = Integer.parseInt(name.substring(6));
				filename = "DTLZ1." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("DTLZ2")) {
				int numberOfObjectives = Integer.parseInt(name.substring(6));
				filename = "DTLZ2." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("DTLZ3")) {
				int numberOfObjectives = Integer.parseInt(name.substring(6));
				filename = "DTLZ3." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("DTLZ4")) {
				int numberOfObjectives = Integer.parseInt(name.substring(6));
				filename = "DTLZ4." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("DTLZ7")) {
				int numberOfObjectives = Integer.parseInt(name.substring(6));
				filename = "DTLZ7." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("WFG1")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				filename = "WFG1." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("WFG2")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				filename = "WFG2." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("WFG3")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				filename = "WFG3." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("WFG4")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				filename = "WFG4." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("WFG5")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				filename = "WFG5." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("WFG6")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				filename = "WFG6." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("WFG7")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				filename = "WFG7." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("WFG8")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				filename = "WFG8." + numberOfObjectives + "D.pf";
			} else if (name.startsWith("WFG9")) {
				int numberOfObjectives = Integer.parseInt(name.substring(5));
				filename = "WFG9." + numberOfObjectives + "D.pf";
			} else if (name.equals("ZDT1")) {
				filename = "ZDT1.pf";
			} else if (name.equals("ZDT2")) {
				filename = "ZDT2.pf";
			} else if (name.equals("ZDT3")) {
				filename = "ZDT3.pf";
			} else if (name.equals("ZDT4")) {
				filename = "ZDT4.pf";
			} else if (name.equals("ZDT5")) {
				filename = "ZDT5.pf";
			} else if (name.equals("ZDT6")) {
				filename = "ZDT6.pf";
			} else if (name.equals("UF1")) {
				filename = "UF1.dat";
			} else if (name.equals("UF2")) {
				filename = "UF2.dat";
			} else if (name.equals("UF3")) {
				filename = "UF3.dat";
			} else if (name.equals("UF4")) {
				filename = "UF4.dat";
			} else if (name.equals("UF5")) {
				filename = "UF5.dat";
			} else if (name.equals("UF6")) {
				filename = "UF6.dat";
			} else if (name.equals("UF7")) {
				filename = "UF7.dat";
			} else if (name.equals("UF8")) {
				filename = "UF8.dat";
			} else if (name.equals("UF9")) {
				filename = "UF9.dat";
			} else if (name.equals("UF10")) {
				filename = "UF10.dat";
			} else if (name.equals("UF11")) {
				filename = "R2_DTLZ2_M5.dat";
			} else if (name.equals("UF12")) {
				filename = "R3_DTLZ3_M5.dat";
			} else if (name.equals("UF13")) {
				filename = "WFG1_M5.dat";
			} else if (name.equals("CF1")) {
				filename = "CF1.dat";
			} else if (name.equals("CF2")) {
				filename = "CF2.dat";
			} else if (name.equals("CF3")) {
				filename = "CF3.dat";
			} else if (name.equals("CF4")) {
				filename = "CF4.dat";
			} else if (name.equals("CF5")) {
				filename = "CF5.dat";
			} else if (name.equals("CF6")) {
				filename = "CF6.dat";
			} else if (name.equals("CF7")) {
				filename = "CF7.dat";
			} else if (name.equals("CF8")) {
				filename = "CF8.dat";
			} else if (name.equals("CF9")) {
				filename = "CF9.dat";
			} else if (name.equals("CF10")) {
				filename = "CF10.dat";
			} else if (name.equals("LZ1")) {
				filename = "LZ09_F1.pf";
			} else if (name.equals("LZ2")) {
				filename = "LZ09_F2.pf";
			} else if (name.equals("LZ3")) {
				filename = "LZ09_F3.pf";
			} else if (name.equals("LZ4")) {
				filename = "LZ09_F4.pf";
			} else if (name.equals("LZ5")) {
				filename = "LZ09_F5.pf";
			} else if (name.equals("LZ6")) {
				filename = "LZ09_F6.pf";
			} else if (name.equals("LZ7")) {
				filename = "LZ09_F7.pf";
			} else if (name.equals("LZ8")) {
				filename = "LZ09_F8.pf";
			} else if (name.equals("LZ9")) {
				filename = "LZ09_F9.pf";
			} else if (name.equalsIgnoreCase("Belegundu")) {
				filename = "Belegundu.pf";
			} else if (name.equalsIgnoreCase("Binh")) {
				filename = "Binh.pf";
			} else if (name.equalsIgnoreCase("Binh2")) {
				filename = "Binh2.pf";
			} else if (name.equalsIgnoreCase("Binh3")) {
				filename = "Binh3.pf";
			} else if (name.equalsIgnoreCase("Binh4")) {
				filename = "Binh4.pf";
			} else if (name.equalsIgnoreCase("Fonseca")) {
				filename = "Fonseca.pf";
			} else if (name.equalsIgnoreCase("Fonseca2")) {
				filename = "Fonseca2.pf";
			} else if (name.equalsIgnoreCase("Jimenez")) {
				filename = "Jimenez.pf";
			} else if (name.equalsIgnoreCase("Kita")) {
				filename = "Kita.pf";
			} else if (name.equalsIgnoreCase("Kursawe")) {
				filename = "Kursawe.pf";
			} else if (name.equalsIgnoreCase("Laumanns")) {
				filename = "Laumanns.pf";
			} else if (name.equalsIgnoreCase("Lis")) {
				filename = "Lis.pf";
			} else if (name.equalsIgnoreCase("Murata")) {
				filename = "Murata.pf";
			} else if (name.equalsIgnoreCase("Obayashi")) {
				filename = "Obayashi.pf";
			} else if (name.equalsIgnoreCase("OKA1")) {
				filename = "OKA1.pf";
			} else if (name.equalsIgnoreCase("OKA2")) {
				filename = "OKA2.pf";
			} else if (name.equalsIgnoreCase("Osyczka")) {
				filename = "Osyczka.pf";
			} else if (name.equalsIgnoreCase("Osyczka2")) {
				filename = "Osyczka2.pf";
			} else if (name.equalsIgnoreCase("Poloni")) {
				filename = "Poloni.pf";
			} else if (name.equalsIgnoreCase("Quagliarella")) {
				filename = "Quagliarella.pf";
			} else if (name.equalsIgnoreCase("Rendon")) {
				filename = "Rendon.pf";
			} else if (name.equalsIgnoreCase("Rendon2")) {
				filename = "Rendon2.pf";
			} else if (name.equalsIgnoreCase("Schaffer")) {
				filename = "Schaffer.pf";
			} else if (name.equalsIgnoreCase("Schaffer2")) {
				filename = "Schaffer2.pf";
			} else if (name.equalsIgnoreCase("Srinivas")) {
				filename = "Srinivas.pf";
			} else if (name.equalsIgnoreCase("Tamaki")) {
				filename = "Tamaki.pf";
			} else if (name.equalsIgnoreCase("Tanaka")) {
				filename = "Tanaka.pf";
			} else if (name.equalsIgnoreCase("Viennet")) {
				filename = "Viennet.pf";
			} else if (name.equalsIgnoreCase("Viennet2")) {
				filename = "Viennet2.pf";
			} else if (name.equalsIgnoreCase("Viennet3")) {
				filename = "Viennet3.pf";
			} else if (name.equalsIgnoreCase("Viennet4")) {
				filename = "Viennet4.pf";
			} else {
				return null;
			}
			
			return loadReferenceSet("pf/" + filename);
		} catch (NumberFormatException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	private NondominatedPopulation loadReferenceSet(String resource)
			throws IOException {
		File file = new File(resource);
		
		if (file.exists()) {
			return new NondominatedPopulation(PopulationIO.readObjectives(
					file));
		} else {
			InputStream input = getClass().getResourceAsStream("/" + resource);
			
			if (input == null) {
				throw new FileNotFoundException(resource);
			} else {
				try {
					return new NondominatedPopulation(
							PopulationIO.readObjectives(new CommentedLineReader(
									new InputStreamReader(input))));
				} finally {
					input.close();
				}
			}
		}
	}

}

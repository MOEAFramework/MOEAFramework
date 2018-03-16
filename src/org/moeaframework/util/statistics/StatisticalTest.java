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
package org.moeaframework.util.statistics;

/**
 * Interface for statistical hypothesis testing methods.
 */
public interface StatisticalTest {

	/**
	 * Returns {@code true} if the null hypothesis is rejected; {@code false}
	 * otherwise. The meaning of the null hypothesis and alternative hypothesis
	 * depends on the specific test.
	 * <p>
	 * The prespecified level of confidence, alpha, can be used for either
	 * one-tailed or two-tailed (directional or nondirectional) distributions,
	 * depending on the specific test. Some tests may only support specific
	 * values for alpha.
	 * 
	 * @param alpha the prespecified level of confidence
	 * @return {@code true} if the null hypothesis is rejected; {@code false}
	 *         otherwise
	 */
	public boolean test(double alpha);

}

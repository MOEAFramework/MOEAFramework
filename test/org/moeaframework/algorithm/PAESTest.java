/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.algorithm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.Problem;

@RunWith(CIRunner.class)
@Retryable
public class PAESTest extends JMetalAlgorithmTest {
	
	public PAESTest() {
		super("PAES", true);
	}
	
	@Test
	public void testConfiguration() {
		Problem problem = new MockRealProblem(2);
		PAES algorithm = new PAES(problem);
		
		Assert.assertEquals(algorithm.getArchive().getCapacity(), algorithm.getConfiguration().getInt("archiveSize"));
		Assert.assertEquals(algorithm.getArchive().getBisections(), algorithm.getConfiguration().getInt("bisections"));
		
		TypedProperties properties = new TypedProperties();
		properties.setInt("archiveSize", 200);
		properties.setInt("bisections", 3);
		
		algorithm.applyConfiguration(properties);
		Assert.assertEquals(200, algorithm.getArchive().getCapacity());
		Assert.assertEquals(3, algorithm.getArchive().getBisections());
	}

}

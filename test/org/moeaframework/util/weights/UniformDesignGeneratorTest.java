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
package org.moeaframework.util.weights;

import org.apache.commons.math3.primes.Primes;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link UniformDesignGenerator} class.
 */
public class UniformDesignGeneratorTest extends WeightGeneratorTest {
	
	@Test
	public void testPrimes() {
		int[] primes = new UniformDesignGenerator(3, 100)
				.generateFirstKPrimes(10);
		
		for (int prime : primes) {
			Assert.assertTrue(Primes.isPrime(prime));
		}
	}
	
	@Test
	public void test() {
		test(new UniformDesignGenerator(3, 100), 3);
		test(new UniformDesignGenerator(5, 100), 5);
		test(new UniformDesignGenerator(10, 100), 10);
	}

}

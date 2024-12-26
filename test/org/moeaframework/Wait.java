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
package org.moeaframework;

import java.time.Duration;
import java.util.function.BooleanSupplier;

import org.moeaframework.util.DurationUtils;

public class Wait {
	
	private Wait() {
		super();
	}
	
	public static void spinFor(Duration duration) {
		final long waitTime = DurationUtils.toNanoseconds(duration);
		final long startTime = System.nanoTime();
		
		spinUntil(() -> System.nanoTime() - startTime >= waitTime);
	}
	
	public static void spinUntil(BooleanSupplier condition) {
		while (!condition.getAsBoolean()) {
			// spin until condition is satisfied
		}
	}
	
	public static void sleepFor(Duration duration) {
		long waitTime = DurationUtils.toMilliseconds(duration);
		
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			Assert.fail("sleep was interrupted which may produce invalid test results");
		}
	}

}

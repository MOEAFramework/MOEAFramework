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
package org.moeaframework;

import java.time.Duration;
import java.util.function.BooleanSupplier;

/**
 * A "spin lock", which waits for some condition in a tight loop.  This results in a few key differences over other
 * locking constructs:
 * <ol>
 *   <li>A spin lock can not be interrupted, so no {@link InterruptedException} is thrown.
 *   <li>A spin lock typically has finer-grained resolution than {@link Thread#sleep(Duration)} or other wait methods.
 *   <li>A spin lock will consume the current thread and CPU.
 * </ol>
 */
public class SpinLock {
	
	private SpinLock() {
		super();
	}
	
	public static void waitFor(Duration duration) {
		final long waitTime = duration.getSeconds() * 1_000_000_000 + duration.getNano();
		final long startTime = System.nanoTime();
		
		waitUntil(() -> System.nanoTime() - startTime >= waitTime);
	}
	
	public static void waitUntil(BooleanSupplier condition) {
		while (!condition.getAsBoolean()) {
			// spin
		}
	}

}

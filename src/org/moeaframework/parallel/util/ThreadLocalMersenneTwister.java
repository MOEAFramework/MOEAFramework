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
package org.moeaframework.parallel.util;

import java.security.SecureRandom;
import java.util.Random;
import org.apache.commons.math3.random.MersenneTwister;

/**
 * A thread-safe version of the {@link MersenneTwister} random number
 * generator (RNG).  Each thread is assigned a unique instance of the 
 * RNG with its own seed.  To use:
 * <pre>
 *     PRNG.setRandom(ThreadLocalMersenneTwister.getInstance());
 * </pre>
 * <p>
 * Note: Java also has the {@see java.util.concurrent.ThreadLocalRandom}
 * that can provide similar access to random numbers across threads.
 * The Mersenne Twister, however, has better characteristics
 * (extremely long period, high equidistribution, etc.).
 */
public class ThreadLocalMersenneTwister extends Random {

	private static final long serialVersionUID = -4586969514356530381L;

	/**
	 * A RNG used to seed each thread's RNG.
	 */
	private static Random SEEDER;
	
	/**
	 * A singleton instance of this class.
	 */
	private static ThreadLocalMersenneTwister INSTANCE;
	
	/**
	 * Local RNGs for each thread.
	 */
	private static ThreadLocal<MersenneTwister> LOCAL_RANDOM;
	
	static {
		SEEDER = new SecureRandom();

		LOCAL_RANDOM = new ThreadLocal<MersenneTwister>() {

			@Override
			protected MersenneTwister initialValue() {
				synchronized (SEEDER) {
					return new MersenneTwister(SEEDER.nextLong());
				}
			}
			
		};
		
		INSTANCE = new ThreadLocalMersenneTwister();
	}

	/**
	 * Constructs a new thread-safe Mersenne Twister instance.
	 */
	private ThreadLocalMersenneTwister() {
		super();
	}
	
	/**
	 * Returns the singleton instance of this synchronized
	 * Mersenne Twister RNG.
	 * 
	 * @return the singleton instance
	 */
	public static ThreadLocalMersenneTwister getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Returns the RNG assigned to the current thread.  This RNG is
	 * not synchronized and is not thread-safe!
	 * 
	 * @return the local RNG for the current thread
	 */
	private MersenneTwister current() {
		return LOCAL_RANDOM.get();
	}

	@Override
	public synchronized void setSeed(long seed) {
		current().setSeed(seed);
	}

	@Override
	public void nextBytes(byte[] bytes) {
		current().nextBytes(bytes);
	}

	@Override
	public int nextInt() {
		return current().nextInt();
	}

	@Override
	public int nextInt(int n) {
		return current().nextInt(n);
	}

	@Override
	public long nextLong() {
		return current().nextLong();
	}

	@Override
	public boolean nextBoolean() {
		return current().nextBoolean();
	}

	@Override
	public float nextFloat() {
		return current().nextFloat();
	}

	@Override
	public double nextDouble() {
		return current().nextDouble();
	}

	@Override
	public double nextGaussian() {
		return current().nextGaussian();
	}

}

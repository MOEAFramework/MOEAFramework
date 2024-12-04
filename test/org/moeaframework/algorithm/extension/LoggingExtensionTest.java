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
package org.moeaframework.algorithm.extension;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.mock.MockRealProblem;

public class LoggingExtensionTest {

	@Test
	public void test() throws IOException {
		Logger logger = Logger.getLogger("test");
		TestHandler handler = new TestHandler();
		
	    handler.setLevel(Level.ALL);
	    logger.setUseParentHandlers(false);
	    logger.addHandler(handler);
	    logger.setLevel(Level.ALL);

		NSGAII algorithm = new NSGAII(new MockRealProblem(2));
		algorithm.addExtension(new LoggingExtension(logger, Duration.ofSeconds(0)));
		
		Assert.assertNotNull(handler.lastRecord);
		Assert.assertStringContains(handler.lastRecord.getMessage(), "starting");
		Assert.assertEquals("NSGA-II", handler.lastRecord.getParameters()[0]);
		
		algorithm.step();
		
		Assert.assertNotNull(handler.lastRecord);
		Assert.assertStringContains(handler.lastRecord.getMessage(), "running");
		Assert.assertEquals("NSGA-II", handler.lastRecord.getParameters()[0]);
		Assert.assertEquals(100, handler.lastRecord.getParameters()[1]);
		
		algorithm.terminate();
		
		Assert.assertNotNull(handler.lastRecord);
		Assert.assertStringContains(handler.lastRecord.getMessage(), "finished");
		Assert.assertEquals("NSGA-II", handler.lastRecord.getParameters()[0]);
		Assert.assertEquals(100, handler.lastRecord.getParameters()[1]);
		
		LoggingExtension.severe(algorithm, "custom message");
		Assert.assertNotNull(handler.lastRecord);
		Assert.assertStringContains(handler.lastRecord.getMessage(), "custom message");
	}
	
	@Test
	public void testNoLogger() throws IOException {
		NSGAII algorithm = new NSGAII(new MockRealProblem(2));
		LoggingExtension.severe(algorithm, "custom message");
	}

	private class TestHandler extends Handler {
		
		private LogRecord lastRecord;

		@Override
		public void publish(LogRecord record) {
			lastRecord = record;
		}

		@Override
		public void close() {}
		@Override
		public void flush() {}
	}

}

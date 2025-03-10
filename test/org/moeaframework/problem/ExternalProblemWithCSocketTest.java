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
package org.moeaframework.problem;

import java.io.File;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.ExternalProblem.Builder;
import org.moeaframework.util.Timer;

public class ExternalProblemWithCSocketTest extends ExternalProblemWithCStdioTest {
	
	@Override
	public Builder createBuilder() {
		Assume.assumePOSIX();
		
		File executable = getExecutable("test_socket.exe");
		
		return new Builder()
				.withCommand(executable.toString())
				.withSocket("127.0.0.1", ExternalProblem.DEFAULT_PORT);
	}
	
	@Test
	public void testFailAfterRetries() {
		Builder builder = new Builder()
				.withSocket(ExternalProblem.DEFAULT_PORT)
				.withDebugging();
		
		try (TestExternalProblem problem = new TestExternalProblem(builder)) {
			Solution solution = problem.newSolution();
			
			// The following should take at least retryAttempts * retryDelay
			Timer timer = Timer.startNew();
			
			try {
				problem.evaluate(solution);
			} catch (ProblemException e) {
				Assert.assertTrue(e.getCause() instanceof ConnectException);
			}
			
			Assert.assertGreaterThanOrEqual(timer.stop(), 5.0);
			
			Assert.assertNull(problem.getInstance().getProcess());
			Assert.assertTrue(problem.getInstance().getSocket() == null || problem.getInstance().getSocket().isClosed());
		}
	}
	
	@Test
	public void testFailAfterRetriesWithProcess() throws InterruptedException {
		File executable = getExecutable("test_stdio.exe");
		
		Builder builder = new Builder()
				.withCommand(executable.toString())
				.withSocket(ExternalProblem.DEFAULT_PORT)
				.withDebugging();
		
		try (TestExternalProblem problem = new TestExternalProblem(builder)) {
			Solution solution = problem.newSolution();
			
			// The following should take at least retryAttempts * retryDelay + shutdownTimeout
			Timer timer = Timer.startNew();
			
			try {
				problem.evaluate(solution);
			} catch (ProblemException e) {
				Assert.assertTrue(e.getCause() instanceof ConnectException);
			}
			
			Assert.assertGreaterThanOrEqual(timer.stop(), 5.0);
			
			Assert.assertNotNull(problem.getInstance().getProcess());
			Assert.assertTrue(problem.getInstance().getProcess().isAlive());
			Assert.assertTrue(problem.getInstance().getSocket() == null || problem.getInstance().getSocket().isClosed());
			problem.close();
			Assert.assertTrue(problem.getInstance().getProcess().waitFor(10, TimeUnit.SECONDS));
		}
	}

}

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
package org.moeaframework;

import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * JUnit runner designed to handle some compatibility issues when using an
 * automated continuous integration service, such as Travis CI.  Specifically
 * enables the use of the {@link RetryOnTravis} and {@link IgnoreOnTravis} annotations.
 */
public class TravisRunner extends BlockJUnit4ClassRunner {

	public TravisRunner(Class<?> type) throws InitializationError {
		super(type);
	}

	public boolean isRunningOnTravis() {
		return Boolean.parseBoolean(System.getProperty("ON_TRAVIS", "false"));
	}

	@Override
	public void run(RunNotifier arg0) {
		isRunningOnTravis();
		super.run(arg0);
	}

	@Override
	protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
		Description description = describeChild(method);

		if (method.getAnnotation(Ignore.class) != null) {
			notifier.fireTestIgnored(description);
		} else if ((method.getAnnotation(IgnoreOnTravis.class) != null ||
				getTestClass().getJavaClass().getAnnotation(IgnoreOnTravis.class) != null) &&
				isRunningOnTravis()) {
			System.out.println("Ignoring " + description.getDisplayName() + " on Travis CI");
			notifier.fireTestIgnored(description);
		} else if (method.getAnnotation(RetryOnTravis.class) != null &&
				isRunningOnTravis()) {
			runTestUnit(methodBlock(method), description, notifier,
					method.getAnnotation(RetryOnTravis.class).value());
		} else if (getTestClass().getJavaClass().getAnnotation(RetryOnTravis.class) != null &&
				isRunningOnTravis()) {
			runTestUnit(methodBlock(method), description, notifier,
					getTestClass().getJavaClass().getAnnotation(RetryOnTravis.class).value());
		} else {
			super.runChild(method, notifier);
		}
	}

	protected final void runTestUnit(Statement statement,
			Description description, RunNotifier notifier, int retries) {
		EachTestNotifier eachTestNotifier = new EachTestNotifier(notifier,
				description);
		eachTestNotifier.fireTestStarted();

		try {
			statement.evaluate();
		} catch (AssumptionViolatedException e) {
			eachTestNotifier.addFailedAssumption(e);
		} catch (Throwable e) {
			retry(eachTestNotifier, statement, description, e, retries);
		} finally {
			eachTestNotifier.fireTestFinished();
		}
	}

	public void retry(EachTestNotifier notifier, Statement statement,
			Description description, Throwable currentThrowable, int retries) {
		Throwable caughtThrowable = currentThrowable;
		int failedAttempts = 1;

		while (retries > failedAttempts) {
			try {
				System.out.println(description.getDisplayName() +
						" failed, retrying (attempt " + failedAttempts +
						" of " + retries + ")");
				statement.evaluate();
				System.out.println(description.getDisplayName() +
						" was successful after retry");
				return;
			} catch (Throwable t) {
				failedAttempts++;
				caughtThrowable = t;
			}
		}

		System.out.println(description.getDisplayName() + " failed after " +
				failedAttempts + " attempts");
		notifier.addFailure(caughtThrowable);
	}
}

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

import java.lang.annotation.Annotation;

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
 * Custom JUnit runner that enables some custom annotations on tests, including {@link Retryable}, {@link Flaky}, and
 * {@link IgnoreOnCI}, when running in an automated continuous integration (CI) environment, including GitHub Actions
 * and Travis CI.
 */
public class CIRunner extends BlockJUnit4ClassRunner {

	public CIRunner(Class<?> type) throws InitializationError {
		super(type);
	}

	private <T extends Annotation> T getAnnotation(final FrameworkMethod method, Class<T> annotationType) {
		T annotation = method.getAnnotation(annotationType);
		
		if (annotation == null) {
			annotation = getTestClass().getJavaClass().getAnnotation(annotationType);
		}
		
		return annotation;
	}
	
	private <T extends Annotation> boolean hasAnnotation(final FrameworkMethod method, Class<T> annotationType) {
		return getAnnotation(method, annotationType) != null;
	}

	@Override
	protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
		Description description = describeChild(method);
		boolean isRunningOnCI = TestEnvironment.isCI();

		if (hasAnnotation(method, Ignore.class)) {
			notifier.fireTestIgnored(description);
			return;
		}
		
		if (!TestEnvironment.getProperty("ALL_TESTS", false)) {
			if (hasAnnotation(method, IgnoreOnCI.class) && isRunningOnCI) {
				System.out.println("Ignoring " + description.getDisplayName() + ", annotated with @" +
						IgnoreOnCI.class.getSimpleName());
				notifier.fireTestIgnored(description);
				return;
			}
			
			if (hasAnnotation(method, Slow.class)) {
				System.out.println("Ignoring " + description.getDisplayName() + ", annotated with @" +
						Slow.class.getSimpleName());
				notifier.fireTestIgnored(description);
				return;
			}
		}
			
		int retries = 0;
		boolean flaky = isRunningOnCI && hasAnnotation(method, Flaky.class);
		
		Retryable retryable = getAnnotation(method, Retryable.class);
		
		if (retryable != null) {
			retries = retryable.value();
		}
			
		runTestUnit(methodBlock(method), description, notifier, retries, flaky);
	}

	protected final void runTestUnit(Statement statement, Description description, RunNotifier notifier, int retries,
			boolean flaky) {
		EachTestNotifier eachTestNotifier = new EachTestNotifier(notifier, description);
		eachTestNotifier.fireTestStarted();

		try {
			statement.evaluate();
		} catch (AssumptionViolatedException e) {
			eachTestNotifier.addFailedAssumption(e);
		} catch (Throwable e) {
			if (retries == 0) {
				if (flaky) {
					fireFlakyTest(description, e);
				} else {
					eachTestNotifier.addFailure(e);
				}
			} else {
				retry(eachTestNotifier, statement, description, e, retries, flaky);
			}
		} finally {
			eachTestNotifier.fireTestFinished();
		}
	}

	public void retry(EachTestNotifier notifier, Statement statement, Description description,
			Throwable currentThrowable, int retries, boolean flaky) {
		Throwable caughtThrowable = currentThrowable;
		int failedAttempts = 1;

		while (retries > failedAttempts) {
			try {
				System.out.println(description.getDisplayName() + " failed, retrying (attempt " + failedAttempts +
						" of " + retries + ")");
				statement.evaluate();
				System.out.println(description.getDisplayName() + " was successful after retry");
				return;
			} catch (AssumptionViolatedException e) {
				notifier.addFailedAssumption(e);
				break;
			} catch (Throwable t) {
				failedAttempts++;
				caughtThrowable = t;
			}
		}

		if (flaky) {
			fireFlakyTest(description, caughtThrowable);
		} else {
			System.out.println(description.getDisplayName() + " failed after " + failedAttempts + " attempts");
			notifier.addFailure(caughtThrowable);
		}
	}
	
	protected void fireFlakyTest(Description description, Throwable e) {
		System.out.println(description.getDisplayName() + " failed. This test is flaky and will be ignored!");
		System.out.println(e.toString());
	}
}

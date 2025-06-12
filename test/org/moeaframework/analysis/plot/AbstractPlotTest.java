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
package org.moeaframework.analysis.plot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Assume;
import org.moeaframework.util.mvc.UI;

/**
 * Abstract test class for plotting, which skips tests when no UI is available and closes any open plot windows.
 */
@Ignore("Abstract test class")
public abstract class AbstractPlotTest {
	
	@Before
	public void setUp() {
		Assume.assumeHasDisplay();
	}
	
	@After
	public void tearDown() {
		if (isJUnitTest()) {
			UI.disposeAll();
		}
	}
	
	public void runAll() throws Exception  {
		Constructor<? extends AbstractPlotTest> constructor = ConstructorUtils.getAccessibleConstructor(getClass());
		AbstractPlotTest instance = constructor.newInstance();
		
		for (Method method : MethodUtils.getMethodsWithAnnotation(getClass(), Test.class)) {
			if (!ClassUtils.isAssignable(method.getAnnotation(Test.class).expected(), Test.None.class)) {
				continue;
			}
			
			method.invoke(instance);
		}
	}

	public static boolean isJUnitTest() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		for (StackTraceElement element : stackTrace) {
			if (element.getClassName().startsWith("org.junit.")) {
				return true;
			}
		}

		return false;
	}

}

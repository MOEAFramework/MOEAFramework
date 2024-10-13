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
package org.moeaframework.util;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.Capture;
import org.moeaframework.Capture.CaptureResult;
import org.moeaframework.core.FrameworkException;

public class ErrorHandlerTest {

	@Test
	public void testDefaults() throws IOException {
		CaptureResult result = Capture.stream(stream -> {
			ErrorHandler errorHandler = new ErrorHandler();
			errorHandler.setMessageStream(stream);
			errorHandler.warn("test warning");
			errorHandler.error("test error");
		});
		
		result.assertEqualsNormalized("test warning\ntest error");
	}
	
	@Test
	public void testDuplicateMessagesSuppressed() throws IOException {
		CaptureResult result = Capture.stream(stream -> {
			ErrorHandler errorHandler = new ErrorHandler();
			errorHandler.setMessageStream(stream);
			errorHandler.setSuppressDuplicates(true);
			errorHandler.warn("test warning");
			errorHandler.error("test error");
			errorHandler.warn("test warning");
		});
		
		result.assertEqualsNormalized("test warning\ntest error");
	}
	
	@Test
	public void testErrorFlag() throws IOException {
		ErrorHandler errorHandler = new ErrorHandler();
		errorHandler.warn("test warning");
		Assert.assertFalse(errorHandler.isError());
		
		errorHandler.error("test error");
		Assert.assertTrue(errorHandler.isError());
		
		errorHandler.reset();
		Assert.assertFalse(errorHandler.isError());
	}
	
	@Test(expected = FrameworkException.class)
	public void testExceptionOnWarn() throws IOException {
		ErrorHandler errorHandler = new ErrorHandler();
		errorHandler.setWarningsAreFatal(true);
		errorHandler.warn("test warning");
	}
	
	@Test(expected = FrameworkException.class)
	public void testExceptionOnError() throws IOException {
		ErrorHandler errorHandler = new ErrorHandler();
		errorHandler.setErrorsAreFatal(true);
		errorHandler.error("test error");
	}
	
}

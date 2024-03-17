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
package org.moeaframework.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.CIRunner;
import org.moeaframework.IgnoreOnCI;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;

/**
 * Tests the {@link RedirectStream} class.
 */
@RunWith(CIRunner.class)
public class RedirectStreamTest {
	
	/**
	 * Returns the data used to test the redirect stream.
	 * 
	 * @return the data used to test the redirect stream
	 */
	private byte[] generateRandomData() {
		byte[] data = new byte[10*Settings.BUFFER_SIZE+271];
		PRNG.getRandom().nextBytes(data);
		return data;
	}

	/**
	 * Tests stream redirection to ensure the contents are copied correctly and the thread is terminated when completed.
	 */
	@Test
	public void testRedirection() throws IOException, InterruptedException {
		byte[] bytes = generateRandomData();
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		RedirectStream.redirect(is, os);

		Thread.sleep(1000); // give the redirection time to work

		Assert.assertArrayEquals(bytes, os.toByteArray());
		Assert.assertEquals(-1, is.read()); // ensure all data is read

		// ensure the thread stopped
		Thread[] threads = new Thread[Thread.activeCount()];
		Thread.enumerate(threads);

		for (Thread thread : threads) {
			if (thread instanceof RedirectStream) {
				Assert.fail("thread still active");
			}
		}
	}

	/**
	 * Tests stream redirection to ensure the contents are cleared correctly and the thread is terminated when
	 * completed.
	 */
	@Test
	public void testNullRedirection() throws IOException, InterruptedException {
		byte[] bytes = generateRandomData();
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);

		RedirectStream.redirect(is);

		Thread.sleep(1000); // give the redirection time to work

		Assert.assertEquals(-1, is.read()); // ensure all data is read

		// ensure the thread stopped
		Thread[] threads = new Thread[Thread.activeCount()];
		Thread.enumerate(threads);

		for (Thread thread : threads) {
			if (thread instanceof RedirectStream) {
				Assert.fail("thread still active");
			}
		}
	}
	
	@Test
	@IgnoreOnCI("failing on Java < 16 in CI but works when running on Windows and Ubuntu locally")
	public void testCapture() throws IOException, InterruptedException {
		Assume.assumeTrue("Test does not work on earlier version of Java in CI",
				SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_16));
		
		ProcessBuilder processBuilder = SystemUtils.IS_OS_WINDOWS ?
				new ProcessBuilder("cmd", "/C", "echo hello world") :
				new ProcessBuilder("echo", "hello world");
				
		Assert.assertEquals("hello world", RedirectStream.capture(processBuilder).trim());
	}
	
	@Test(expected = IOException.class)
	public void testCaptureWithInvalidCommand() throws IOException, InterruptedException {
		RedirectStream.capture(new ProcessBuilder("invalidCommand"));		
	}

}

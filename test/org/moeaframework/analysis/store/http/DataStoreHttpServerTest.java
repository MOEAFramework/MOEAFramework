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
package org.moeaframework.analysis.store.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.fs.FileSystemDataStore;
import org.moeaframework.util.DurationUtils;

public class DataStoreHttpServerTest {
	
	private static final Duration TIMEOUT = Duration.ofSeconds(5);

	@Test
	public void test() throws IOException, InterruptedException {
		File tempDirectory = TempFiles.createDirectory();
		DataStore dataStore = new FileSystemDataStore(tempDirectory);

		Reference reference = Reference.of("foo", "bar");
		Container container = dataStore.getContainer(reference);
		Blob blob = container.getBlob("baz");

		blob.storeText("Hello world!");

		DataStoreHttpServer server = new DataStoreHttpServer();

		try {
			assertStart();
			
			Assert.assertEquals("OK", get("http://localhost:8080/_health"));
			
			// Requests should 404 if no data store is configured
			Assert.assertNull(get("http://localhost:8080/test"));
			Assert.assertNull(get("http://localhost:8080/test?foo=bar"));
			Assert.assertNull(get("http://localhost:8080/test?foo=bar&_name=baz"));
			
			// Register data store
			server.configure("test", dataStore);
						
			Assert.assertEquals(
					"{\"type\":\"container\",\"uri\":\"\\/test?foo=bar\",\"reference\":{\"foo\":\"bar\"},\"blobs\":[{\"type\":\"blob\",\"name\":\"baz\",\"uri\":\"\\/test?foo=bar&_name=baz\"}]}" + System.lineSeparator(),
					get("http://localhost:8080/test?foo=bar"));
			
			Assert.assertEquals("Hello world!", get("http://localhost:8080/test?foo=bar&_name=baz"));
			
			Assert.assertNull(get("http://localhost:8080/missing"));
			Assert.assertNull(get("http://localhost:8080/test?foo=missing"));
			Assert.assertNull(get("http://localhost:8080/test?foo=bar&_name=missing"));
		} finally {
			server.shutdown();
		}
		
		assertShutdown();
	}
	
	private void assertStart() throws InterruptedException, IOException {
		StopWatch stopwatch = StopWatch.create();
		stopwatch.start();
		
		do {
			try {
				get("http://localhost:8080/_health");
				return;
			} catch (ConnectException e) {
				// retry
			}
		} while (stopwatch.getDuration().compareTo(TIMEOUT) <= 0);
		
		Assert.fail("Server failed to start");
	}
	
	private void assertShutdown() throws InterruptedException, IOException {
		StopWatch stopwatch = StopWatch.create();
		stopwatch.start();
		
		do {
			try {
				get("http://localhost:8080/_health");
			} catch (ConnectException e) {
				return;
			}
		} while (stopwatch.getDuration().compareTo(TIMEOUT) <= 0);
		
		Assert.fail("Server failed to shut down");
	}

	private String get(String path) throws IOException {
		HttpURLConnection connection = null;

		try {
			URL url = URI.create(path).toURL();
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout((int)(DurationUtils.toMilliseconds(TIMEOUT)));
			connection.setReadTimeout((int)(DurationUtils.toMilliseconds(TIMEOUT)));
			
			connection.connect();

			if (connection.getResponseCode() == 200) {
				try (InputStream in = connection.getInputStream()) {
					return new String(in.readAllBytes(), StandardCharsets.UTF_8);
				}
			} else if (connection.getResponseCode() != 404) {
				Assert.fail("Unexpected response status " + connection.getResponseCode());
			}
			
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

}

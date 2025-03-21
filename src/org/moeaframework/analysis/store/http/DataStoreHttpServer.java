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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.DataStoreURI;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.io.OutputHandler;
import org.moeaframework.util.validate.Validate;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Provides read-only HTTP access to data stores.
 * <p>
 * <strong>This server uses unsecured HTTP connections.</strong>  Use caution when running on a publicly-accessible
 * network.  This is primarily intended for testing and debugging purposes.
 */
public class DataStoreHttpServer {
	
	/**
	 * Shutdown delay to allow any active requests to finish.
	 */
	private static final Duration SHUTDOWN_DELAY = Duration.ofSeconds(1);
		
	private final Logger logger;
			
	private final HttpServer server;
	
	/**
	 * Creates a new HTTP server to provide read-only access data stores.
	 * 
	 * @throws IOException if an error occurred starting the server, such as {@link java.net.BindException}
	 */
	public DataStoreHttpServer() throws IOException {
		this(new InetSocketAddress("localhost", 8080));
	}
	
	/**
	 * Creates a new HTTP server to provide read-only access data stores.
	 * 
	 * @param address the socket address the server binds to
	 * @throws IOException if an error occurred starting the server, such as {@link java.net.BindException}
	 */
	public DataStoreHttpServer(InetSocketAddress address) throws IOException {
		super();
		
		logger = OutputHandler.getLogger(DataStoreHttpServer.class.getSimpleName());
		
		server = HttpServer.create(address, 0);
		server.createContext("/_health", new HealthHttpHandler(logger));
		
		logger.info("Starting server!");
		server.start();
	}
	
	/**
	 * Associates the given URI path with the data store.
	 * 
	 * @param path the URI path that directs requests to this data store
	 * @param dataStore the data store
	 */
	public void configure(String path, DataStore dataStore) {
		// normalize path
		path = path.replace('\\', '/');
		path = (path.startsWith("/") ? "" : "/") + path;
		
		if (path.equalsIgnoreCase("_health")) {
			throw new IllegalArgumentException("_health is a reserved endpoint, try with a different path");
		}
				
		server.createContext(path, new DataStoreHttpHandler(dataStore, logger));
		
		logger.info("Configured data store at " + server.getAddress().getHostString() + ":" +
 				server.getAddress().getPort() + path);
	}
	
	/**
	 * Registers a shutdown hook to stop this server when the JVM shuts down.
	 */
	public void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
	}
	
	/**
	 * Shuts down this HTTP server.  This should either be called explicitly when the server is not longer in use, or
	 * by {@link #registerShutdownHook()}.
	 */
	public void shutdown() {
		logger.info("Shutting down server!");
		server.stop((int)(SHUTDOWN_DELAY.getSeconds()));
	}
	
	private static class HttpRequestContext {
		
		private static final int CHUNKED_RESPONSE = 0;
		
		private static final int EMPTY_RESPONSE = -1;
		
		private final UUID requestId;
		
		private final HttpExchange exchange;
		
		private final Logger logger;
		
		private HttpRequestContext(HttpExchange exchange, Logger logger) {
			super();
			this.exchange = exchange;
			this.logger = logger;
			
			this.requestId = UUID.randomUUID();
			exchange.getResponseHeaders().add("REQUEST_ID", requestId.toString());
						
			info("Start request from " + exchange.getRemoteAddress().getHostString());
			info(exchange.getRequestMethod() + " " + exchange.getRequestURI());
		}

		public static HttpRequestContext begin(HttpExchange exchange, Logger logger) {
			return new HttpRequestContext(exchange, logger);
		}
		
		public DataStoreURI getDataStoreURI() {
			return DataStoreURI.parse(exchange.getRequestURI().toString().substring(1));
		}
		
		public boolean isGet() {
			return exchange.getRequestMethod().equalsIgnoreCase("GET");
		}
		
		public void fail(int code) throws IOException {
			Validate.that("code", code).isGreaterThanOrEqualTo(300);
			
			exchange.sendResponseHeaders(code, EMPTY_RESPONSE);
			exchange.close();
			
			info("Status code " + code);
			info("End request");
		}
		
		public void setFileName(String filename) {
			exchange.getResponseHeaders().add("Content-Disposition", "inline; filename=\"" + filename + "\"");
		}
		
		public HttpOutputStream beginResponse(int code) throws IOException {
			return beginResponse(code, null);
		}
		
		public HttpOutputStream beginResponse(int code, String contentType) throws IOException {
			Validate.that("code", code).isBetween(200, 299);
			
			info("Status code " + code);
			
			exchange.getResponseHeaders().add("Content-Type",
					contentType != null ? contentType : "application/octet-stream");
			exchange.sendResponseHeaders(code, CHUNKED_RESPONSE);
			
			return new HttpOutputStream(this, exchange.getResponseBody());
		}
		
		public void severe(String message) {
			logger.info(requestId + ": " + message);
		}
		
		public void info(String message) {
			logger.info(requestId + ": " + message);
		}
		
	}
	
	
	private static class HttpOutputStream extends FilterOutputStream {
		
		private final HttpRequestContext context;
		
		public HttpOutputStream(HttpRequestContext context, OutputStream out) {
			super(out);
			this.context = context;
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			super.write(b, off, len);
			context.info("Sent " + len + " bytes");
		}

		@Override
		public void close() throws IOException {
			super.close();
			context.info("End request");
		}
		
	}

	private static class DataStoreHttpHandler implements HttpHandler {
		
		private final DataStore dataStore;
		
		private final Logger logger;
				
		public DataStoreHttpHandler(DataStore dataStore, Logger logger) {
			super();
			this.dataStore = dataStore;
			this.logger = logger;
		}

		public void handle(HttpExchange exchange) throws IOException {
			HttpRequestContext ctx = HttpRequestContext.begin(exchange, logger);
			
			try {
				DataStoreURI dsUri = ctx.getDataStoreURI();			
				
				Reference reference = dsUri.getReference();
				String blobName = dsUri.getName();
	
				ctx.info("Reference=" + reference);
				ctx.info("Name=" + blobName);
							
				if (ctx.isGet()) {
					if (blobName != null) {
						Blob blob = dataStore.getContainer(reference).getBlob(blobName);
						
						if (blob.exists()) {
							ctx.setFileName(blobName);
							
							try (OutputStream out = ctx.beginResponse(200)) {
								blob.extractTo(out);
							}
						} else {
							ctx.fail(404);
						}
					} else if (!reference.isRoot()) {
						Container container = dataStore.getContainer(reference);
						
						if (container.exists()) {
							try (PrintWriter out = new PrintWriter(ctx.beginResponse(200, "application/json"))) {
								out.println(container.toJSON(exchange.getRequestURI(), true));
							}
						} else {
							ctx.fail(404);
						}
					} else {					
						try (PrintWriter out = new PrintWriter(ctx.beginResponse(200, "application/json"))) {
							out.println(dataStore.toJSON(exchange.getRequestURI()));
						}
					}
				} else {
					ctx.fail(400);
				}
			} catch (FrameworkException e) {
				ctx.severe(ExceptionUtils.getStackTrace(e));
				ctx.fail(500);
			}
		}
		
	}
	
	private static class HealthHttpHandler implements HttpHandler {
		
		private final Logger logger;
		
		public HealthHttpHandler(Logger logger) {
			super();
			this.logger = logger;
		}

		public void handle(HttpExchange exchange) throws IOException {
			HttpRequestContext ctx = HttpRequestContext.begin(exchange, logger);
			
			try (OutputStream out = ctx.beginResponse(200)) {
				out.write("OK".getBytes(StandardCharsets.UTF_8));
			}
		}
		
	}

}

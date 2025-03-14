package org.moeaframework.analysis.store.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.text.StringEscapeUtils;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.DataStoreURI;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.util.Iterators;
import org.moeaframework.util.Iterators.IndexedValue;
import org.moeaframework.util.io.OutputHandler;
import org.moeaframework.util.validate.Validate;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Provides read-only HTTP access to a data store.  Note that this uses unsecured HTTP connections and is only intended
 * for debugging and testing purposes.
 */
public class DataStoreHttpServer {
	
	/**
	 * Since HTTP requests do not include the {@code #fragment} portion of a URL, pass it instead as part of the query.
	 */
	private static final String NAME_KEY = "__name";
		
	private final Logger logger;
	
	private final DataStore dataStore;
		
	private final HttpServer server;
	
	public DataStoreHttpServer(DataStore dataStore, String path) throws IOException {
		super();
		this.dataStore = dataStore;
		
		// normalize and make path absolute
		path = path.replace('\\', '/');

		if (!path.startsWith("/")) {
			path = "/" + path;
		}
				
		logger = OutputHandler.getLogger(DataStoreHttpServer.class.getSimpleName());
		server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
		server.createContext(path, new DataStoreHttpHandler());
		
		logger.info("Server configured at " + server.getAddress().getHostString() + ":" +
				server.getAddress().getPort() + path);
	}

	public void start() throws IOException {
		logger.info("Starting server!");
		server.start();
	}
	
	public void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
	}
	
	public void shutdown() {
		logger.info("Shutting down server!");
		server.stop(1);
	}
	
	/**
	 * Manages the lifecycle of a request, including associating a unique request id.
	 */
	private static class HttpRequestContext {
		
		private final UUID requestId;
		
		private final HttpExchange exchange;
		
		private final Logger logger;
		
		private HttpRequestContext(HttpExchange exchange, Logger logger) {
			super();
			this.exchange = exchange;
			this.logger = logger;
			
			this.requestId = UUID.randomUUID();
			exchange.getResponseHeaders().add("REQUEST_ID", requestId.toString());
			
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
			
			exchange.sendResponseHeaders(code, -1);
			exchange.close();
			
			info("FAIL (" + code + ")");
		}
		
		public void setFileName(String filename) {
			exchange.getResponseHeaders().add("Content-Disposition", "inline; filename=\"" + filename + "\"");
		}
		
		public HttpOutputStream beginResponse(int code) throws IOException {
			return beginResponse(code, null);
		}
		
		public HttpOutputStream beginResponse(int code, String contentType) throws IOException {
			Validate.that("code", code).isBetween(200, 299);
			
			info("Begin response");
			
			exchange.getResponseHeaders().add("Content-Type", contentType != null ? contentType : "application/octet-stream");
			exchange.sendResponseHeaders(code, 0); // 0 indicates chunked response
			
			return new HttpOutputStream(this, exchange.getResponseBody());
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
			context.info("End response");
		}
		
	}

	private class DataStoreHttpHandler implements HttpHandler {
				
		public DataStoreHttpHandler() {
			super();

		}

		public void handle(HttpExchange exchange) throws IOException {
			HttpRequestContext ctx = HttpRequestContext.begin(exchange, logger);
			
			//InputStream in = exchange.getRequestBody();
			DataStoreURI dsUri = ctx.getDataStoreURI();			
			TypedProperties query = dsUri.getQuery();
			String blobName = null;
			
			if (query.contains(NAME_KEY)) {
				blobName = query.getString(NAME_KEY);
				query.remove(NAME_KEY);
			}
			
			Reference reference = Reference.of(query);

			ctx.info("Reference=" + reference);
			ctx.info("Blob Name=" + blobName);
			
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
				} else if (!dsUri.getQuery().isEmpty()) {
					Container container = dataStore.getContainer(reference);
					
					if (container.exists()) {
						try (PrintWriter out = new PrintWriter(ctx.beginResponse(200, "application/json"))) {
							out.print("{");
							out.print("\"type\":\"container\",");
							out.print("\"url\":\"");
							out.print(StringEscapeUtils.escapeJson(toURL(exchange.getRequestURI(), container)));
							out.print("\",");
							out.print("\"reference\":{");
							
							for (IndexedValue<String> field : Iterators.enumerate(container.getReference().fields())) {
								if (field.getIndex() > 0) {
									out.print(",");
								}
								
								out.print("\"");
								out.print(StringEscapeUtils.escapeJson(field.getValue()));
								out.print("\":\"");
								out.print(StringEscapeUtils.escapeJson(container.getReference().get(field.getValue())));
								out.print("\"");
							}
							
							out.print("},");
							out.print("\"blobs\":[");
							
							for (IndexedValue<Blob> blob : Iterators.enumerate(container.listBlobs())) {
								if (blob.getIndex() > 0) {
									out.print(",");
								}
								
								out.print("{");
								out.print("\"type\":\"blob\",");
								out.print("\"name\":\"");
								out.print(StringEscapeUtils.escapeJson(blob.getValue().getName()));
								out.print("\",");
								out.print("\"url\":\"");
								out.print(StringEscapeUtils.escapeJson(toURL(exchange.getRequestURI(), blob.getValue())));
								out.print("\"");
								out.print("}");
							}
							
							out.print("]");
							out.print("}");
						}
					} else {
						ctx.fail(404);
					}
				} else {					
					try (PrintWriter out = new PrintWriter(ctx.beginResponse(200, "application/json"))) {
						out.print("{");
						out.print("\"type\":\"datastore\",");
						out.print("\"url\":\"");
						out.print(StringEscapeUtils.escapeJson(exchange.getRequestURI().getPath()));
						out.print("\",");
						out.print("\"blobs\":[");
						
						for (IndexedValue<Blob> blob : Iterators.enumerate(dataStore.getRootContainer().listBlobs())) {
							if (blob.getIndex() > 0) {
								out.print(",");
							}
							
							out.print("{");
							out.print("\"type\":\"blob\",");
							out.print("\"name\":\"");
							out.print(StringEscapeUtils.escapeJson(blob.getValue().getName()));
							out.print("\",");
							out.print("\"url\":\"");
							out.print(StringEscapeUtils.escapeJson(toURL(exchange.getRequestURI(), blob.getValue())));
							out.print("\"");
							out.print("}");
						}
						
						out.print("],");
						out.print("\"containers\":[");
						
						for (IndexedValue<Container> container : Iterators.enumerate(dataStore.listContainers())) {
							if (container.getIndex() > 0) {
								out.print(",");
							}
							
							out.print("{");
							out.print("\"type\":\"container\",");
							out.print("\"url\":\"");
							out.print(StringEscapeUtils.escapeJson(toURL(exchange.getRequestURI(), container.getValue())));
							out.print("\",");
							out.print("\"reference\":{");
							
							for (IndexedValue<String> field : Iterators.enumerate(container.getValue().getReference().fields())) {
								if (field.getIndex() > 0) {
									out.print(",");
								}
								
								out.print("\"");
								out.print(StringEscapeUtils.escapeJson(field.getValue()));
								out.print("\":\"");
								out.print(StringEscapeUtils.escapeJson(container.getValue().getReference().get(field.getValue())));
								out.print("\"");
							}
							
							out.print("}");
							out.print("}");
						}
						
						out.print("]");
						out.print("}");
					}
				}
			}
			
			ctx.fail(500);
		}
		
		private String toURL(URI requestUri, Container container) {
			StringBuilder sb = new StringBuilder();
			sb.append(requestUri.getPath());
			
			if (!container.getReference().isRoot()) {
				sb.append("?");
				sb.append(container.getURI().getQuery());
			}
			
			return sb.toString();
		}
		
		private String toURL(URI requestUri, Blob blob) {
			StringBuilder sb = new StringBuilder();
			sb.append(requestUri.getPath());
			sb.append("?");
			
			if (!blob.getContainer().getReference().isRoot()) {
				sb.append(blob.getURI().getQuery());
				sb.append("&");
			}
			
			sb.append(NAME_KEY);
			sb.append("=");
			sb.append(blob.getURI().getFragment());
			
			return sb.toString();
		}
		
	}

}

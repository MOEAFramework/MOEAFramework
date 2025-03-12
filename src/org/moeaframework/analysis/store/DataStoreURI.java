package org.moeaframework.analysis.store;

import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Displayable;
import org.moeaframework.util.format.TabularData;

/**
 * URI referencing a {@link DataStore} or its contents.
 */
public class DataStoreURI implements Displayable {
	
	/**
	 * The file scheme string constant.
	 */
	public static final String FILE_SCHEME = "file";
	
	/**
	 * The charset used to encode URIs.
	 */
	public static final Charset UTF8 = Charset.forName("UTF-8");
	
	private final URI uri;
	
	private DataStoreURI(URI uri) {
		super();
		this.uri = uri;
	}
	
	/**
	 * Returns the original URI.
	 * 
	 * @return the URI
	 */
	public URI getURI() {
		return uri;
	}
	
	/**
	 * Returns the scheme component of the URI, defaulting to {@value FILE_SCHEME} if not specified.
	 * 
	 * @return the scheme
	 */
	public String getScheme() {
		return uri.getScheme() == null ? FILE_SCHEME : uri.getScheme();
	}
	
	/**
	 * Returns the server component of the URI.  For {@value FILE_SCHEME} URIs, this returns {@code null}.  Otherwise,
	 * it contains the user info, host, and port.
	 * 
	 * @return the server component
	 */
	public String getServer() {
		if (isFileScheme(uri)) {
			return null;
		} else {
			return uri.getAuthority();
		}
	}
	
	/**
	 * Returns the path component of the URI.  An empty string is returned if no path is specified.
	 * 
	 * @return the path
	 */
	public Path getPath() {
		if (isFileScheme(uri) && uri.getAuthority() != null) {
			return Path.of(uri.getAuthority(), uri.getPath());
		} else {
			return Path.of(uri.getPath());
		}
	}
	
	/**
	 * Returns the query component of the URI, converted into a {@link TypedProperties}.
	 * 
	 * @return the query
	 */
	public TypedProperties getQuery() {
		TypedProperties query = new TypedProperties();
		
		if (uri.getQuery() != null) {
			for (String parameter : uri.getQuery().split("&")) {
				String[] parts = parameter.split("=", 2);
				query.setString(URLDecoder.decode(parts[0], UTF8), URLDecoder.decode(parts[1], UTF8));
			}
		}
		
		return query;
	}
	
	/**
	 * Returns the fragment component of the URI, or {@code null} if not specified.
	 * 
	 * @return the fragment
	 */
	public String getFragment() {
		return uri.getFragment();
	}

	public void display(PrintStream out) {
		Map<String, String> parts = new LinkedHashMap<>();
		parts.put("IsAbsolute", Boolean.toString(uri.isAbsolute()));
		parts.put("IsOpaque", Boolean.toString(uri.isOpaque()));
		parts.put("Scheme", uri.getScheme());
		parts.put("Scheme Specific Part", uri.getSchemeSpecificPart());
		parts.put("Authority", uri.getAuthority());
		parts.put("User Info", uri.getUserInfo());
		parts.put("Host", uri.getHost());
		parts.put("Port", Integer.toString(uri.getPort()));
		parts.put("Path", uri.getPath());
		parts.put("Query", uri.getQuery());
		parts.put("Fragment", uri.getFragment());
		
		TabularData<Map.Entry<String, String>> data = new TabularData<>(parts.entrySet());
		data.addColumn(new Column<Map.Entry<String, String>, String>("Field", x -> x.getKey()));
		data.addColumn(new Column<Map.Entry<String, String>, String>("Value", x -> x.getValue() == null ? "<null>" : x.getValue()));
		data.display(out);
	}
	
	@Override
	public String toString() {
		return uri.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getScheme())
				.append(getServer())
				.append(getPath())
				.append(getQuery())
				.append(getFragment())
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			DataStoreURI rhs = (DataStoreURI)obj;
			
			return new EqualsBuilder()
					.append(getScheme(), rhs.getScheme())
					.append(getServer(), rhs.getServer())
					.append(getPath(), rhs.getPath())
					.append(getQuery(), rhs.getQuery())
					.append(getFragment(), rhs.getFragment())
					.isEquals();
		}
	}
	
	/**
	 * Parses the given string as a URI.
	 * 
	 * @param str the string
	 * @return the URI
	 * @throws IllegalArgumentException if the string is not a valid URI
	 */
	public static DataStoreURI parse(String str) {
		return parse(URI.create(str));
	}
	
	/**
	 * Converts the given URI into a data store URI.
	 * 
	 * @param uri the URI
	 * @return the data store URI
	 */
	public static DataStoreURI parse(URI uri) {
		System.out.println("URI: " + uri);
		
		if (uri.isOpaque() && isFileScheme(uri)) {
			uri = URI.create(
				(uri.getScheme() == null ? "" : (uri.getScheme() + "://")) +
				uri.getSchemeSpecificPart() +
				(uri.getFragment() == null ? "" : "#" + uri.getFragment()));
		}
	
		DataStoreURI dsUri = new DataStoreURI(uri);
		dsUri.display(System.out);
		System.out.println();
		return dsUri;
	}
	
	/**
	 * Resolves the URI for the given container.
	 * 
	 * @param container the container
	 * @return the URI
	 */
	public static URI resolve(Container container) {
		URI baseUri = container.getDataStore().getURI();
		Reference reference = container.getReference();
		StringBuilder sb = new StringBuilder();
		
		for (String field : reference.fields()) {
			sb.append(URLEncoder.encode(field, UTF8));
			sb.append("=");
			sb.append(URLEncoder.encode(reference.get(field), UTF8));
		}
		
		try {
			return new URI(baseUri.getScheme(), baseUri.getAuthority(), baseUri.getPath(), sb.toString(), null);
		} catch (URISyntaxException e) {
			throw new DataStoreException("Invalid URI syntax for data store container", e);
		}
	}
	
	/**
	 * Resolves the URI for the given blob.
	 * 
	 * @param blob the blob
	 * @return the URI
	 */
	public static URI resolve(Blob blob) {
		URI containerUri = resolve(blob.getContainer());

		try {
			return new URI(containerUri.getScheme(), containerUri.getAuthority(), containerUri.getPath(),
					containerUri.getQuery(), URLEncoder.encode(blob.getName(), UTF8));
		} catch (URISyntaxException e) {
			throw new DataStoreException("Invalid URI syntax for data store blob", e);
		}
	}
	
	/**
	 * Resolves the URI for the given local path.
	 * 
	 * @param path the path
	 * @return the URI
	 */
	public static URI resolvePath(Path path) {
		return path.toUri();
	}
	
	/**
	 * Returns {@code true} if the given URI uses the {@value FILE_SCHEME} scheme.
	 * 
	 * @param uri the URI
	 * @return {@code true} if the given URI references a file; {@code false} otherwise
	 */
	private static boolean isFileScheme(URI uri) {
		return uri.getScheme() == null || uri.getScheme().equalsIgnoreCase(FILE_SCHEME);
	}

}

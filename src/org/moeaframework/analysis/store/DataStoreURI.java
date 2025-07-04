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
package org.moeaframework.analysis.store;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.TypedProperties;

/**
 * URI referencing a {@link DataStore} or its contents.
 */
public class DataStoreURI {
	
	/**
	 * The query parameter used to identify the blob name.
	 */
	public static final String NAME_PARAMETER = "_name";
	
	/**
	 * The file scheme string constant.
	 */
	public static final String FILE_SCHEME = "file";
	
	private final URI uri;
	
	private final String scheme;
	
	private final Path path;
	
	private final TypedProperties query;
	
	private final Reference reference;
	
	private final String name;
	
	private DataStoreURI(URI uri) {
		super();
		this.uri = uri;
		
		scheme = isFileScheme(uri) ? FILE_SCHEME : uri.getScheme();
		
		if (uri.getAuthority() != null && scheme.equals(FILE_SCHEME)) {
			path = Path.of(uri.getAuthority(), uri.getPath());
		} else if (uri.getPath().matches("^/[a-zA-Z]:.*")) {
			// Windows-style file URIs have a leading "/" in the path, for example, "file:///C:/path"
			path = Path.of(uri.getPath().substring(1));
		} else {
			path = Path.of(uri.getPath());
		}
		
		// Parse the query parameters, removing _name if specified
		query = new TypedProperties();
		
		if (uri.getQuery() != null) {
			for (String parameter : uri.getQuery().split("&")) {
				String[] parts = parameter.split("=", 2);
				
				if (parts.length < 2 || parts[0].isBlank()) {
					throw new IllegalArgumentException("Invalid query string '" + uri.getQuery() + "'");
				}
				
				String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
				String value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);

				query.setString(key, value);
			}
		}
		
		name = query.getString(NAME_PARAMETER, uri.getFragment());
		
		query.remove(NAME_PARAMETER);
		reference = Reference.of(query);
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
		return scheme;
	}
	
	/**
	 * Returns the server component of the URI.  For {@value FILE_SCHEME} URIs, this returns {@code null}.  Otherwise,
	 * it contains the user info, host, and port.
	 * 
	 * @return the server component
	 */
	public String getServer() {
		if (scheme.equals(FILE_SCHEME)) {
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
		return path;
	}
	
	/**
	 * Returns the query component of the URI, converted into a {@link TypedProperties}.  This is for testing purposes
	 * only.
	 * 
	 * @return the query
	 */
	TypedProperties getQuery() {
		return query.copy();
	}
	
	/**
	 * Returns the reference specified by the URI.
	 * 
	 * @return the reference
	 */
	public Reference getReference() {
		return reference;
	}
	
	/**
	 * Returns the blob name specified in the URI.  This can be defined in {@link URI#getFragment()} or using the
	 * {@value DataStoreURI#NAME_PARAMETER} query parameter.  The latter is recommended as the fragment part is
	 * typically excluded from HTTP requests.
	 * 
	 * @return the blob name, or {@code null} if not specified
	 */
	public String getName() {
		return name;
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
				.append(getName())
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
					.append(getName(), rhs.getName())
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
		if (uri.isOpaque() && isFileScheme(uri)) {
			uri = URI.create(
				(uri.getScheme() == null ? "" : (uri.getScheme() + "://")) +
				uri.getSchemeSpecificPart() +
				(uri.getFragment() == null ? "" : "#" + uri.getFragment()));
		}
	
		return new DataStoreURI(uri);
	}
	
	/**
	 * Resolves the URI for the given container.
	 * 
	 * @param container the container
	 * @return the URI
	 */
	public static URI resolve(Container container) {
		return resolve(container.getDataStore().getURI(), container);
	}
	
	static URI resolve(URI baseURI, Container container) {
		Reference reference = container.getReference();
		StringBuilder sb = new StringBuilder();
		
		for (String field : reference.fields()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			
			sb.append(URLEncoder.encode(field, StandardCharsets.UTF_8));
			sb.append("=");
			sb.append(URLEncoder.encode(reference.get(field), StandardCharsets.UTF_8));
		}
		
		try {
			return new URI(baseURI.getScheme(), baseURI.getAuthority(), baseURI.getPath(), sb.toString(), null);
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
		return resolve(blob.getContainer().getDataStore().getURI(), blob);
	}
	
	static URI resolve(URI baseURI, Blob blob) {
		URI containerUri = resolve(baseURI, blob.getContainer());
		
		StringBuilder sb = new StringBuilder();
		sb.append(containerUri.getQuery() != null ? containerUri.getQuery() : "");
		
		if (sb.length() > 0) {
			sb.append("&");
		}
		
		sb.append(NAME_PARAMETER);
		sb.append("=");
		sb.append(URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8));

		try {
			return new URI(containerUri.getScheme(), containerUri.getAuthority(), containerUri.getPath(),
					sb.toString(), null);
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

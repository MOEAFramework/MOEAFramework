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
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.moeaframework.util.Iterators;
import org.moeaframework.util.Iterators.IndexedValue;

/**
 * A container of blobs.
 */
public interface Container {

	/**
	 * Gets the data store managing this container.
	 * 
	 * @return the data store
	 */
	DataStore getDataStore();

	/**
	 * Gets the reference for this container.
	 * 
	 * @return the reference
	 */
	Reference getReference();

	/**
	 * Gets a reference to a blob with the given name.
	 * 
	 * @param name the blob name
	 * @return the blob reference
	 */
	Blob getBlob(String name);

	/**
	 * Creates the underlying, physical container.  Containers are automatically created when writing a blob, so
	 * an explicit call to create the container is not required.
	 * 
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	void create() throws DataStoreException;

	/**
	 * Returns {@code true} if the underlying, physical container exists.
	 * 
	 * @return {@code true} if the container exists; {@code false} otherwise
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	boolean exists() throws DataStoreException;

	/**
	 * Returns all blobs stored in this container.
	 * 
	 * @return the blobs in this container
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	List<Blob> listBlobs() throws DataStoreException;

	/**
	 * Returns {@code true} if the blob identified by this name exists within this container.
	 * 
	 * @param name the blob name
	 * @return {@code true} if the blob exists; {@code false} otherwise
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public default boolean contains(String name) throws DataStoreException {
		return getBlob(name).exists();
	}

	/**
	 * Returns the URI for this container, which can be used with
	 * {@link DataStoreFactory#resolveContainer(java.net.URI)}.
	 * 
	 * @return the URI
	 */
	public default URI getURI() {
		return DataStoreURI.resolve(this);
	}
	
	/**
	 * Returns the contents of this container formatted as JSON.
	 * 
	 * @return the JSON string
	 */
	public default String toJSON() {
		return toJSON(getDataStore().getURI());
	}

	/**
	 * Returns the contents of this container formatted as JSON.
	 * 
	 * @param baseURI the base URI, which is used to produce URLs
	 * @return the JSON string
	 */
	default String toJSON(URI baseURI) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("\"type\":\"container\",");
		sb.append("\"url\":\"");
		sb.append(StringEscapeUtils.escapeJson(DataStoreURI.resolve(baseURI, this).toString()));
		sb.append("\",");
		sb.append("\"reference\":{");

		for (IndexedValue<String> field : Iterators.enumerate(getReference().fields())) {
			if (field.getIndex() > 0) {
				sb.append(",");
			}

			sb.append("\"");
			sb.append(StringEscapeUtils.escapeJson(field.getValue()));
			sb.append("\":\"");
			sb.append(StringEscapeUtils.escapeJson(getReference().get(field.getValue())));
			sb.append("\"");
		}

		sb.append("},");
		sb.append("\"blobs\":[");

		for (IndexedValue<Blob> blob : Iterators.enumerate(listBlobs())) {
			if (blob.getIndex() > 0) {
				sb.append(",");
			}

			sb.append("{");
			sb.append("\"type\":\"blob\",");
			sb.append("\"name\":\"");
			sb.append(StringEscapeUtils.escapeJson(blob.getValue().getName()));
			sb.append("\",");
			sb.append("\"url\":\"");
			sb.append(StringEscapeUtils.escapeJson(DataStoreURI.resolve(baseURI, blob.getValue()).toString()));
			sb.append("\"");
			sb.append("}");
		}

		sb.append("]");
		sb.append("}");
		
		return sb.toString();
	}

}

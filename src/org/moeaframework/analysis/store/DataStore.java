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
 * Interface for storing data or objects to some persistent backend.
 * <p>
 * The data store organizes related data, called {@link Blob}s, in collections called {@link Container}s.  A container
 * is referenced by a {@link Reference}, and each blob is referenced by a name.  A data store is itself an abstract
 * representation of the underlying storage, as the content could be stored on a local file system, cloud storage, or
 * a database.
 * <p>
 * In addition to the container associated with each reference, there also exists a top-level or "root" container that
 * can store data applicable to the entire data store.  This is useful, for example, to store the parameter samplings
 * used to generate the data.
 */
public interface DataStore {
	
	/**
	 * Returns the container for the given reference.  A container is always returned, though this does not imply the
	 * underlying storage exists or has been provisioned.
	 * 
	 * @param reference the data reference
	 * @return the container
	 */
	public Container getContainer(Reference reference);
	
	/**
	 * Returns a list of all containers, excluding the root container, in this data store.
	 * 
	 * @return a list of containers
	 * @throws DataStoreException if an error occurred accessing the data store
	 */
	public List<Container> listContainers() throws DataStoreException;
	
	/**
	 * Returns the URI for this data store, which can be used with {@link DataStoreFactory#getDataStore(java.net.URI)}.
	 * 
	 * @return the URI
	 */
	public URI getURI();
	
	/**
	 * Returns the root container for this data store.  This container is useful for storing general information or
	 * data about the experiment or data store.
	 * 
	 * @return the root container
	 */
	public default Container getRootContainer() {
		return getContainer(Reference.root());
	}

	/**
	 * Returns the container for the given {@link Referenceable} object.
	 * 
	 * @param reference the data reference
	 * @return the container
	 * @see #getContainer(Reference)
	 */
	public default Container getContainer(Referenceable reference) {
		return getContainer(reference.getReference());
	}
	
	/**
	 * Returns the contents of this data store formatted as JSON.
	 * 
	 * @return the JSON string
	 */
	public default String toJSON() {
		return toJSON(getURI());
	}
	
	/**
	 * Returns the contents of this data store formatted as JSON.
	 * 
	 * @param baseURI the base URI, which is used to produce URLs
	 * @return the JSON string
	 */
	default String toJSON(URI baseURI) {
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		sb.append("\"type\":\"datastore\",");
		sb.append("\"url\":\"");
		sb.append(StringEscapeUtils.escapeJson(baseURI.toString()));
		sb.append("\",");
		sb.append("\"blobs\":[");
		
		for (IndexedValue<Blob> blob : Iterators.enumerate(getRootContainer().listBlobs())) {
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
		
		sb.append("],");
		sb.append("\"containers\":[");
		
		for (IndexedValue<Container> container : Iterators.enumerate(listContainers())) {
			if (container.getIndex() > 0) {
				sb.append(",");
			}
			
			sb.append("{");
			sb.append("\"type\":\"container\",");
			sb.append("\"url\":\"");
			sb.append(StringEscapeUtils.escapeJson(DataStoreURI.resolve(baseURI, container.getValue()).toString()));
			sb.append("\",");
			sb.append("\"reference\":{");
			
			for (IndexedValue<String> field : Iterators.enumerate(container.getValue().getReference().fields())) {
				if (field.getIndex() > 0) {
					sb.append(",");
				}
				
				sb.append("\"");
				sb.append(StringEscapeUtils.escapeJson(field.getValue()));
				sb.append("\":\"");
				sb.append(StringEscapeUtils.escapeJson(container.getValue().getReference().get(field.getValue())));
				sb.append("\"");
			}
			
			sb.append("}");
			sb.append("}");
		}
		
		sb.append("]");
		sb.append("}");
		
		return sb.toString();
	}

}

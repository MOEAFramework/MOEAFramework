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
package org.moeaframework.analysis.store.fs;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.DataStoreException;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.TransactionalOutputStream;
import org.moeaframework.analysis.store.TransactionalWriter;
import org.moeaframework.analysis.store.schema.Schema;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;

/**
 * Data store backed by the local file system.  A {@link FileMap} determines the layout of the containers and blobs.
 * Unless otherwise specified, {@link HashFileMap} is used.
 */
public class FileSystemDataStore implements DataStore {

	/**
	 * The name of the manifest file, which will be located in the root directory of the data store.
	 */
	private static final String MANIFEST_FILENAME = ".manifest";

	/**
	 * The name of the reference file, which will be located in each container.
	 */
	private static final String REFERENCE_FILENAME = ".reference";

	private final Path root;

	private final FileMap fileMap;

	private final Schema schema;

	private final Lock mkdirLock;

	/**
	 * Constructs a default file system data store at the specified directory.
	 * 
	 * @param root the root directory
	 * @throws DataStoreException if an error occurred accessing the data store
	 * @throws ManifestValidationException if the existing manifest failed validation
	 */
	public FileSystemDataStore(File root) {
		this(root.toPath());
	}

	/**
	 * Constructs a default file system data store at the specified directory.
	 * 
	 * @param root the root directory
	 * @throws DataStoreException if an error occurred accessing the data store
	 * @throws ManifestValidationException if the existing manifest failed validation
	 */
	public FileSystemDataStore(Path root) {
		this(root, Schema.schemaless());
	}

	/**
	 * Constructs a default file system data store at the specified directory.
	 * 
	 * @param root the root directory
	 * @param schema the schema defining the structure of the data store
	 * @throws DataStoreException if an error occurred accessing the data store
	 * @throws ManifestValidationException if the existing manifest failed validation
	 */
	public FileSystemDataStore(File root, Schema schema) {
		this(root.toPath(), schema);
	}

	/**
	 * Constructs a default file system data store at the specified directory.
	 * 
	 * @param root the root directory
	 * @param schema the schema defining the structure of the data store
	 * @throws DataStoreException if an error occurred accessing the data store
	 * @throws ManifestValidationException if the existing manifest failed validation
	 */
	public FileSystemDataStore(Path root, Schema schema) {
		this(root, new HierarchicalFileMap(), schema);
	}

	/**
	 * Constructs a default file system data store at the specified directory.
	 * 
	 * @param root the root directory
	 * @param fileMap the file map that determines the layout of files
	 * @param schema the schema defining the structure of the data store
	 * @throws DataStoreException if an error occurred accessing the data store
	 * @throws ManifestValidationException if the existing manifest failed validation
	 */
	public FileSystemDataStore(File root, FileMap fileMap, Schema schema) {
		this(root.toPath(), fileMap, schema);
	}

	/**
	 * Constructs a hierarchical file system data store at the specified directory.
	 * 
	 * @param root the root directory
	 * @param fileMap the file map that determines the layout of files
	 * @param schema the schema defining the structure of the data store
	 * @throws DataStoreException if an error occurred accessing the data store
	 * @throws ManifestValidationException if the existing manifest failed validation
	 */
	public FileSystemDataStore(Path root, FileMap fileMap, Schema schema) {
		super();
		this.root = root;
		this.fileMap = fileMap;
		this.schema = schema;
		this.mkdirLock = new ReentrantLock();

		createOrValidateManifest();
	}

	/**
	 * Returns the schema used by this file store.
	 * 
	 * @return the schema
	 */
	public Schema getSchema() {
		return schema;
	}

	@Override
	public Container getContainer(Reference key) {
		return new FileSystemContainer(key);
	}

	@Override
	public Stream<Container> streamContainers() throws DataStoreException {
		try {
			return Files.walk(root)
					.skip(1)
					.filter(Files::isDirectory)
					.map(path -> {
						Path referenceFile = path.resolve(REFERENCE_FILENAME);

						if (!Files.exists(referenceFile)) {
							return null;
						}

						try (FileReader reader = new FileReader(referenceFile.toFile())) {
							TypedProperties properties = new TypedProperties();
							properties.load(reader);
							return (Container)new FileSystemContainer(Reference.of(properties));
						} catch (FileNotFoundException e) {
							return null;
						} catch (IOException e) {
							throw new DataStoreException("Failed while loading " + referenceFile, e);
						}
					})
					.filter(Objects::nonNull);
		} catch (IOException e) {
			throw new DataStoreException("Failed while listing containers", e);
		}
	}

	@Override
	public URI getURI() {
		return root.toUri();
	}

	/**
	 * Creates all directories, including any missing parents, for the specified path.
	 * 
	 * @param path the directory path
	 * @throws IOException if an I/O error occurred
	 */
	private void mkdirs(Path path) throws IOException {
		if (Files.exists(path)) {
			return;
		}

		// Use lock as concurrent mkdirs on the same path can fail
		try {
			mkdirLock.lock();
			Files.createDirectories(path);
		} finally {
			mkdirLock.unlock();
		}
	}

	/**
	 * Writes the manifest file or validates the existing manifest file.
	 * 
	 * @throws DataStoreException if an error occurred accessing the data store
	 * @throws ManifestValidationException if the existing manifest
	 */
	private void createOrValidateManifest() throws DataStoreException {
		try {
			Path path = root.resolve(MANIFEST_FILENAME);
			Manifest expectedManifest = getManifest();

			if (Files.exists(path)) {
				try (FileReader reader = new FileReader(path.toFile())) {
					Manifest actualManifest = new Manifest();
					actualManifest.load(reader);
					actualManifest.validate(expectedManifest);
				}
			} else {
				mkdirs(path.getParent());

				try (FileWriter writer = new FileWriter(path.toFile())) {
					expectedManifest.save(writer);
				}
			}
		} catch (IOException e) {
			throw new DataStoreException("Failed while creating or validating manifest", e);
		}
	}

	/**
	 * Constructs the manifest for this data store.
	 * 
	 * @return the manifest
	 */
	private Manifest getManifest() {
		Manifest manifest = new Manifest();
		manifest.setInt("version", Settings.getMajorVersion());
		fileMap.updateManifest(manifest);
		schema.updateManifest(manifest);
		return manifest;
	}

	class FileSystemContainer implements Container {

		private final Reference reference;

		public FileSystemContainer(Reference reference) {
			super();
			this.reference = reference;
		}

		@Override
		public DataStore getDataStore() {
			return FileSystemDataStore.this;
		}

		@Override
		public Reference getReference() {
			return reference;
		}

		@Override
		public Blob getBlob(String name) {
			return new FileSystemBlob(reference, name);
		}

		@Override
		public void create() throws DataStoreException {
			try {
				Path container = fileMap.mapContainer(getSchema(), root, reference);
				mkdirs(container);

				Path dest = container.resolve(REFERENCE_FILENAME);

				if (!Files.exists(dest)) {
					Path temp = Files.createTempFile("datastore", null);

					try (TransactionalFileWriter writer = new TransactionalFileWriter(temp.toFile(), dest.toFile())) {
						TypedProperties properties = new TypedProperties();

						for (String field : reference.fields()) {
							properties.setString(field, reference.get(field));
						}

						properties.save(writer);
						writer.commit();
					}
				}
			} catch (IOException e) {
				throw DataStoreException.wrap(e, this);
			}
		}

		@Override
		public boolean exists() throws DataStoreException {
			try {
				Path container = fileMap.mapContainer(getSchema(), root, reference);
				Path referenceFile = container.resolve(REFERENCE_FILENAME);
				
				return Files.exists(referenceFile) || reference.isRoot();
			} catch (IOException e) {
				throw DataStoreException.wrap(e, this);
			}
		}
		
		@Override
		public boolean delete() throws DataStoreException {
			try {
				Path container = fileMap.mapContainer(getSchema(), root, reference);
				Path referenceFile = container.resolve(REFERENCE_FILENAME);
				
				if (Files.exists(referenceFile) || reference.isRoot()) {
					if (Files.list(container).anyMatch(Files::isDirectory)) {
						try (Stream<Blob> stream = streamBlobs()) {
							stream.forEach(Blob::delete);
						}
						
						Files.deleteIfExists(referenceFile);
					} else {
						FileUtils.deleteDirectory(container.toFile());
					}
					
					return true;
				}
				
				return false;
			} catch (IOException e) {
				throw DataStoreException.wrap(e, this);
			}
		}
		
		@Override
		public Stream<Blob> streamBlobs() throws DataStoreException {
			try {
				Path container = fileMap.mapContainer(getSchema(), root, reference);

				if (!Files.exists(container)) {
					return Stream.empty();
				}

				return Files.list(container)
							.filter(Predicate.not(Files::isDirectory))
							.map(x -> x.getFileName().toString())
							.filter(x -> x.charAt(0) != '.')
							.map(this::getBlob);
			} catch (IOException e) {
				throw DataStoreException.wrap(e, this);
			}
		}

	}

	class FileSystemBlob implements Blob {

		private final Reference reference;

		private final String name;

		public FileSystemBlob(Reference reference, String name) {
			super();
			this.reference = reference;
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Container getContainer() {
			return new FileSystemContainer(reference);
		}

		@Override
		public boolean exists() throws DataStoreException {
			try {
				return Files.exists(fileMap.mapBlob(getSchema(), root, reference, name));
			} catch (IOException e) {
				throw DataStoreException.wrap(e, this);
			}
		}

		@Override
		public boolean delete() throws DataStoreException {
			try {
				return Files.deleteIfExists(fileMap.mapBlob(getSchema(), root, reference, name));
			} catch (IOException e) {
				throw DataStoreException.wrap(e, this);
			}
		}

		@Override
		public Instant lastModified() throws DataStoreException {
			try {
				return Files.getLastModifiedTime(fileMap.mapBlob(getSchema(), root, reference, name)).toInstant();
			} catch (IOException e) {
				throw DataStoreException.wrap(e, this);
			}
		}

		@Override
		public Reader openReader() throws DataStoreException {
			try {
				return new FileReader(fileMap.mapBlob(getSchema(), root, reference, name).toFile());
			} catch (IOException e) {
				throw DataStoreException.wrap(e, this);
			}
		}

		@Override
		public InputStream openInputStream() throws DataStoreException {
			try {
				return new FileInputStream(fileMap.mapBlob(getSchema(), root, reference, name).toFile());
			} catch (IOException e) {
				throw DataStoreException.wrap(e, this);
			}
		}

		@Override
		public TransactionalWriter openWriter() throws DataStoreException {
			try {
				getContainer().create();

				Path dest = fileMap.mapBlob(getSchema(), root, reference, name);
				Path temp = Files.createTempFile("datastore", null);
				return new TransactionalFileWriter(temp.toFile(), dest.toFile());
			} catch (IOException e) {
				throw DataStoreException.wrap(e, this);
			}
		}

		@Override
		public TransactionalOutputStream openOutputStream() throws DataStoreException {
			try {
				getContainer().create();

				Path dest = fileMap.mapBlob(getSchema(), root, reference, name);
				Path temp = Files.createTempFile("datastore", null);
				return new TransactionalFileOutputStream(temp.toFile(), dest.toFile());
			} catch (IOException e) {
				throw DataStoreException.wrap(e, this);
			}
		}

	}

	class TransactionalFileOutputStream extends TransactionalOutputStream {

		private final File tempFile;

		private final File destFile;

		TransactionalFileOutputStream(File tempFile, File destFile) throws IOException {
			super(new BufferedOutputStream(new FileOutputStream(tempFile)));
			this.tempFile = tempFile;
			this.destFile = destFile;
		}

		@Override
		protected void doCommit() throws IOException {
			Files.move(tempFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		@Override
		protected void doRollback() throws IOException {
			tempFile.delete();
		}

	}

	class TransactionalFileWriter extends TransactionalWriter {

		private final File tempFile;

		private final File destFile;

		TransactionalFileWriter(File tempFile, File destFile) throws IOException {
			super(new BufferedWriter(new FileWriter(tempFile)));
			this.tempFile = tempFile;
			this.destFile = destFile;
		}

		@Override
		protected void doCommit() throws IOException {
			Files.move(tempFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		@Override
		protected void doRollback() throws IOException {
			tempFile.delete();
		}

	}

}

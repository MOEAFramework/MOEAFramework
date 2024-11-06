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
package org.moeaframework.analysis.store.fs;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.TransactionalOutputStream;
import org.moeaframework.analysis.store.TransactionalWriter;
import org.moeaframework.analysis.store.schema.Schema;
import org.moeaframework.core.Settings;

/**
 * Data store backed by the local file system.  A {@link FileMap} determines the layout of the files.
 */
public class FileSystemDataStore implements DataStore {
	
	private final Path root;
		
	private final FileMap fileMap;
	
	private final Schema schema;
	
	private final Lock mkdirLock;
	
	/**
	 * Constructs a hierarchical file system data store at the specified directory.
	 * 
	 * @param root the root directory
	 * @throws IOException if an I/O error occurred
	 * @throws ManifestValidationException if the existing manifest failed validation
	 */
	public FileSystemDataStore(File root) throws IOException {
		this(root, new HierarchicalFileMap());
	}
	
	/**
	 * Constructs a hierarchical file system data store at the specified directory.
	 * 
	 * @param root the root directory
	 * @param schema the schema defining the structure of the data store
	 * @throws IOException if an I/O error occurred
	 * @throws ManifestValidationException if the existing manifest failed validation
	 */
	public FileSystemDataStore(File root, Schema schema) throws IOException {
		this(root.toPath(), new HierarchicalFileMap(), schema);
	}
	
	/**
	 * Constructs a file system data store at the specified directory.
	 * 
	 * @param root the root directory
	 * @param fileMap the file map that determines the layout of files
	 * @throws IOException if an I/O error occurred
	 * @throws ManifestValidationException if the existing manifest failed validation
	 */
	public FileSystemDataStore(File root, FileMap fileMap) throws IOException {
		this(root.toPath(), fileMap, Schema.schemaless());
	}

	/**
	 * Constructs a hierarchical file system data store at the specified directory.
	 * 
	 * @param root the root directory
	 * @param fileMap the file map that determines the layout of files
	 * @param schema the schema defining the structure of the data store
	 * @throws IOException if an I/O error occurred
	 * @throws ManifestValidationException if the existing manifest failed validation
	 */
	public FileSystemDataStore(Path root, FileMap fileMap, Schema schema) throws IOException {
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
	
	/**
	 * Returns the root directory for this data store.
	 * 
	 * @return the root directory
	 */
	public Path getRoot() {
		return root;
	}

	@Override
	public FileSystemContainer getContainer(Reference key) {
		return new FileSystemContainer(key);
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
	 * @throws IOException if an I/O error occurred
	 * @throws ManifestValidationException if the existing manifest 
	 */
	private void createOrValidateManifest() throws IOException, ManifestValidationException {
		Path path = getRoot().resolve(Manifest.FILENAME);
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
		
		private final Reference key;
		
		public FileSystemContainer(Reference key) {
			super();
			this.key = key;
		}

		@Override
		public Reference getKey() {
			return key;
		}

		@Override
		public Blob getBlob(String name) {
			return new FileSystemBlob(key, name);
		}
		
		@Override
		public void create() throws IOException {
			try {
				mkdirs(fileMap.mapContainer(getSchema(), getRoot(), key));
			} catch (UnsupportedOperationException e) {
				// suppress exception - containers are not supported
			}
		}

		@Override
		public boolean exists() throws IOException {
			try {
				return Files.exists(fileMap.mapContainer(getSchema(), getRoot(), key));
			} catch (UnsupportedOperationException e) {
				// suppress exception - containers are not supported
				return true;
			}
		}
		
	}
	
	class FileSystemBlob implements Blob {
		
		private final Reference key;
		
		private final String name;
		
		public FileSystemBlob(Reference key, String name) {
			super();
			this.key = key;
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public Container getContainer() {
			return new FileSystemContainer(key);
		}

		@Override
		public boolean exists() throws IOException {
			return Files.exists(fileMap.mapBlob(getSchema(), getRoot(), key, name));
		}

		@Override
		public boolean delete() throws IOException {
			return Files.deleteIfExists(fileMap.mapBlob(getSchema(), getRoot(), key, name));
		}

		@Override
		public Instant lastModified() throws IOException {
			return Files.getLastModifiedTime(fileMap.mapBlob(getSchema(), getRoot(), key, name)).toInstant();
		}

		@Override
		public Reader openReader() throws IOException {
			return new FileReader(fileMap.mapBlob(getSchema(), getRoot(), key, name).toFile());
		}

		@Override
		public InputStream openInputStream() throws IOException {
			return new FileInputStream(fileMap.mapBlob(getSchema(), getRoot(), key, name).toFile());
		}

		@Override
		public TransactionalWriter openWriter() throws IOException {
			Path dest = fileMap.mapBlob(getSchema(), getRoot(), key, name);
			mkdirs(dest.getParent());
			
			Path temp = Files.createTempFile("datastore", null);
			return new TransactionalFileWriter(temp.toFile(), dest.toFile());
		}

		@Override
		public TransactionalOutputStream openOutputStream() throws IOException {
			Path dest = fileMap.mapBlob(getSchema(), getRoot(), key, name);
			mkdirs(dest.getParent());
			
			Path temp = Files.createTempFile("datastore", null);
			return new TransactionalFileOutputStream(temp.toFile(), dest.toFile());
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

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
import org.moeaframework.analysis.store.Key;
import org.moeaframework.analysis.store.TransactionalOutputStream;
import org.moeaframework.analysis.store.TransactionalWriter;
import org.moeaframework.core.Settings;

public class FileSystemDataStore implements DataStore {
	
	private final Path root;
		
	private final FileMap fileMap;
	
	private final Lock mkdirLock;
		
	public FileSystemDataStore(File root) throws IOException {
		this(root, new HierarchicalFileMap());
	}
	
	public FileSystemDataStore(File root, FileMap fileMap) throws IOException {
		this(root.toPath(), fileMap);
	}

	public FileSystemDataStore(Path root, FileMap fileMap) throws IOException {
		super();
		this.root = root;
		this.fileMap = fileMap;
		this.mkdirLock = new ReentrantLock();
		
		createOrValidateManifest();
	}
	
	public Path getRoot() {
		return root;
	}

	@Override
	public FileSystemContainer getContainer(Key key) {
		return new FileSystemContainer(key);
	}

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
				expectedManifest.store(writer);
			}
		}
	}
	
	private Manifest getManifest() {
		Manifest manifest = new Manifest();
		manifest.setInt("version", Settings.getMajorVersion());
		manifest.setString("fileMap", fileMap.getDefinition());
		return manifest;
	}
	
	class FileSystemContainer implements Container {
		
		private final Key key;
		
		public FileSystemContainer(Key key) {
			super();
			this.key = key;
		}

		@Override
		public Key getKey() {
			return key;
		}

		@Override
		public Blob getBlob(String name) {
			return new FileSystemBlob(key, name);
		}
		
		@Override
		public void create() throws IOException {
			try {
				mkdirs(fileMap.mapContainer(getRoot(), key));
			} catch (UnsupportedOperationException e) {
				// suppress exception - containers are not supported
			}
		}

		@Override
		public boolean exists() throws IOException {
			try {
				return Files.exists(fileMap.mapContainer(getRoot(), key));
			} catch (UnsupportedOperationException e) {
				// suppress exception - containers are not supported
				return true;
			}
		}
		
	}
	
	class FileSystemBlob implements Blob {
		
		private final Key key;
		
		private final String name;
		
		public FileSystemBlob(Key key, String name) {
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
			return Files.exists(fileMap.mapBlob(getRoot(), key, name));
		}

		@Override
		public boolean delete() throws IOException {
			return Files.deleteIfExists(fileMap.mapBlob(getRoot(), key, name));
		}

		@Override
		public Instant lastModified() throws IOException {
			return Files.getLastModifiedTime(fileMap.mapBlob(getRoot(), key, name)).toInstant();
		}

		@Override
		public Reader openReader() throws IOException {
			return new FileReader(fileMap.mapBlob(getRoot(), key, name).toFile());
		}

		@Override
		public InputStream openInputStream() throws IOException {
			return new FileInputStream(fileMap.mapBlob(getRoot(), key, name).toFile());
		}

		@Override
		public TransactionalWriter openWriter() throws IOException {
			Path dest = fileMap.mapBlob(getRoot(), key, name);
			mkdirs(dest.getParent());
			
			Path temp = Files.createTempFile("datastore", null);
			return new TransactionalFileWriter(temp.toFile(), dest.toFile());
		}

		@Override
		public TransactionalOutputStream openOutputStream() throws IOException {
			Path dest = fileMap.mapBlob(getRoot(), key, name);
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

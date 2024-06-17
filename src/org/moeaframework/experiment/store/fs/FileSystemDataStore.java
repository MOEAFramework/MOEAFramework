package org.moeaframework.experiment.store.fs;

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
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.moeaframework.experiment.store.DataReader;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataWriter;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.TransactionalOutputStream;
import org.moeaframework.experiment.store.TransactionalWriter;
import org.moeaframework.experiment.store.schema.Schema;
import org.moeaframework.util.PropertyNotFoundException;
import org.moeaframework.util.TypedProperties;

public class FileSystemDataStore implements DataStore {
	
	private final Schema schema;
	
	private final FileMap fileMap;
	
	private final ReentrantLock mkdirLock;
		
	public FileSystemDataStore(Schema schema, File root) throws IOException {
		this(schema, HashFileMap.at(root));
	}

	public FileSystemDataStore(Schema schema, FileMap fileMap) throws IOException {
		super();
		this.schema = schema;
		this.fileMap = fileMap;
		
		mkdirLock = new ReentrantLock();
		
		fileMap.validateSchema(schema);
		createOrValidateManifest();
	}
	
	@Override
	public Schema getSchema() {
		return schema;
	}

	@Override
	public FileSystemDataWriter writer(Key key, DataType dataType) {
		return new FileSystemDataWriter(key, dataType);
	}
	
	@Override
	public FileSystemDataReader reader(Key key, DataType dataType) {
		return new FileSystemDataReader(key, dataType);
	}

	private void mkdirs(File file) throws IOException {
		if (file.exists()) {
			return;
		}
		
		// Use lock as concurrent mkdirs on the same path can fail
		try {
			mkdirLock.lock();
			Files.createDirectories(file.toPath());
		} finally {
			mkdirLock.unlock();
		}
	}
	
	private void createOrValidateManifest() throws IOException, ManifestValidationException {
		File manifest = new File(fileMap.getRoot(), ".manifest");
		
		if (manifest.exists()) {	
			try (FileReader reader = new FileReader(manifest)) {
				TypedProperties properties = new TypedProperties();
				properties.load(reader);
				
				fileMap.validateManifest(properties);
				
				if (!StringUtils.equalsIgnoreCase(schema.toString(), properties.getString("schema"))) {
					throw new ManifestValidationException("Schemas do not match, expected " + schema + " but was " +
							properties.getString("schema"));
				}
			} catch (PropertyNotFoundException e) {
				throw new ManifestValidationException("Missing value in manifest", e);
			}
		} else {
			mkdirs(manifest.getParentFile());
			
			try (FileWriter writer = new FileWriter(manifest)) {
				TypedProperties properties = new TypedProperties();
				fileMap.createManifest(properties);
				properties.setString("schema", schema.toString());
				properties.store(writer);
			}
		}
	}
	
	class FileSystemDataWriter implements DataWriter {
		
		private final Key key;
		
		private final DataType dataType;
		
		public FileSystemDataWriter(Key key, DataType dataType) {
			super();
			this.key = key;
			this.dataType = dataType;
		}
		
		@Override
		public boolean exists() {
			return fileMap.map(schema, key, dataType).exists();
		}
		
		@Override
		public boolean delete() {
			return fileMap.map(schema, key, dataType).delete();
		}
		
		@Override
		public TransactionalOutputStream asBinary() throws IOException {
			File destFile = fileMap.map(schema, key, dataType);
			mkdirs(destFile.getParentFile());
			
			File tempFile = File.createTempFile("datastore", null);
			
			return new TransactionalFileOutputStream(tempFile, destFile);
		}

		@Override
		public TransactionalWriter asText() throws IOException {
			File destFile = fileMap.map(schema, key, dataType);
			mkdirs(destFile.getParentFile());
			
			File tempFile = File.createTempFile("datastore", null);
			
			return new TransactionalFileWriter(tempFile, destFile);
		}
		
	}
	
	class FileSystemDataReader implements DataReader {
		
		private final Key key;
		
		private final DataType dataType;
		
		public FileSystemDataReader(Key key, DataType dataType) {
			super();
			this.key = key;
			this.dataType = dataType;
		}
		
		@Override
		public boolean exists() {
			return fileMap.map(schema, key, dataType).exists();
		}
		
		@Override
		public Instant lastModified() {
			long lastModified = fileMap.map(schema, key, dataType).lastModified();
			
			if (lastModified == 0L) {
				return null;
			}
			
			return Instant.ofEpochMilli(lastModified);
		}

		@Override
		public InputStream asBinary() throws IOException {
			return new FileInputStream(fileMap.map(schema, key, dataType));
		}

		@Override
		public Reader asText() throws IOException {
			return new FileReader(fileMap.map(schema, key, dataType));
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
			Files.move(tempFile.toPath(), destFile.toPath(), StandardCopyOption.ATOMIC_MOVE,
					StandardCopyOption.REPLACE_EXISTING);
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
			Files.move(tempFile.toPath(), destFile.toPath(), StandardCopyOption.ATOMIC_MOVE,
					StandardCopyOption.REPLACE_EXISTING);
		}
		
		@Override
		protected void doRollback() throws IOException {
			tempFile.delete();
		}

	}

}

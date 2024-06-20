package org.moeaframework.experiment.job;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.experiment.Sample;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.TransactionalOutputStream;
import org.moeaframework.experiment.store.TransactionalWriter;
import org.moeaframework.experiment.store.schema.Field;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.format.TableFormat;
import org.moeaframework.util.format.TabularData;

public class JobUtils {
	
	private JobUtils() {
		super();
	}
	
	public static String getProblemName(Key key, Sample sample) {
		if (key != null && Field.PROBLEM.isDefined(key)) {
			return Field.PROBLEM.valueOf(key);
		}
		
		if (sample != null && Field.PROBLEM.isDefined(sample)) {
			return Field.PROBLEM.valueOf(sample);
		}
		
		throw new IllegalArgumentException("Must provide key or sample that defines the field '" +
				Field.PROBLEM.getName() + "'");
	}
	
	public static NondominatedPopulation loadApproximationSet(DataStore dataStore, Key key, Problem problem)
			throws IOException {
		File tempFile = File.createTempFile("result", null);
		
		try {
			dataStore.reader(key, DataType.APPROXIMATION_SET).extract(tempFile);
			
			NondominatedPopulation approximationSet = null;
				
			try (ResultFileReader reader = ResultFileReader.open(problem, tempFile)) {
				for (ResultEntry entry : reader) {
					approximationSet = entry.getPopulation();
				}
			}
			
			return approximationSet;
		} finally {
			tempFile.delete();
		}
	}
	
	public static void saveApproximationSet(DataStore dataStore, Key key, Problem problem,
			NondominatedPopulation approximationSet) throws IOException {
		saveApproximationSet(dataStore, key, problem, approximationSet, new TypedProperties());
	}
	
	public static void saveApproximationSet(DataStore dataStore, Key key, Problem problem,
			NondominatedPopulation approximationSet, TypedProperties properties) throws IOException {
		File tempFile = File.createTempFile("result", null);
		
		try {
			try (ResultFileWriter writer = ResultFileWriter.overwrite(problem, tempFile)) {
				writer.append(new ResultEntry(approximationSet, properties));
			}
			
			dataStore.writer(key, DataType.APPROXIMATION_SET).store(tempFile);
		} finally {
			tempFile.delete();
		}
	}
	
	public static <T extends TypedProperties> void saveProperties(DataStore dataStore, DataReference dataReference,
			T properties) throws IOException {
		saveProperties(dataStore, dataReference.getKey(), dataReference.getDataType(), properties);
	}
	
	public static <T extends TypedProperties> void saveProperties(DataStore dataStore, Key key, DataType dataType,
			T properties) throws IOException {
		try (TransactionalWriter out = dataStore.writer(key, dataType).asText()) {
			properties.store(out);
			out.commit();
		}
	}
	
	public static <T extends TypedProperties> T loadProperties(DataStore dataStore, DataReference dataReference,
			T properties) throws IOException {
		return loadProperties(dataStore, dataReference.getKey(), dataReference.getDataType(), properties);
	}
	
	public static <T extends TypedProperties> T loadProperties(DataStore dataStore, Key key, DataType dataType,
			T properties) throws IOException {
		try (Reader in = dataStore.reader(key, dataType).asText()) {
			properties.load(in);
		}
		
		return properties;
	}
	
	public static TypedProperties loadProperties(DataStore dataStore, DataReference dataReference) throws IOException {
		return loadProperties(dataStore, dataReference.getKey(), dataReference.getDataType());
	}
	
	public static TypedProperties loadProperties(DataStore dataStore, Key key, DataType dataType) throws IOException {
		return loadProperties(dataStore, key, dataType, new TypedProperties());
	}
	
	public static void saveTabularData(DataStore dataStore, DataReference dataReference, TabularData<?> data)
			throws IOException {
		saveTabularData(dataStore, dataReference.getKey(), dataReference.getDataType(), data);
	}
	
	public static void saveTabularData(DataStore dataStore, Key key, DataType dataType, TabularData<?> data)
			throws IOException {
		saveTabularData(dataStore, key, dataType, data, TableFormat.Plaintext);
	}
	
	public static void saveTabularData(DataStore dataStore, DataReference dataReference, TabularData<?> data,
			TableFormat tableFormat) throws IOException {
		saveTabularData(dataStore, dataReference.getKey(), dataReference.getDataType(), data, tableFormat);
	}
	
	public static void saveTabularData(DataStore dataStore, Key key, DataType dataType, TabularData<?> data,
			TableFormat tableFormat) throws IOException {
		try (TransactionalOutputStream out = dataStore.writer(key, dataType).asBinary();
				PrintStream ps = new PrintStream(out)) {
			data.display(tableFormat, ps);
			out.commit();
		}
	}

}

package org.moeaframework.experiment.store.type;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.moeaframework.util.TypedProperties;

public class TypedPropertiesDataType extends TextDataType<TypedProperties> {

	TypedPropertiesDataType(String name) {
		super(name, TypedPropertiesDataType::reader, TypedPropertiesDataType::writer);
	}
	
	private static TypedProperties reader(Reader in) throws IOException {
		TypedProperties properties = new TypedProperties();
		properties.load(in);
		return properties;
	}
	
	private static void writer(Writer out, TypedProperties properties) throws IOException {
		properties.store(out);
	}

}

package org.moeaframework.analysis.collector;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * An observation records information about an algorithm at a point in time.  
 */
public class Observation implements Serializable, Comparable<Observation> {

	private static final long serialVersionUID = 3267334718718774271L;
	
	private int nfe;
	
	private Map<String, Serializable> data;
	
	public Observation(int nfe) {
		super();
		this.nfe = nfe;
		this.data = new TreeMap<String, Serializable>(String.CASE_INSENSITIVE_ORDER);
	}
	
	public int getNFE() {
		return nfe;
	}
	
	public Set<String> keys() {
		return data.keySet();
	}
	
	public Serializable get(String key) {
		Serializable value = data.get(key);
		
		if (value == null) {
			throw new IllegalArgumentException(MessageFormat.format("no observation with key: {0}", key));
		}
		
		return value;
	}
	
	public void set(String key, Serializable value) {
		data.put(key, value);
	}
	
	@Override
	public int compareTo(Observation other) {
		return Integer.compare(getNFE(), other.getNFE());
	}

}

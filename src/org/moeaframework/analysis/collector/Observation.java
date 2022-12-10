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
	
	/**
	 * Creates a new observation recorded at the given number of function evaluations.
	 * 
	 * @param nfe the number of function evaluations
	 */
	public Observation(int nfe) {
		super();
		this.nfe = nfe;
		this.data = new TreeMap<String, Serializable>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Returns the number of function evaluations during which this record was created.
	 * 
	 * @return the number of function evaluations
	 */
	public int getNFE() {
		return nfe;
	}
	
	/**
	 * Returns the keys recorded in this observation.
	 * 
	 * @return the keys
	 */
	public Set<String> keys() {
		return data.keySet();
	}
	
	/**
	 * Returns the value of the given key.
	 * 
	 * @param key the key
	 * @return the value
	 */
	public Serializable get(String key) {
		Serializable value = data.get(key);
		
		if (value == null) {
			throw new IllegalArgumentException(MessageFormat.format("no observation with key: {0}", key));
		}
		
		return value;
	}
	
	/**
	 * Sets the value of the given key.  At a minimum, these values must be serializable to support
	 * saving these observations, but we recommend using primitive types (int, double, etc.) whenever
	 * possible for formatted output.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void set(String key, Serializable value) {
		data.put(key, value);
	}
	
	@Override
	public int compareTo(Observation other) {
		return Integer.compare(getNFE(), other.getNFE());
	}

}

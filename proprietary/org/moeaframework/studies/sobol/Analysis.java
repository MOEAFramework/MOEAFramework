package org.moeaframework.studies.sobol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.moeaframework.analysis.sensitivity.MetricFileReader;
import org.moeaframework.analysis.sensitivity.Parameter;
import org.moeaframework.analysis.sensitivity.ParameterFile;
import org.moeaframework.analysis.sensitivity.SampleReader;


public class Analysis {

	public static int metric = 0;

	public static final String[] problems = new String[] { "UF1", "UF2", "UF3",
			"UF4", "UF5", "UF6", "UF7", "UF8", "UF9", "UF10", "UF11", "UF12",
			"UF13", "DTLZ1_2", "DTLZ1_4", "DTLZ1_6", "DTLZ1_8", "DTLZ2_2",
			"DTLZ2_4", "DTLZ2_6", "DTLZ2_8", "DTLZ3_2", "DTLZ3_4", "DTLZ3_6",
			"DTLZ3_8", "DTLZ4_2", "DTLZ4_4", "DTLZ4_6", "DTLZ4_8", "DTLZ7_2",
			"DTLZ7_4", "DTLZ7_6", "DTLZ7_8" };

	public static final double[] target = new double[] { 0.6660063922268297,
			0.6634701480797255, 0.6660784494802546, 0.3302104756874696, 0.475,
			0.43731202965121746, 0.49750350961560763, 0.46868175299131326,
			0.7832227911064088, 0.4695830430104315, 0.6539567787053399,
			0.650409318405089, 0.9596535265957981, 0.4901, 0.31633159986000003,
			0.1836749313388098, 0.10, 0.2079697791, 0.5458060198438512,
			0.5160066864270534, 0.615, 0.2079697791, 0.5458060198438512,
			0.5160066864270534, 0.615, 0.2079697791, 0.5458060198438512,
			0.5160066864270534, 0.615, 0.3300000994995173, 0.1920456837901699,
			0.11742177518724363, 0.08 };

	public static final String[] algorithms = new String[] { "Borg", "eMOEA",
		"MOEAD", "GDE3", "OMOPSO", "IBEA", "eNSGAII", "NSGAII", "SPEA2" };

	public static final File directory = new File(System.getenv("WORK"));
	
	private static double[] metrics;
	
	private static double[][] parameters;
	
	private static int total;
	
	private static double[] toArray(Properties properties, ParameterFile pf) {
		double[] result = new double[pf.size()];
		
		for (int i=0; i<pf.size(); i++) {
			result[i] = Double.parseDouble(properties.getProperty(pf.get(i).getName()));
		}
		
		return result;
	}
	
	private static void load(String algorithm, String problem) throws IOException {
		ParameterFile parameterFile = new ParameterFile(new File("./params/" + algorithm + "_Params"));
		SampleReader parameterReader = null;
		MetricFileReader metricReader = null;
		
		List<double[]> parameterList = new ArrayList<double[]>();
		List<Double> metricList = new ArrayList<Double>();
		total = 0;

		try {
			parameterReader = new SampleReader(new File("./params/" + algorithm + "_Sobol"), parameterFile);
			int count = 0;
			
			for (int i=0; i<=9; i++) {
				try {
					File file = new File("./scratch/" + algorithm + "_" + problem + "_" + 0 + "_" + i);
					
					if (!file.exists()) {
						break;
					}
					
					while (count < SobolJobFactory.N*i) {
						parameterReader.next();
						count++;
					}

					metricReader = new MetricFileReader(file);
					
					while (metricReader.hasNext()) {
						double value = metricReader.next()[metric];
						Properties parameters = parameterReader.next();

						count++;
						
						if (metric == 0) {
							if ((value < 0.0) || (value > 1.0)) {
								continue;
							}
						} else if ((metric == 1) || (metric == 4)) {
							value = Math.max(0.0, 1.0 - value);
							
							//cutoff gd and eps indicator values to the range [0, 1]
							value = Math.max(0.0, value);
							value = Math.min(1.0, value);
						}

						metricList.add(value);
						parameterList.add(toArray(parameters, parameterFile));
					}
				} finally {
					if (metricReader != null) {
						metricReader.close();
					}
				}
			}
		} finally {
			if (parameterReader != null) {
				parameterReader.close();
			}
		}
		
		if (parameterList.size() != metricList.size()) {
			throw new IOException("something went horribly wrong");
		}

		total = Math.max(total, parameterList.size());
		parameters = new double[parameterList.size()][];
		metrics = new double[metricList.size()];
		
		for (int i=0; i<metricList.size(); i++) {
			metrics[i] = metricList.get(i);
			parameters[i] = parameterList.get(i);
		}
	}

//	private static double[][] loadParameters(String algorithm) throws IOException {
//		CommentedLineReader reader = null;
//		File file = new File("./params/" + algorithm + "_LHS");
//
//		try {
//			reader = new CommentedLineReader(new FileReader(file));
//			List<double[]> data = new ArrayList<double[]>();
//			String line = null;
//			
//			while ((line = reader.readLine()) != null) {
//				String[] tokens = line.split("\\s+");
//				double[] values = new double[tokens.length];
//
//				for (int i = 0; i < tokens.length; i++) {
//					values[i] = Double.parseDouble(tokens[i]);
//				}
//				
//				data.add(values);
//			}
//
//			return data.toArray(new double[0][]);
//		} finally {
//			if (reader != null) {
//				reader.close();
//			}
//		}
//	}
	
//	private static double[] loadMetrics(String algorithm, String problem) throws IOException {
//		List<Double> sums = new ArrayList<Double>();
//		List<Integer> counts = new ArrayList<Integer>();
//
//		for (int seed=0; seed < 50; seed++) {
//			File file = new File(directory, algorithm + "_" + problem + "_"
//					+ seed);
//			
//			if (!file.exists()) {
//				file = new File(directory, "old/" + algorithm + "_" + problem +
//						"_" + seed);
//			}
//
//			if (!file.exists()) {
//				continue;
//			}
//
//			MetricFileReader reader = null;
//
//			try {
//				reader = new MetricFileReader(new FileReader(file));
//				int index = 0;
//
//				while (reader.hasNext()) {
//					double value = 0.0;
//					
//					try {
//						value = reader.next()[metric];
//					} catch (NumberFormatException e) {
//						break;
//					}
//					
//					if ((metric == 1) || (metric == 4)) {
//						value = Math.max(0.0, 1.0 - value);
//					}
//					
//					if ((value < 0.0) || (value > 1.0)) {
//						continue;
//					}
//
//					if (index >= sums.size()) {
//						sums.add(value);
//						counts.add(1);
//					} else {
//						sums.set(index, sums.get(index) + value);
//						counts.set(index, counts.get(index) + 1);
//					}
//
//					index++;
//				}
//			} finally {
//				if (reader != null) {
//					reader.close();
//				}
//			}
//
//			seed++;
//		}
//		
//		double[] result = new double[sums.size()];
//
//		for (int i = 0; i < sums.size(); i++) {
//			result[i] = sums.get(i) / counts.get(i);
//		}
//
//		return result;
//	}
	
	private static double[][] threshold(double[][] parameters, 
			double[] metrics, double threshold) {
		int count = 0;
		
		for (int i = 0; i < Math.min(parameters.length, metrics.length); i++) {
			if (metrics[i] >= threshold) {
				count++;
			}
		}

		double[][] result = new double[count][];
		count = 0;
		
		for (int i = 0; i < Math.min(parameters.length, metrics.length); i++) {
			if (metrics[i] >= threshold) {
				result[count] = parameters[i];
				count++;
			}
		}

		return result;
	}

	public static void runBest() throws IOException {
		for (int problem = 0; problem < problems.length; problem++) {
			System.out.println(problems[problem] + "\t" + target[problem]);
			
			for (int algorithm = 0; algorithm < algorithms.length; algorithm++) {
				load(algorithms[algorithm], problems[problem]);
				double best = 0.0;
				
				for (int i = 0; i < metrics.length; i++) {
					best = Math.max(metrics[i], best);
				}
				
				System.out.println("\t" + algorithms[algorithm] + "\t"
						+ best + "\t" + 
						(best / target[problem]));
			}
		}
	}

	public static void runAttainment() throws IOException {
		for (int problem = 0; problem < problems.length; problem++) {
			double threshold = 0.75 * target[problem];
			System.out.println(problems[problem] + "\t" + threshold);
			
			for (int algorithm = 0; algorithm < algorithms.length; algorithm++) {
				load(algorithms[algorithm], problems[problem]);
				int count = 0;

				for (int i = 0; i < metrics.length; i++) {
					if (metrics[i] >= threshold) {
						count++;
					}
				}

				System.out.println("\t" + algorithms[algorithm] + "\t"
						+ (count / (double)metrics.length) + "\t" + 
						count);
			}
		}
	}
	
	public static void runControllability() throws IOException {
		for (int problem = 0; problem < problems.length; problem++) {
			double threshold = 0.75 * target[problem];
			System.out.println(problems[problem] + "\t" + threshold);
			
			for (int algorithm = 0; algorithm < algorithms.length; algorithm++) {
				ParameterFile pf = new ParameterFile(new File("./params/" + algorithms[algorithm] + "_Params"));
				load(algorithms[algorithm], problems[problem]);
				double[][] attainmentVolume = threshold(parameters, metrics, threshold);
				
				// normalize parameters
				for (int i = 0; i < pf.size(); i++) {
					Parameter parameter = pf.get(i);

					for (int j = 0; j < parameters.length; j++) {
						parameters[j][i] = (parameters[j][i] - parameter.getLowerBound())
								/ (parameter.getUpperBound() - parameter.getLowerBound());
					}
				}
				
				//if (attainmentVolume.length) {
					System.out.println("\t" + algorithms[algorithm] + "\t" +
							FractalDimension.computeDimension(attainmentVolume) + "\t" +
							FractalDimension.computeDimension(parameters) + "\t" +
							attainmentVolume.length);
				//} else {
				//	System.out.println("\t" + algorithms[algorithm] + "\t" +
				//			"-" + "\t" + "1.0");
				//}
				
			}
		}
	}
	
	public static void runEfficiency(double p) throws IOException {
		int bandWidth = 10000;
		
		for (int problem = 0; problem < problems.length; problem++) {
			double threshold = 0.75 * target[problem];
			System.out.println(problems[problem] + "\t" + threshold);
			
			for (int algorithm = 0; algorithm < algorithms.length; algorithm++) {
				//find the max evaluations parameter index
				int evalIndex = -1;
				ParameterFile pf = new ParameterFile(new File("./params/" + algorithms[algorithm] + "_Params"));
				for (int i=0; i<pf.size(); i++) {
					if (pf.get(i).getName().equals("maxEvaluations")) {
						evalIndex = i;
						break;
					}
				}
				
				//load data
				load(algorithms[algorithm], problems[problem]);
				int count = 0;
				int band = 1000000;

				//find lowest band reaching attainment
				for (int i=0; i<=1000000; i+=bandWidth) {
					for (int j=0; j<Math.min(metrics.length, parameters.length); j++) {
						if ((parameters[j][evalIndex] >= i) && (parameters[j][evalIndex] <= i+bandWidth-1)) {
							total++;
							
							if (metrics[j] > threshold) {
								count++;
							}
						}
					}
					
					if (count/(total * bandWidth/1000000.0) >= p) {
						band = i;
						break;
					}
				}
				
				System.out.println("\t" + algorithms[algorithm] + "\t" +
						band + "\t" + ((1000000 - band) / 1000000.0));
			}
		}
	}

	public static void main(String[] args) throws IOException {
		metric = Integer.parseInt(args[0]);
		
//		System.out.println("Best:");
//		runBest();
//		System.out.println();
//		System.out.println();
//		System.out.println("Attainment:");
//		runAttainment();
//		System.out.println();
//		System.out.println();
//		System.out.println("Efficiency (90):");
//		runEfficiency(0.9);
//		System.out.println();
//		System.out.println();
//		System.out.println("Efficiency (75):");
//		runEfficiency(0.75);
//		System.out.println();
//		System.out.println();
//		System.out.println("Efficiency (50):");
//		runEfficiency(0.5);
//		System.out.println();
//		System.out.println();
//		System.out.println("Controllability:");
//		runControllability();
	}

}

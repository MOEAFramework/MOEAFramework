# Release Notes

This page documents notable changes introduced in each chronological release of the MOEA Framework.

## Version 5.0-SNAPSHOT (TBD)

Version 5.0 includes substantial changes over previous versions.  While we encourage users to upgrade to this new
version to access the latest features, please be aware you will likely need to update your code.
  
  * Adds `Objective` and `Constraint` classes.  This includes, for example, being able to specify an objective as either
    `Minimize` or `Maximize`.
    
  * Adds a `name` parameter to `Variable`, `Objective`, and `Constraint`, allowing problems to define custom names for
    each.  If no name is given, the name defaults to `Var<N>`, `Obj<N>`, and `Constr<N>`.
    
  * Variables no longer have a constructor for setting the value.  Instead, the value must be set in a separate call.
    This helps avoid any ambiguity regarding the ordering of arguments.
    ```
    // old version
    RealVariable variable = new RealVariable(0.5, 0.0, 1.0);
    
    // new version
    RealVariable variable = new RealVariable(0.0, 1.0);
    variable.setValue(0.5);
    ```
    
  * Updated "result file" format.  Namely, information about the new objective and constraint types is stored in the
    header, allowing it to interpret the data correctly.
    
  * Removes `Executor` and `Analyzer`.  If previously using the `Analyzer`, switch to `IndicatorStatistics`.
  
  * Reorganized class and package structure.  For instance, `Population` and its subclasses were moved to
    `org.moeaframework.core.population`.  If you see import errors, try updating the import.
    
  * Replaces `EncodingUtils` with static methods on each variable types:
    ```
    // old version
    double[] values = EncodingUtils.getReal(solution);
    
    // new version
    double[] values = RealVariable.getReal(solution);
    ```
    
  * Updated command line tools:
  
    * Several of the command line tools are removed or renamed.  For example, `Evaluator` is now `EndOfRunEvaluator`.
    
    * Adds main entry point for all command line tools via the `cli` script:
      ```
      ./cli --help
      ./cli solve --problem DTLZ2 --algorithm NSGAII --numberOfEvaluations 10000 --output NSGAII_DTLZ2_Runtime.txt
      ./cli calc --problem DTLZ2 --indicator hypervolume NSGAII_DTLZ2_Runtime.txt
      ```
       
  * New parameter definitions and sampling package.  
    ```
    ParameterSet parameters = new ParameterSet(
        Parameter.named("populationSize").asInt().range(100, 1000, 10),
        Parameter.named("sbx.rate").asDecimal().range(0.0, 1.0, 0.1),
        Parameter.named("pm.rate").asDecimal().range(0.0, 1.0, 0.1));
    
    Samples samples = parameters.enumerate();
    ```
    
  * Streams API.  This simplifies common data manipulation and aggregation procedures, such as grouping samples and
    computing the average hypervolume:
    ```
    double averageHypervolume = DataStream.of(samples)
        .map(sample -> {
            NSGAII algorithm = new NSGAII(new DTLZ2(2));
            algorithm.applyConfiguration(sample);
            return algorithm.getResult()
        })
        .groupBy(Groupings.bucket("populationSize", 100))
        .map(result -> hypervolume.evaluate(result))
        .measure(Measures.average());
    ```
  
  * New data store package.  This provides a means to store a large number of output files without needing to manage
    or organize the data.
    ```
    DataStore dataStore = new FileSystemDataStore(new File("results"));
    
    NSGAII algorithm = new NSGAIII(problem);
	algorithm.run(10000);
    	
    Reference reference = Reference.of(algorithm.getConfiguration());
    Container container = dataStore.getContainer(reference);
    Blob blob = container.getBlob("result");
    
    blob.store(algorithm.getResult());
    ```
 

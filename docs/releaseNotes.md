# Release Notes

This page documents notable changes introduced in each chronological release of the MOEA Framework.

## Version 5.1 (In Development)

  * Fixes `ResultFileViewer` tool, which wasn't starting the viewer when invoking the CLI.
  
  * Adds `distributeAll` to `Samples` to evaluate each sample in parallel.
  
  * Redesigns the data store interfaces to avoid "ambiguous method" errors, simplify the available methods, and wraps
    errors in `DataStoreException`.  Also adds a CLI tool for managing data stores.
  
  * Redesigns plot generation by introducing `PlotBuilder` and its subtypes (`XYPlotBuilder`, `HeatMapBuilder`, etc.).
    This provides compile-time checking and improves IDE auto-completion by only allowing valid operations on each
    plot type.  The `Plot` class will be deprecated and removed in a future release.


## Version 5.0 (17 Jan 2025)

Version 5.0 includes substantial changes over previous versions.  While we encourage users to upgrade to this new
version to access the latest features, please be aware you will likely need to update your code.
  
  * Adds `Objective` and `Constraint` classes.  This includes, for example, being able to specify an objective as either
    `Minimize` or `Maximize`.
    [Example](../examples/org/moeaframework/examples/srinivas/Srinivas.java#L63-L76)
    
  * Adds an optional `name` parameter to `Variable`, `Objective`, and `Constraint`.  When provided, this name is used
    when displaying or saving results, providing a way to store additional context or self-documentation.
    
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
    
  * Removes `Executor` and `Analyzer`.  Instead, algorithms should be created using their constructors and analysis
    performed using `IndicatorStatistics`.
    [Example](../examples/org/moeaframework/examples/indicators/IndicatorStatisticsExample.java)
  
  * Reorganized class and package structure.  For instance, `Population` and its subclasses were moved to
    `org.moeaframework.core.population`.  If you see import errors, try updating the import.
    
  * Replaces `EncodingUtils` with static methods on each variable type:
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
    [Example](../examples/org/moeaframework/examples/experiment/ParameterSampleExample.java#L44-L50)
    
  * Streams API.  This simplifies common data manipulation and aggregation procedures, such as grouping samples and
    computing the average hypervolume.
    [Example](../examples/org/moeaframework/examples/experiment/ParameterSampleExample.java#L65-L69)
  
  * New data store package.  This provides a means to store a large number of output files without needing to manage
    or organize the data.
    [Example](../examples/org/moeaframework/examples/experiment/DataStoreExample.java#L55-L74)
 
  * Adds example for Generalized Decomposition.
    [Example](../examples/org/moeaframework/examples/generalizedDecomposition/GeneralizedDecompositionExample.java)

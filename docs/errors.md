# Known Error and Warning Messages

The following is a list of the known errors and warning messages along with the cause, impact, and suggest fixes (if known).

## Errors

Errors halt the execution of the program and produce an error message to the standard error stream (i.e., the console).  Most errors can be corrected by the
user.

**Exception in thread "main" java.lang.NoClassDefFoundError: \<class\>**  
Thrown when Java is starting but is unable to find the specified class.  Ensure the specified class is located on the Java classpath.  If the class
is located in a JAR file, use
```
java -classpath "$CLASSPATH:/path/to/library.jar" ...
```
If the class is an individual `.class` file in a folder, use
```
java -classpath "$CLASSPATH:/path/to/folder/"
```
Also ensure you are using the correct classpath separator.  Linux users will use the colon (:) as the above examples demonstrate.  Windows and Cygwin
users should use the semi-colon (;).

**Error occurred during initialization of VM**  
**Too small initial heap for new size specified**  
This Java error occurs when the initial heap size (allocated memory) is too small to instantiate the Java virtual machine (VM).  This error is likely
caused by the `-Xmx` command line option requesting less memory than is necessary to start the VM.  Increasing the `-Xmx` value may resolve this issue.
Also ensure the `-Xmx` argument is properly formatted.  For instance, use `-Xmx128m` and NOT `-Xmx128`.

**Error occurred during initialization of VM**  
**Could not reserve enough space for object heap**  
**Could not create the Java virtual machine**  
This Java error occurs when there is insufficient heap size (allocated memory) to instantiate the Java virtual machine (VM).  This error is likely
caused by the `-Xmx` command line option requesting more memory than is available on the host system.  This error may also occur if other running
processes consume large quantities of memory.  Lowering the `-Xmx` value may resolve this issue.
  
**Exception in thread "main" java.lang.OutOfMemoryError: GC overhead limit exceeded**  
Java relies on a garbage collector to detect and free memory which is no longer in use.  This process is usually fast.  However, if Java determines it
is spending too much time performing garbage collection (98% of the time) and is only recovering a small amount of memory (2% of the heap), this error is
thrown.  This is likely caused when the in-use memory approaches the maximum heap size, leaving little unallocated memory for temporary objects.  Try 
increasing the maximum heap size with the `-Xmx` command line argument.
  
**Assertion failed: fp != NULL, file \<filename\>, line \<linenumber\>**  
PISA modules communicate using the file system.  Some anti-virus software scans the contents of files before read and after write operations.  This may
cause one of the PISA communication files to become inaccessible and cause this error.  To test if this is the cause, try disabling your anti-virus
and re-run the program.
  
A more permanent and secure solution involves adding an exception to the anti-virus software to prevent active monitoring of PISA communication files.
For example, first add the line
```
java.io.tmpdir=<folder>
```
to `moeaframework.properties` and set `<folder>` to some temporary folder where the PISA communication files will be stored.  Then configure your anti-virus 
software to ignore the contents of `<folder>`.

**problem does not have an analytical solution**  
Attempted to use `SetGenerator` to produce a reference set for a problem which does not implement `AnalyticalProblem`.  Only `AnalyticalProblem`s, which
provide a method for generating Pareto optimal solutions, can be used with `SetGenerator`.
  
**input appears to be newer than output**  
Several of the command line utilities read entries in an input file and write the corresponding outputs to a separate output file.  If the last
modified date on the input file is newer than the date on the output file, this exception is thrown.  This suggests that the input file has been
modified unexpectedly, and attempting to resume with a partially evaluated output file may result in incorrect results.  To resolve:
  
1. If the input file is unchanged, use the `--force` command line option to override this check.
       
2. If the input file is changed, delete the output file and restart evaluation from the beginning.
  
**no reference set available**  
Several of the command line utilities require a reference set.  The reference set either is provided by the problem (through the `ProblemProvider`), or
supplied by the user via a command line argument.  This exception occurs if neither approach provides a reference set.
  
**reference set contains invalid number of objectives**  
Several of the command line utilities require a reference set.  The reference set file should contain only the objectives.  This error is thrown when the
reference set file contains solutions with an incorrect number of objectives.  Ensure the reference set file is valid and only includes objectives.
  
**unable to load reference set**  
Indicates that a reference set is specified, but it could not be loaded.  The error message should contain additional information about the underlying
cause for the load failure.
  
**output has more entries than input**  
Thrown by the `Evaluator` or `ResultFileEvaluator` command line utilities when attempting to resume evaluation of a partially evaluated file, but the
output file contains more entries than the input file.  This implies the input file was either modified, or a different input file was supplied
than originally used to produce the output file.  Unless the original input file is found, do not attempt to recover from this exception.  Delete the
output file and restart evaluation from the beginning.
  
**maxEvaluations not defined**  
Thrown by the `Evaluator` command line utility if the `maxEvaluations` property has not been defined.  This property must either be defined in the
parameter input file or through the `-x maxEvaluations=<value>` command line argument.
  
**unsupported decision variable type**  
Thrown when the user attempts to use an algorithm that does not support the given problem's decision variable encoding.  For instance, GDE3 only supports
real-valued encodings, and will throw this exception if binary or permutation encoded problems are provided.
  
**not enough bits / not enough dimensions**  
The Sobol sequence generator supports up to 21000 dimensions and can produce up to 2147483647 samples (2^31-1).  While unlikely, if either of these two
limits are exceeded, these exceptions are thrown.
  
**invalid number of parents**  
Attempting to use `CompoundVariation` in a manner inconsistent with its API specification will result in this exception.  Refer to the API documentation
and the restrictions on the number of parents for a variation operator.
  
**binary variables not same length / permutations not same size**  
Thrown by variation operators which require binary variables or permutations of equal length, but the supplied variables differ in length.
  
**invalid bit string**  
Thrown by `ResultFileReader` if either of the following two cases occurs: 1) the binary variable length differs from that specified in the problem
definition; and 2) the string encoding in the file contains invalid characters.  In either case, the binary variable stored in the result file
could not be read.
  
**invalid permutation**  
Thrown by `ResultFileReader` if either of the following two cases occurs: 1) the permutation length differs from that specified in the problem
definition; and 2) the string encoding in the file does not represent a valid permutation.  In either case, the permutation stored in the result file
could not be read.
  
**no provider for \<name\>**
Thrown by the service provider interface (`org.moeaframework.core.spi`) codes when no provider for the requested service is available.  Check the
following:
  
1. If a nested exception is reported, the nested exception will identify the failure.
       
2. Ensure `<name>` is in fact provided by a built-in or third-party provider.  Check spelling and case sensitivity.
       
3. If `<name>` is supplied by a third-party provider, ensure the provider is located on the Java classpath.  If the provider is in a JAR file, use
   ```
   java -classpath "$CLASSPATH:/path/to/provider.jar" ...
   ```
   If the provider is supplied as class files in a folder, use
   ```
   java -classpath "$CLASSPATH:/path/to/folder/"
   ```
   Also ensure you are using the correct classpath separator.  Linux users will use the colon (:) as the above examples demonstrate.  Windows and
   Cygwin users should use the semi-colon (;).

**error sending variables to external process**  
**error receiving variables from external process**  
Thrown when communicating with an external problem, but an I/O error occurred that disrupted the communication.  Numerous situations may cause this
exception, such as the external process terminating unexpectedly.
  
**end of stream reached when response expected**  
Thrown when communicating with an external process, but the connection to the external process closed.  This is most likely the result of an error on the
external process side which caused the external process to terminate unexpectedly.  Error messages printed to the standard error stream should
appear in the Java error stream.

**response contained fewer tokens than expected**  
Thrown when communicating with an external problem, and the external process has returned an unexpected number of entries.  This is most likely a
configuration error where the defined number of objectives or constraints differs from what is actually returned by the external process.
  
**unable to serialize variable**  
Attempted to serialize a decision variable to send to an external problem, but the decision variable is not one of the supported types.  Only real
variables are supported.

**restart not supported**  
PISA supports the ability to reuse a selector after a run has completed.  The MOEA Framework currently does not support this feature.  This exception
is thrown if the PISA selector attempts to reset.

**expected END on last line**  
**unexpected end of file**  
**invalid selection length**  
These exceptions are thrown when communicating with PISA processes, and the files produced by the PISA process appear to be incomplete or malformed.
Check the implementation of the PISA codes to ensure they follow the correct protocol and syntax.
  
**invalid variation length**  
This exception is caused by an incorrect configuration of PISA.  The following equality must hold
```
children * (mu / parents) = lambda,
```
where `mu` is the number of parents selected by the PISA process, `parents` is the number of parent solutions required by the variation operator, `children`
is the number of offspring produced by a single invocation of the variation operator, and `lambda` is the total number of offspring produced during a
generation.

**unexpected rule separator**  
**rule must contain at least one production**  
**invalid symbol**  
**rule must start with non-terminal**  
**rule must contain at least one production**  
**codon array is empty**  
Each of these exceptions originates in the grammatical evolution code, and indicate specific errors when loading or processing a context free grammar.
The specific error message details the cause.

**unable to mkdir \<directory\>**  
For an unknown reason, the underlying operating system was unable to create a directory.  Check to ensure the location of the directory is writable.  One
may also manually create the directory.
  
**no scripting engine for extension \<ext\>**  
**no scripting engine for \<name\>**  
Attempted to use the Java Scripting APIs, but no engine for the specified file extension or name could be found.  To resolve:
  
1. Check that the extension is valid.  If not, supply the file extension for the scripting language required.
       
2. Ensure the scripting language engine is listed on the classpath.  The engine, if packaged in a JAR, can be specified with:
   ```
   java -classpath "$CLASSPATH:/path/to/engine.jar"
   ```
   Also ensure you are using the correct classpath separator.  Linux users will use the colon (:) as the above example demonstrates.  Windows and
   Cygwin users should use the semi-colon (;).

**file has no extension**  
Attempted to use a script file with `ScriptedProblem`, but the filename does not contain a valid extension.  Either supply the file extension for the
scripting language required, or use the constructor which accepts the engine name as an argument.

**scripting engine not invocable**  
Thrown when using a scripting language engine which does not implement the `Invocable` interface.  The scripting language does not support methods or
functions, and thus can not be used as intended.
  
**requires two or more groups**  
Attempted to use one of the n-ary statistical tests which require at least two groups.  Either add a second group to compare against, or remove the
statistical test.
  
**could not locate resource \<name\>**  
Thrown when attempting to access a resource packages within the MOEA Framework, but the resource could not be located.  This is an error with the
distribution.  Please contact the distributor to correct this issue.
  
**insufficient number of entries in row**  
Attempted to read a data file, but the row was missing one or more entries.  The exact meaning depends on the specific data file, but generally this error
means the file is incomplete, improperly formatted or corrupted.  See the documentation on the various file types to determine if this error can be
corrected.
  
**invalid entry in row**  
Attempted to read a data file, but an entry was not formatted correctly.  See the documentation on the various file types to determine if this error
can be corrected.

**invoke calculate prior to getting indicator values**  
Attempted to retrieve one of the indicator values prior to invoking the calculate method.  Ensure the calculate method is invoked first, prior
When using QualityIndicator, the calculate method must be invoked prior to retrieving any of the indicator values.
  
**not a real variable**  
**not a binary variable**  
**not a boolean variable**  
**not a permutation**  
The `EncodingUtils` class handles all the type checking internally.  If any of the arguments are not of the expected type, one of these exceptions is
thrown.  Ensure the argument is of the expected type.  For example, ensure variable is a `BinaryVariable` when calling `getBinary(variable)`.

**invalid number of values**  
**invalid number of bits**  
Attempted to set the decision variable values using an array, but the number of elements in the array does not match the required number of elements.
For `setReal` and `setInt`, ensure the number of real-valued/integer-valued decision variables being set matches the array length.  For `setBinary`,
ensure the number of bits expressed in the binary variable matches the array length.
  
**lambda function is not valid**  
In genetic programming, a lambda function was created with an invalid body.  The body of a lambda function must be fully defined and strongly typed.  If
not, this exception is thrown.  Check the definition of the lambda function and ensure all arguments are non-null and are of the correct type.  Check the
error output to see if any warning messages were printed that detail the cause of this exception.
  
**index does not reference node in tree**  
Attempted to use one of the `node.getXXXAt()` methods, but the index referred to a node not within the tree.  This is similar to an out-of-bounds
exception, as the index pointed to a node outside the tree.  Ensure the index is valid.
  
**malformed property argument**  
The `Evaluator` and `Solve` command line utilities support setting algorithm parameters on the command line with the `-x` option.  The parameters should be
of the form:
```
-x name=value
```
or if multiple parameters are set:
```
-x name1=value1;name2=value2;name3=value3
```
This error is thrown if the command line argument is not in either of these two forms.  Check the command line argument to ensure it is formatted
correctly.
  
**key not defined in accumulator: \<key\>**  
Thrown when attempting to access a key in an `Accumulator` object that is not contained within the `Accumulator`.  Use `accumulator.keySet()` to see what keys
are available and ensure the requested key exists within the accumulator.
  
**an unclean version of the file exists from a previous run, requires manual intervention**  
Thrown when `ResultFileWriter` or `MetricFileWriter` attempt to recover data from an interrupted run, but it appears there already exists an "unclean" file from
a previous recovery attempt.  If the user believes the unclean file contains valid data, she can copy the unclean file to its original location.  Or, she
can delete the unclean file to start fresh.  The `org.moeaframework.analysis.sensitivity.cleanup` property in `moeaframework.properties` controls the default
behavior in this scenario.
  
**requires at least two solutions**  
**objective with empty range**  
These two exceptions are thrown when using the `Normalizer` with a degenerate population.  A degenerate population either has fewer than two solutions or
the range of any objective is below computer precision.  In this scenario, the population can not be normalized.

**lower bound and upper bounds not the same length**  
When specifying the `--lowerBounds` and `--upperBounds` arguments to the `Solve` utility, the number of values in the comma-separated list must match.

**invalid variable specification \<value\>, not properly formatted**  
**invalid real specification \<value\>, expected R(\<lb\>,\<ub\>)**  
**invalid binary specification \<value\>, expected B(\<length\>)**  
**invalid permutation specification \<value\>, expected P(\<length\>)**  
**invalid variable specification \<value\>, unknown type**  
The `--variables` argument to the `Solve` utility allows specifying the types and ranges of the decision variables.  These error messages indicate that one or
more of the variable specifications is invalid.  The message will identify the problem.  An example variable specification is provided below:
```
--variables "R(0;1),B(5),P(10),R(-1;1)"
```
Also, always surround the argument with quotes as shown in this example.

**must specify either the problem, the variables, or the lower and upper bounds arguments**  
The `Solve` command line utility operates on both problems defined within the MOEA Framework (by name) or problems external to the MOEA Framework, such as
an executable.  For problems identified by name, the `--problem` argument must be specified.  For external problems, (1) if the problem is real-valued, you
can use the `--lowerBounds` and `--upperBounds` arguments; or (2) use the `--variables` argument to specify the decision variables and their types.

## Warnings

Warnings are messages printed to the standard error stream (i.e., the console) that indicate an abnormal or unsafe condition.  While warnings do not indicate
an error occurred, they do indicate caution is required by the user.

**saving result file without variables, may become unstable**  
Occurs when writing a result file with the output of decision variables suppressed.  The suppression of decision variable output is a user-specified
option.  The warning "may become unstable" indicates that further use of the result file may result in unexpected errors if the decision variables are
required.
  
**unsupported decision variable type, may become unstable**  
Occurs when reading or writing result files which use unsupported decision variable types.  When this occurs, the program is unable to read or write
the decision variable, and its value is therefore lost.  The warning "may become unstable" indicates that further use of the result file may result
in unexpected errors if the decision variables are required.
  
**duplicate solution found**  
Issued by `ReferenceSetMerger` if any of the algorithms contribute identical solutions.  If this warning is emitted, the contribution of each algorithm
to the reference set is invalid.  Use `SetContribution` instead to compute the contribution of overlapping sets to a reference set.
  
**can not initialize unknown type**  
Emitted by `RandomInitialization` if the problem uses unsupported decision variable types.  The algorithm will continue to run, but the unsupported
decision variables will remain initialized to their default values.

**an error occurred while writing the state file**  
**an error occurred while reading the state file**  
Occurs when checkpoints are enabled, but the algorithm does not support checkpoints or an error occurred while reading or writing the checkpoint.  
The execution of the algorithm will continue normally, but no checkpoints will be produced.
  
**multiple constraints not supported, aggregating into first constraint**  
Occurs when an algorithm implementation does not support multiple constraints.  This occurs primarily with the JMetal library, which only uses a single
aggregate constraint violation value.  When translating between JMetal and the MOEA Framework, the first objective in the MOEA Framework is assigned
the aggregate constraint violation value; the remaining objectives become 0.

**increasing MOEA/D population size**  
The population size of MOEA/D must be at least the number of objectives of the problem.  If not, the population size is automatically increased.
  
**checkpoints not supported when running multiple seeds**  
Emitted by the `Executor` when the `withCheckpointFile(...)` and `accumulateAcrossSeeds(...)` options are both used.  Checkpoints are only
supported for single-seed evaluation.  The `Executor` will continue without checkpoints.
  
**checkpoints not supported by algorithm**  
Emitted by the `Executor` if the algorithm is not `Resumable` (i.e., does not support checkpoints).  The `Executor` will continue without checkpoints.
  
**Provider org.moeaframework.algorithm.jmetal.JMetalAlgorithms could not be instantiated: java.lang.NoClassDefFoundError: \<class\>**  
This warning occurs when attempting to instantiate the JMetal algorithm provider, but the JMetal library could not be found on the classpath.  This
is treated as a warning and not an exception since a secondary provider may exist for the specified algorithm.  If no secondary provider exists, a
`ProviderNotFoundException` will be thrown.  To correct, obtain the latest JMetal library from http://jmetal.sourceforge.net/ and list it on the 
classpath as follows:
```
java -classpath "$CLASSPATH:/path/to/JMetal.jar"
```
Also ensure you are using the correct classpath separator.  Linux users will use the colon (:) as the above example demonstrates.  Windows and Cygwin
users should use the semi-colon (;).
    
**unable to negate values in \<file\>, incorrect number of values in a row**  
Emitted by the `Negater` command line utility when one of the files it is processing contains an invalid number of values in a row.  The file is
expected to contain the same number of values in a row as values passed to the `-d,--direction` command line argument.  The file will not be modified if
this issue is detected.

**unable to negate values in \<file\>, unable to parse number**  
Emitted by the `Negater` command line utility when one of the files it is processing encounters a value it is unable to parse.  The columns being
negated must be numeric values.  The file will not be modified if this issue is detected.
  
**argument is null**  
**\<class\> not assignable from \<class\>**  
When validating an expression tree using the `node.isValid()` method, details identifying why the tree is invalid are printed.  The warning "argument is
null" indicates the tree is incomplete and contains a missing argument.  Check to ensure all arguments of all nodes within the tree are non-null.
The warning "<class> not assignable from <class>" indicates the required type of an argument did not match the return type of the argument.  If this
warning appears when using Sequence, For or While, ensure you specify the return type of these nodes using the appropriate constructor.
  
**unable to parse solution, ignoring remaining entries in the file**  
**insufficient number of entries in row, ignoring remaining rows in the file**  
Occurs when `MetricFileReader` or `ResultFileReader` encounter invalid data in an input file.  They automatically discard any remaining entries in the
file, assuming they are corrupt.  This is primarily intended to allow the software to automatically recover from a previous, interrupted execution.
These warnings are provided to inform the user that invalid entries are being discarded.

**Unable to find the file \<file\>**  
This warning is shown when running an example that must load a data file but the data file could not be found.  Ensure that the examples directory is
located on your classpath:
```
java -classpath "$CLASSPATH:examples" ...
```
Also ensure you are using the correct classpath separator.  Linux users will use the colon (:) as the above example demonstrates.  Windows and Cygwin
users should use the semi-colon (;).

**incorrect number of names, using defaults**  
Occurs when using the `--names` argument provided by `ARFFConverter` and `AerovisConverter` to provide custom names for the decision variables and/or
objectives, but the number of names provided is not correct.  When providing names for only the objectives, the number of names must match the number of
objectives.  When providing names for both variables and objectives, the number of names must match the number of variables and objectives in the data
file.  Otherwise, this warning is displayed and the program uses default names.

**population is empty, can not generate ARFF file**  
The `ARFFConverter` outputs an ARFF file using the last entry in a result file. If the last entry is empty, then no ARFF file is generated.

**properties not accessed: \<property\>**
Warns when a property was set but never read when executing the algorithm.  This is not necessarily a problem, but could indicate a typo or
incorrectly configured algorithm or operator.

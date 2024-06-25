# Large-Scale Experiments

Starting in 4.3, support for running large-scale experiments was improved with the addition of the
`org.moeaframework.experiment` package.

## Concepts / Terminology

### Schema

A schema defines the structure of an experiment.  It defines the fields, which typically map to the parameters of
interest, and their types.  Suppose we want to test the performance of different algorithms and population sizes,
repeating some number of seeds for each run.  Our schema would represet this as follows:

```java
Schema schema = Schema.of(
		Field.named("algorithm").asString(),
		Field.named("populationSize").asInt(),
		Field.named("seed").asInt());
```
   
### Parameter Set

The parameter set defines the range of values we are investigating for each parameter of interest.  Each parameter
should correspond to a field in the schema, unless the parameter is a constant value.  Parameters come in two flavors:
(1) enumerated and (2) sampled.  An **enumerated** parameter has a fixed set of possible values:

```java
EnumeratedParameterSet parameterSet = new EnumeratedParameterSet(
		schema,
		Parameter.named("problem").asConstant("DTLZ2_2"),
		Parameter.named("maxEvaluations").asConstant(25000),
		Parameter.named("algorithm").withValues("NSGAII", "eMOEA", "MOEAD"),
		Parameter.named("populationSize").withValues(50, 100, 150, 200, 250, 300, 350, 400, 450, 500),
		Parameter.named("seed").asInt().range(0, 10));
```


A **sampled** parameter is randomly sampled between some lower and upper bounds:

```java
Parameter.named("sbx.rate").asDecimal().sampledBetween(0.0, 1.0)
```
   
### Data Store

The data store abstracts how results from the experiment are persisted.  For instance, we provide a 
`FileSystemDataStore` that saves results to files on the local disk, but one could also develop storage backed
by a database or on the cloud.

```java
DataStore dataStore = new FileSystemDataStore(schema, HierarchicalFileMap.at(new File("results")));
```

We will discuss saving and loading content later after introducing all terminology.

### Samples

From a parameter set, we can generate samples.  For **enumerated** parameters, the "cross-product" of all possible
parameter values is produced.  For **sampled** parameters, we request a specific number of samples along with the
sampling strategy.  Using the above example with enumerated parameters:

```java
Samples samples = parameterSet.generate();
```

Then, in general, we perform some operation on each sample using a loop:

```java
for (TypedProperties sample : samples) {
	... do something ...
}
```
   
### Keys

Keys uniquely identify each sample or input.  This is analogous to a primary key in a database.  We can both get the
key for a specific sample:

```java
Key key = Key.from(schema, sample);
```

or locate the sample matching the key:

```java
TypedProperties sample = samples.get(key);
```

Keys can also represent a prefix, in which case they can match multiple samples.  For example, if we wanted to find
all samples the the algorithm NSGA-II, we could use:

```java
Key filterKey = Key.from(schema, "NSGAII");
Samples filteredSamples = samples.filter(filterKey);
```

Convenience methods are provided for organizing and aggregating the data.  For instance, here we partition the samples
by the algorithm used:

```java
Map<Key, Samples> partitionedSamples = samples.partition("algorithm");

for (Map.Entry<Key, Samples> entry : partitionedSamples.entrySet()) {
	... process samples for each algorithm ...
}
```

## Saving and Loading Data

We use the `DataStore` to save and load data.  Each piece of data is referenced by its `Key` and `DataType`.
Think of `DataType` like the file extension.  It simply identifies what kind of data is stored.  Several common types
are already defined, such as `DataType.APPROXIMATION_SET`.

When saving data, we first create a writer for the given `Key` and `DataType`, then call either `asText` for
storing textual data or `asBinary` for binary data.

```java
try (TransactionalOutputStream out = dataStore.writer(key, DataType.APPROXIMATION_SET).asBinary()) {
	approximationSet.saveBinary(out);
	out.commit();
}
```

Note that the returned output stream is "transactional", which requires us to call `commit()` to indicate the file
was completely and successfully written.  This is important to prevent partial or incomplete data being stored.  Thus,
always call `commit()` as shown above.

Then, to load the data we previously stored:

```java
try (InputStream in = dataStore.reader(key, DataType.APPROXIMATION_SET).asBinary()) {
	approximationSet = Population.loadBinary(in);
}
```

## Validation

One of the advantages of splitting the design of an experiment in this manner is it enables safety checks to ensure
an experiment remains consistent even if changes occurs.  For example, suppose after performing the above experiment
we decide we want to include an additional algorithm, GDE3.  We can simply update the parameter set with:

```java
Parameter.named("algorithm").withValues("NSGAII", "eMOEA", "MOEAD", "GDE3")
```

The structure, i.e., the schema, didn't change so the layout of data is unchanged.  Additionally, all existing results
still exist.  We are simply appending the results from GDE3.

However, a breaking change would occur if we wanted to add another parameter.  This does change the schema and results
in an error:

```
Exception in thread "main" org.moeaframework.experiment.store.fs.ManifestValidationException: Schemas do not match, expected Schema[{algorithm=java.lang.String},{populationSize=java.lang.Integer},{seed=java.lang.Integer},{sbx.rate=java.lang.Double}] but was Schema[{algorithm=java.lang.String},{populationSize=java.lang.Integer},{seed=java.lang.Integer}]
	at org.moeaframework.experiment.store.fs.FileSystemDataStore.createOrValidateManifest(FileSystemDataStore.java:94)
	at org.moeaframework.experiment.store.fs.FileSystemDataStore.<init>(FileSystemDataStore.java:51)
```

We would either need to modify the existing data to fit the new schema, or essentially discard the results and re-run
the experiment.  Therefore, it's a good idea to plan ahead.  You can always setup a schema and simply pass in a
constant value for a parameter, then later change it to an enumerated or sampled parameter.
   
## Jobs

Jobs are pre-defined computational steps in an experiment, such as evaluating an algorithm and saving the approximation
set, computing performance indicators, merging approximation sets, statistical analysis, etc.  Jobs can be thought of
like a function, taking some input(s) and producing some output(s).  However, the inputs and outputs are not passed
directly between jobs, instead they reference content in the data store.  This has a few advantages:

1. By publicizing their inputs and outputs, we can deduce the relationship or dependencies between jobs, ensuring they
   run in the correct sequence.
2. We can determine if a job previously ran successfully by checking if the output exists in the data store, skipping
   such jobs.
   
All of this logic is handled by the `Experiment` class.  Jobs are submitted to the `Experiment`, which then
sequences and runs jobs based on their dependencies.  It can even parallelize the jobs!  Here's an example where we
evaluate the algorithm and compute the performance indicators for each sampled input:

```java
Experiment experiment = new Experiment(dataStore, Executors.newFixedThreadPool(4), Logger.getDefault());
			
for (TypedProperties input : parameterSet.generate()) {
	Key key = Key.from(schema, input);
	
	experiment.submit(new EndOfRunJob(key, input));
	experiment.submit(new EvaluateIndicatorsJob(key, input));
}

experiment.shutdownAndWait();
```

Jobs immediately start running when submitted (assuming all dependencies are satisfied).  We recommend calling
`shutdownAndWait()` after all jobs are submitted, which waits until all jobs complete.

The output will look something like:

```
Starting EndOfRunJob([algorithm=NSGAII,populationSize=50,seed=0])
Queueing EvaluateIndicatorsJob([algorithm=NSGAII,populationSize=50,seed=0]), waiting on one or more dependencies...
Starting EndOfRunJob([algorithm=NSGAII,populationSize=50,seed=1])
Queueing EvaluateIndicatorsJob([algorithm=NSGAII,populationSize=50,seed=1]), waiting on one or more dependencies...
...
Processing 400 jobs (200 EvaluateIndicatorsJob, 200 EndOfRunJob)
Completed EndOfRunJob([algorithm=NSGAII,populationSize=50,seed=1])
Starting EvaluateIndicatorsJob([algorithm=NSGAII,populationSize=50,seed=1])
Processing 399 jobs (200 EvaluateIndicatorsJob, 199 EndOfRunJob)
...
All jobs completed!
```

## Stale Data

During an experiment, you may decide to modify some aspects of the experiment.  As a result, data previously stored
can become stale.  There are some built-in safeguards to detect stale data, such as comparing the timestamps when the
data was created.  When stale data is detected, the stale files are automatically cleaned up and re-run.  The following
message is also displayed at the end of the experiment:

> Detected stale data during execution.  Most jobs should automatically clean up and re-evaluate any
> stale data, but we also recommend re-running the experiment until this message no longer appears.

However, there are some cases that can not be detected and handled automatically, such as removing a parameter.  We
can force the re-evaluation of all "derivative" jobs.  Our definition of derivative here are jobs that depend on data
produced by other jobs.  For instance, the job that executes the algorithm is **not** derivative as it depends only
on the sample input, but jobs performing statistical analysis or plotting results are derivative.

Below is a code snippet showing how we can check if the samples changed, and if so update the stored samples file and
re-run all derivative jobs.

```java
Samples samples = parameterSet.generate();
		
if (!samples.matchesStoredSamples(dataStore)) {
	samples.save(dataStore);
	experiment.markDerivativeJobsAsStale(true);
}
```

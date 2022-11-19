# The Diagnostic Tool

The diagnostic tool is a convenient GUI for visualizing and comparing the performance of algorithms.  In the screenshot
below, it is comparing NSGA-II and $\epsilon$-MOEA on the UF1 test problem.  Note the controls on the left-hand side let you
select different problem, algorithms, and quality indicators.

![image](https://user-images.githubusercontent.com/2496211/202853310-2e41b809-7997-4b30-865a-cd4fce2ed36f.png)

## Starting the Diagnostic Tool

On Windows, run the `launch-diagnostic-tool.bat` to start this program.  On other systems, run the following command:

```
java -classpath "lib/*" org.moeaframework.analysis.diagnostics.LaunchDiagnosticTool
```

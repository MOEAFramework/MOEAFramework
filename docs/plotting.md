# Plotting Data

While not intended to offer comprehensive plotting capabilities, we do provide classes for generating basic 2D plots.
All plotting code utilizes a builder pattern, whereby the plot is constructed by making one or more calls to configure
the plot.  Below we demonstrate creating and styling the various plot types.


## XY Plots

XY plots display one or more series of data along the X and Y axes.  The specific method that is called controls how
the data is rendered.

### Line Plot

Here we plot a line graph of $$f(x) = x^2$$ for $$x \in [-1, 1]$$.  We can provide arrays directly to the plotting
methods:

<!-- :code: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=linePlotXYData -->

```java
int N = 100;
double[] x = new double[N];
double[] y = new double[N];

for (int i = 0; i < N; i++) {
    x[i] = -1.0 + 2.0 * i / (N - 1);
    y[i] = Math.pow(x[i], 2.0);
}

new XYPlotBuilder()
        .line("Series", x, y)
        .title("Line Plot")
        .xLabel("X")
        .yLabel("Y")
        .show();
```

<!-- :plot: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=linePlotXYData dest=imgs/plot-line.png width=70% -->

<p align="center">
	<img src="imgs/plot-line.png" width="70%" />
</p>

Plots can also be generated directly from our stream classes.  The data, stored as a `Partition` (which is just a
collection of key-value pairs, is identical to the previous example, but expressed in a more compact and readable style.

<!-- :code: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=linePlotPartition -->

```java
Partition<Double, Double> data = DataStream.range(-1.0, 1.0, 100).map(d -> Math.pow(d, 2));

new XYPlotBuilder()
        .line("Series", data)
        .title("Line Plot")
        .xLabel("X")
        .yLabel("Y")
        .show();
```

### Scatter Plot

<!-- :code: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=scatterPlot -->

```java
double[] x = IntStream.range(0, 100).mapToDouble(i -> -1.0 + 2.0 * i / 99).toArray();
double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();

new XYPlotBuilder()
        .scatter("Series", x, y)
        .title("Scatter Plot")
        .xLabel("X")
        .yLabel("Y")
        .show();
```

<!-- :plot: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=scatterPlot dest=imgs/plot-scatter.png width=70% -->

<p align="center">
	<img src="imgs/plot-scatter.png" width="70%" />
</p>

### Combined Plots / Multiple Series

<!-- :code: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=combinedPlot -->

```java
double[] x = IntStream.range(0, 100).mapToDouble(i -> -1.0 + 2.0 * i / 99).toArray();
double[] y1 = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();
double[] y2 = DoubleStream.of(x).map(d -> Math.pow(d, 2) + PRNG.nextGaussian(0.0, 0.05)).toArray();

new XYPlotBuilder()
        .line("Series1", x, y1, Style.blue(), Style.large())
        .scatter("Series2", x, y2, Style.red(), Style.circle())
        .title("Combined Plot")
        .xLabel("X")
        .yLabel("Y")
        .show();
```

<!-- :plot: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=combinedPlot dest=imgs/plot-combined.png width=70% -->

<p align="center">
	<img src="imgs/plot-combined.png" width="70%" />
</p>

### Area Plot

<!-- :code: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=areaPlot -->

```java
double[] x = IntStream.range(0, 100).mapToDouble(i -> -1.0 + 2.0 * i / 99).toArray();
double[] y = DoubleStream.of(x).map(d -> 1.0 - Math.pow(d, 2)).toArray();

new XYPlotBuilder()
        .area("Series", x, y)
        .title("Area Plot")
        .xLabel("X")
        .yLabel("Y")
        .show();
```

<!-- :plot: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=areaPlot dest=imgs/plot-area.png width=70% -->

<p align="center">
	<img src="imgs/plot-area.png" width="70%" />
</p>

### Stacked Area Plot

<!-- :code: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=stackedAreaPlot -->

```java
double[] x = IntStream.range(0, 100).mapToDouble(i -> -1.0 + 2.0 * i / 99).toArray();
double[] y1 = DoubleStream.of(x).map(d -> 1.0 - Math.pow(d, 2)).toArray();
double[] y2 = DoubleStream.of(x).map(d -> Math.pow(d, 2)).toArray();

new XYPlotBuilder()
        .stacked("Series1", x, y1)
        .stacked("Series2", x, y2)
        .title("Stacked Area Plot")
        .xLabel("X")
        .yLabel("Y")
        .show();
```

<!-- :plot: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=stackedAreaPlot dest=imgs/plot-stackedArea.png width=70% -->

<p align="center">
	<img src="imgs/plot-stackedArea.png" width="70%" />
</p>

### Deviation

<!-- :code: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=deviationPlot -->

```java
double[] x = IntStream.range(0, 10000).mapToDouble(i -> i / 1000.0).toArray();
double[] y = DoubleStream.of(x).map(d -> Math.pow(d, 2) + PRNG.nextGaussian(0.0, d)).toArray();

new XYPlotBuilder()
        .deviation("Series", x, y)
        .title("Deviation Plot")
        .xLabel("X")
        .yLabel("Y")
        .show();
```

<!-- :plot: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=deviationPlot dest=imgs/plot-deviation.png width=70% -->

<p align="center">
	<img src="imgs/plot-deviation.png" width="70%" />
</p>

### Histogram

Unlike the previous plots, a histogram is generated from a single array of values.  The Y axis measures the number of
times each value appears in the input.

<!-- :code: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=histogram -->

```java
double[] values = IntStream.range(0, 10000).mapToDouble(i -> PRNG.nextGaussian()).toArray();

new XYPlotBuilder()
        .histogram("Values", values)
        .title("Histogram")
        .xLabel("Value")
        .yLabel("Count")
        .show();
```

<!-- :plot: src=test/org/moeaframework/analysis/plot/XYPlotBuilderTest.java method=histogram dest=imgs/plot-histogram.png width=70% -->

<p align="center">
	<img src="imgs/plot-histogram.png" width="70%" />
</p>

## Heat Map

<!-- :code: src=test/org/moeaframework/analysis/plot/HeatMapBuilderTest.java method=heatMap -->

```java
double[] x = IntStream.range(0, 100).mapToDouble(i -> i / 100.0).toArray();
double[] y = IntStream.range(0, 200).mapToDouble(i -> i / 100.0).toArray();
double[][] z = new double[x.length][y.length];

for (int i = 0; i < x.length; i++) {
    for (int j = 0; j < y.length; j++) {
        z[i][j] = i * Math.sqrt(j);
    }
}

new HeatMapBuilder()
        .xCoords(x)
        .yCoords(y)
        .zData(z)
        .style(Style.showToolTips(), Style.red())
        .xLabel("X")
        .yLabel("Y")
        .zLabel("Value")
        .show();
```

<!-- :plot: src=test/org/moeaframework/analysis/plot/HeatMapBuilderTest.java method=heatMap dest=imgs/plot-heatMap.png width=70% -->

<p align="center">
	<img src="imgs/plot-heatMap.png" width="70%" />
</p>

## Bar Graph

<!-- :code: src=test/org/moeaframework/analysis/plot/BarGraphBuilderTest.java method=barGraph -->

```java
double[] x = IntStream.range(0, 10).mapToDouble(i -> i).toArray();
double[] y1 = IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray();
double[] y2 = IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray();

new BarGraphBuilder()
        .bars("Set1", x, y1)
        .bars("Set2", x, y2)
        .show();
```

<!-- :plot: src=test/org/moeaframework/analysis/plot/BarGraphBuilderTest.java method=barGraph dest=imgs/plot-barGraph.png width=70% -->

<p align="center">
	<img src="imgs/plot-barGraph.png" width="70%" />
</p>

## Box-and-Whisker Plot

<!-- :code: src=test/org/moeaframework/analysis/plot/BoxAndWhiskerPlotBuilderTest.java method=boxAndWhiskerPlot -->

```java
new BoxAndWhiskerPlotBuilder()
        .add("Set1", IntStream.range(0, 10).mapToDouble(i -> PRNG.nextDouble()).toArray())
        .add("Set2", IntStream.range(0, 50).mapToDouble(i -> 2 * PRNG.nextDouble()).toArray())
        .add("Set3", IntStream.range(0, 100).mapToDouble(i -> PRNG.nextDouble()).toArray())
        .show();
```

<!-- :plot: src=test/org/moeaframework/analysis/plot/BoxAndWhiskerPlotBuilderTest.java method=boxAndWhiskerPlot dest=imgs/plot-boxAndWhisker.png width=70% -->

<p align="center">
	<img src="imgs/plot-boxAndWhisker.png" width="70%" />
</p>

## Sensitivity Plot

<!-- :code: src=test/org/moeaframework/analysis/plot/SensitivityPlotBuilderTest.java method=sensitivityPlot -->

```java
StandardChartTheme theme = new StandardChartTheme("Sensitivity");
theme.setPlotBackgroundPaint(Color.WHITE);

SensitivityResult sensitivityResult = getSensitivityResult();

new SensitivityPlotBuilder()
        .data(sensitivityResult)
        .theme(theme)
        .show();
```

<!-- :plot: src=test/org/moeaframework/analysis/plot/SensitivityPlotBuilderTest.java method=sensitivityPlot dest=imgs/plot-sensitivity.png width=70% -->

<p align="center">
	<img src="imgs/plot-sensitivity.png" width="70%" />
</p>

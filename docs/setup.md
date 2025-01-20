# Setup

## Prerequisites

Download and install the latest version of Java Development Kit (JDK).  Version 17 or later is required.  For Windows,
we recommend either [Eclipse Temurin](https://adoptium.net/) or [Azul Zulu](https://www.azul.com/downloads/?package=jdk).
Linux users with Apt can run `sudo apt install default-jdk`.

While not required, we recommend using [Eclipse](http://eclipse.org) as your Java editor.  The instructions found in
these docs are tailored for Eclipse.

## Setting up an Eclipse Project

Download the source or compiled binaries from the [Releases page](https://github.com/MOEAFramework/MOEAFramework/releases).
These are distributed as `.tar.gz` archives that can be extracted using 7-zip (Windows)  or `tar -xzf <file>` (Linux/Mac).

<p align="left">
	<img src="imgs/release-assets.png" width="80%" />
</p>

After extracting the archive, select `File > Open Projects from File System` within Eclipse and open the extracted
directory.  Once imported, you should see the following in the package explorer:

<p align="left">
	<img src="imgs/eclipse-project.png" width="40%" />
</p>

### Running Examples

The examples are contained in the `examples/` directory.  Navigate to the `examples > (default package)`
to view the introductory examples:

<p align="left">
	<img src="imgs/eclipse-examples.png" width="23%" />
</p>

Right-click on an example and select `Run As > Java Application`.  You should soon see output appearing in the
console.

### Command Line

Alternatively, we can also run examples directly from the command line:

```bash
java -classpath "lib/*" examples/Example1.java
```

### Next Steps

Congratulations, you're all set up!  As you get started using the MOEA Framework, check out the `examples/` and `docs/`
to learn more.  If you need to find out information about any specific class, see the published API specification
under `javadoc/`.


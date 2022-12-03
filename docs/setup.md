# Setup

## Prerequisites

Download and install the latest version of Java Development Kit (JDK).  For Windows, we recommend either [Eclipse Temurin](https://adoptium.net/) or
[Azul Zulu](https://www.azul.com/downloads/?package=jdk).  Linux users with Apt can run `sudo apt install default-jdk`.

Download and install the latest version of Eclipse from http://eclipse.org.

## Setting up a Java Project

Download the the compiled binaries from either our website at http://moeaframework.org or the [Releases page](https://github.com/MOEAFramework/MOEAFramework/releases).
After extracting the download, select `File > Open Projects from File System` and open the extracted directory.  Once imported, you should see the following
in the package explorer:

![image](https://user-images.githubusercontent.com/2496211/202720521-40e80ebd-9385-4988-9756-86521224c284.png)

## Running Examples

The examples are contained in the `examples/` directory.  Navigate to the `examples > (default package)` to view the introductory examples:

![image](https://user-images.githubusercontent.com/2496211/202720905-163f3161-0a80-4e16-87bc-836f21143022.png)

Right-click on an example and select `Run As > Java Application`.  You should soon see output appearing in the console.  Congratulations, you're all set up!

### Command Line

Eclipse is not required to use the MOEA Framework - any IDE will work.  We can also run examples directly from the command line:

```bash

java -classpath "lib/*" examples/Example1.java
```

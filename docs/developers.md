# Developer's Guide

## Versioning

We use [semantic versioning](https://semver.org/) following the pattern `{major}.{minor}`.  Two versions with the same
`{major}` number are expected to be backwards compatible, for example allowing one to upgrade from `3.0` to `3.2`
without difficulty.

### Preview Code

In some instances, especially when working directly from the Git default branch, new code is being actively developed.  These
packages and classes should include the `@preview` tag to indicate they are subject to change.

## Java Versions

Each release of the MOEA Framework targets a specific version of Java:

* `3.x` - Java 8+
* `2.x` - Java 6+ (some features are deprecated in Java 16+ and no longer work)

To determine if and when to update which Java version we target, we generally look at the
[support roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) and favor LTS releases.
While supporting earlier versions limits our use of newer language features, the tradeoff is supporting
the widest possible audience.

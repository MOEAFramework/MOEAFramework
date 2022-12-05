# List of Decision Variable

A `Solution` consists of one or more decision variables.  All decision variables derive from the `Variable` interface.  There are a number of built-in
decision variable types along with [mutation and crossover operators](operators.md) for evolving those types.

For convenience, we also provide an `EncodingUtils` class that handles creating, reading, and writing values to decision variables.  It makes converting
between native Java types and decision variables easy.

## Real

Real-valued decision variables store numbers between some lower and upper bounds.  Internally these are represented as double precision (64-bit) floating
point values.

```java

// Creating a real-valued variable:
solution.setVariable(i, EncodingUtils.newReal(lowerBound, upperBound));

// Reading and writing a single variable:
double value = EncodingUtils.getReal(solution.getVariable(i));
EncodingUtils.setReal(solution.getVariable(i), value);

// Reading and writing all variables (when all variables in the solution are real-valued):
double[] values = EncodingUtils.getReal(solution);
EncodingUtils.setReal(solution, values);
```

## Binary

Binary decision variables represent a bit string of a fixed length.  Each bit has the value `0` or `1` (or `false` and `true`).  In Java, you can read
the value either as a `boolean[]` or using a `BitSet`.

```java

// Creating a binary variable:
solution.setVariable(i, EncodingUtils.newBinary(length));

// Reading the values as an array or BitSet:
boolean[] bits = EncodingUtils.getBinary(solution.getVariable(i));
BitSet bits = EncodingUtils.getBitSet(solution.getVariable(i));

// Updating the bits:
EncodingUtils.setBinary(solution.getVariable(i), bits);
```

## Integer

Integers can be represented internally using either the real or binary encoding.  The choice of encoding affects what operators are available.  We generally
recommend using binary.  By default, the binary representation uses [Gray code](https://en.wikipedia.org/wiki/Gray_code) to ensure a single bit
flip can produce an adjacent integer (`X-1` or `X+1`).

```java

// Creating an integer variable:
solution.setVariable(i, EncodingUtils.newInt(lowerBound, upperBound));
solution.setVariable(i, EncodingUtils.newBinaryInt(lowerBound, upperBound));

// Reading and writing a single variable:
int value = EncodingUtils.getInt(solution.getVariable(i));
EncodingUtils.setInt(solution.getVariable(i), value);

// Reading and writing all variables (when all variables in the solution are integers):
int[] values = EncodingUtils.getInt(solution);
EncodingUtils.setInt(solution, values);
```

## Permutation

A permutation is a fixed-length array of numbers $N$ numbers, from $0, ..., N-1$.  The ordering of values in the array is important.  Additionally,
each value can appear in the permutation exactly once.  For example, permutations are used by the Traveling Salesman Problem to specify the order that
cities are visited.

```java

// Creating a permutation:
solution.setVariable(i, EncodingUtils.newPermutation(length));

// Reading and writing a permutation:
int[] permutation = EncodingUtils.getPermutation(solution.getVariable(i));
EncodingUtils.setPermutation(solution.getVariable(i), permutation);
```

## Subset

A subset represents a fixed or variable-length set of $N$ possible elements.  The elements range from $0, ..., N-1$.  Unlike a permutation, the order
of elements in a subset do not matter.  Additionally, each element can appear *at most* once.

There is some overlap, but also key differences, between subsets and binary encodings.  A binary encoding of length $N$ is equivalent to a variable-length
subset that can hold between $0$ and $N$ elements.  The bits represent membership in the set.  However, unlike binary, a subset can require a specific
number of elements in the set.

```java

// Creating a fixed and variable-length subset:
solution.setVariable(i, EncodingUtils.newSubset(fixedSize, numberOfElements));
solution.setVariable(i, EncodingUtils.newSubset(minSize, maxSize, numberOfElements));

// Reading and writing the sets
int[] subset = EncodingUtils.getSubset(solution.getVariable(i));
EncodingUtils.setSubset(solution.getVariable(i), subset);
```

## Program

The program type is useful when generating computer code, rule systems, or decision trees.  The MOEA Framework has a strongly-typed programming
language built into it that can generate and evaluate such programs.

It works by constructing a program tree, where each leaf in the tree is a "node".  A node represents some operation, like a function, with some number
of inputs and an output.  Furthermore, each input and output is typed.  For example, the `Add` node takes two inputs, both numbers, and produces as output
another number.  These types determine how connections can be formed between nodes.

```mermaid
graph TD;
    START[ ] ---|Number| Add
    Add ---|Number| A["Get(#quot;X#quot;)"];
    Add ---|Number| Multiply;
    Multiply ---|Number| B["Get(#quot;Y#quot;)"];
    Multiply ---|Number| C["Constant(2)"];
    
    style START fill-opacity:0, stroke-opacity:0;
```

Check out the code samples in [/examples/org/moeaframework/examples/gp](examples/org/moeaframework/examples/gp).

## Grammar

The grammar type facilitates grammatical evolution.  This is similar in functionality to programs, except it used a context-free grammar given in
[Backus-Naur form](https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form).  For example, the following grammar specification:

```
<expr> ::= <func> | (<expr> <op> <expr>) | <value>
<func> ::= <func-name> ( <expr> )
<func-name> ::= sin | cos | exp | log
<op> ::= + | * | - | /
<value> ::= x | y
```

would generate statements like `sin(x) + cos(x)`, `log(x / y)`, `y - x`, etc.  However, unlike a program, a grammar can generate arbitrary statements,
not necessarily just executable programs.

Regardless of what statements the grammar produces, one then needs a way to "evaluate" the program in terms of fitness.  For executable programs, the
statement can be fed into a scripting language, including any of [Java's supported scripting languages](https://objectcomputing.com/resources/publications/sett/march-2001-scripting-languages-for-java).  Find code samples in [/examples/org/moeaframework/examples/ge](examples/org/moeaframework/examples/ge).

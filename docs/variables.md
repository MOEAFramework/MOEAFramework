# Decision Variable Types

## Real

Real-valued decision variables store numbers between $\pm \inf$ but are typically restricted to some finite lower and upper bounds.  Internally these are
represented as double precision (64-bit) floating point values.

```java

solution.setVariable(i, EncodingUtils.newReal(lowerBound, upperBound));

double value = EncodingUtils.getReal(solution.getVariable(i));
```

## Binary

Binary decision variables represent a bit string of a fixed length.  Each bit has the value `0` or `1` (or `false` and `true`).  

```java

solution.setVariable(i, EncodingUtils.newBinary(length));

boolean[] bits = EncodingUtils.getBinary(solution.getVariable(i));
BitSet bits = EncodingUtils.getBitSet(solution.getVariable(i));
```

## Integer

Integers can be represented internally using either the real or binary encoding.  The choice of encoding affects what operators are available.  We generally
recommend using binary.  By default, the binary representation uses [Gray code](https://en.wikipedia.org/wiki/Gray_code) to ensure a single bit
flip can produce an adjacent integer ($N-1$ or $N+1$).

```java

solution.setVariable(i, EncodingUtils.newInt(lowerBound, upperBound));
solution.setVariable(i, EncodingUtils.newBinaryInt(lowerBound, upperBound));

int value = EncodingUtils.getInt(solution.getVariable(i));
```

## Permutation

A permutation is a fixed-length array of numbers from $0, ..., N-1$ where $N$ is the length of the permutation with some ordering.  For example, permutations are
used in Traveling Salesman Problem to specify the order that cities are visited.

```java

solution.setVariable(i, EncodingUtils.newPermutation(length));

int[] permutation = EncodingUtils.getPermutation(solution.getVariable(i));
```

## Subset

A subset is a fixed or variable-length array of numbers from the set $0, ..., N-1$.  There is some overlap in functionality between subsets and binary
encodings, where the membership in the set can be represented by the value of each bit.  However, subsets provide more control over the number of members
in the set, whereas a binary encoding has no way to limit the number of bits set to `0` or `1`.

```java

solution.setVariable(i, EncodingUtils.newSubset(fixedSize, numberOfElements));
solution.setVariable(i, EncodingUtils.newSubset(minSize, maxSize, numberOfElements));

int[] subset = EncodingUtils.getSubset(solution.getVariable(i));
```

## Program

The program type is useful when generating computer code, rule systems, or decision trees.  The MOEA Framework has a strongly-typed programming
language built into it that can generate executable programs.

It works by constructing a program tree, where each leaf in the tree is a "node".  A node represents some operation, like a function, that takes some
inputs and produce an output.  The inputs and output are typed.  This way, when we evolve the program, we can ensure the types of inputs and outputs
are compatible.

```mermaid
graph TD;
    Add --> A[Get("X")];
    Add --> Multiply;
    Multiply --> B[Get("Y")];
    Multiply --> C[Constant(2)];
```

Check out the code samples in [examples/org/moeaframework/examples/gp](examples/org/moeaframework/examples/gp) for examples.

## Grammar

The grammar type facilitates grammatical evolution.  This is similar in functionality to programs, except it used a well-defined grammar given in
[Backus-Naur form](https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form).  For example, the following grammar specification:

```bnf

<expr> ::= <func> | (<expr> <op> <expr>) | <value>
<func> ::= <func-name> ( <expr> )
<func-name> ::= sin | cos | exp | log
<op> ::= + | * | - | /
<value> ::= x | y
```

would generate expressions like `sin(x) + cos(x)`, `log(x / y)`, `y - x`, etc.

The MOEA Framework then evolves valid programs for the grammar, which can then be fed into a scripting language to evaluate the program.
Check out the code samples in [examples/org/moeaframework/examples/ge](examples/org/moeaframework/examples/ge) for examples.

## Mixing Types

The MOEA Framework is able to automatically recognize and select appropriate variation operators when a solution contains a single type.  However, it is
unable to do so when mixing differen types in the same solution.  Instead, we need to explicitly set the variation operators.




# Decision Variable Types

## Real

Real-valued decision variables store numbers between $\pm \inf$.  Internally these are represented as double precision (64-bit) floating point values.  They always
have a defined lower and upper bound.

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

A subset is a fixed or variable-length array of numbers from the set $0, ..., N-1$.  Unlikes a permutation that contains all elements, subsets only contain some
of the elements.  For example, subsets are often used in Bin Packing / Knapsack problems where we pick some assortment of items to fill the bag.

```java

solution.setVariable(i, EncodingUtils.newSubset(fixedSize, numberOfElements));
solution.setVariable(i, EncodingUtils.newSubset(minSize, maxSize, numberOfElements));

int[] subset = EncodingUtils.getSubset(solution.getVariable(i));
```

## Program

## Grammar


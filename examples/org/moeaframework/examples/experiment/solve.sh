#!/bin/bash

# Demonstrates using the CLI tools to solve the 2-objective DTLZ2 problem using NSGA-II and
# computing the Hypervolume metric

set -ex

./cli solve --problem DTLZ2 --algorithm NSGAII --numberOfEvaluations 10000 --output NSGAII_DTLZ2_Runtime.txt
./cli calc --problem DTLZ2 --indicator hypervolume NSGAII_DTLZ2_Runtime.txt

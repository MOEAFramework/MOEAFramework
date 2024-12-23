#!/usr/bin/env bash

./cli WeightGenerator --method normalboundary --dimension 3 --divisions 20 > nbi_weights.txt
cat nbi_weights.txt | python3 examples/org/moeaframework/examples/generalizedDecomposition/gd.py > gd_weights.txt

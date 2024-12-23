#!/usr/bin/env bash

./cli WeightGenerator --method normalboundary --dimension 3 --divisions 20 > nbi_weights.txt
./cli WeightGenerator --method normalboundary --dimension 3 --divisions 20 --generalized > gd_weights.txt

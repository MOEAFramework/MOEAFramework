#!/usr/bin/env bash

ROOT="$(dirname -- "${BASH_SOURCE[0]}")"

./cli SequenceGenerator --weights normalboundary --dimension 3 --divisions 12 > nbi_weights.txt
cat nbi_weights.txt | python3 "${ROOT}/generalizedDecomposition.py" > gd_weights.txt
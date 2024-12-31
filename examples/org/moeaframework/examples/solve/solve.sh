#!/usr/bin/env bash

SCRIPT_DIR="$(dirname -- "${BASH_SOURCE[0]}")"

./cli Solve --algorithm NSGAII \
    --variables "R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1)" \
    --objectives 2 \
    --numberOfEvaluations 10000 \
    --output NSGAII_DTLZ2_Stdio.txt \
    python3 ${SCRIPT_DIR}/dtlz2.py

./cli Solve --algorithm NSGAII \
    --variables "R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1)" \
    --objectives 2 \
    --numberOfEvaluations 10000 \
    --useSocket \
    --output NSGAII_DTLZ2_Sockets.txt \
    python3 ${SCRIPT_DIR}/dtlz2.py --sockets

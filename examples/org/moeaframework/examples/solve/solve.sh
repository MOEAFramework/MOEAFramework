#!/usr/bin/env bash

# Demonstrates using the Solve CLI tool, which is used to optimize a program or function external to the MOEA Framework.
# It connects to the external program by sending decision variables and reading objectives / constraints either using
# standard I/O or sockets.
#
# Decision variables are specified as a ';'-separated list of:
#
#     Type          Syntax
#     -----------   ----------------------------------
#     Real          R(<lb>,<ub>) or Real(<lb>,<ub>)
#     Integer       I(<lb>,<ub>) or Integer(<lb>,<ub>)
#     Binary        B(<len>) or Binary(<len>)
#     Permutation   P(<len>) or Permutation(<len>)
#     Subset        S(<k>,<n>) or Subset(<k>,<n>)
#
# Objectives are specified either as an integer value, in which case the default direction is used (Minimize), or as
# a ';'-separated list of:
#
#     Type          Syntax
#     -----------   ----------------------------------
#     Minimize      Min or Minimize
#     Maximize      Max or Maximize
#
# Constraints are specified either as an integer value, in which ase the default constraint is used (Equal), or as a
# ';'-separated list of:
#
#     Type                  Syntax
#     -----------           ----------------------------------
#     LessThan              LT(<val>) or LessThan(<val>)
#     LessThanOrEqual       LEQ(<val>) or LessThanOrEqual(<val>)
#     GreaterThan           GT(<val>) or GreaterThan(<val>)
#     GreaterThanOrEqual    GEQ(<val>) or GreaterThanOrEqual(<val>)
#     Equal                 EQ(<val>) or Equal(<val>)
#     NotEqual              NEQ(<val>) or NotEqual(<val>)
#     Between               Between(<lhs>,<rhs>)
#     Outside               Outside(<lhs>,<rhs>)

SCRIPT_DIR="$(dirname -- "${BASH_SOURCE[0]}")"

# Defining real-valued decision variables using lower and upper bounds:
./cli Solve --algorithm NSGAII \
    --lowerBounds "0,0,0,0,0,0,0,0,0,0,0" \
    --upperBounds "1,1,1,1,1,1,1,1,1,1,1" \
    --objectives 2 \
    --numberOfEvaluations 10000 \
    --output NSGAII_DTLZ2_Output1.txt \
    python3 ${SCRIPT_DIR}/dtlz2.py

# Defining real-valued decision variables using the variable specification syntax:
./cli Solve --algorithm NSGAII \
    --variables "R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1)" \
    --objectives 2 \
    --numberOfEvaluations 10000 \
    --output NSGAII_DTLZ2_Output2.txt \
    python3 ${SCRIPT_DIR}/dtlz2.py

# Defining both decision variables and objective direction:
./cli Solve --algorithm NSGAII \
    --variables "R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1)" \
    --objectives "Min;Min" \
    --numberOfEvaluations 10000 \
    --output NSGAII_DTLZ2_Output3.txt \
    python3 ${SCRIPT_DIR}/dtlz2.py

# Using sockets instead of standard I/O:
./cli Solve --algorithm NSGAII \
    --variables "R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1);R(0,1)" \
    --objectives 2 \
    --numberOfEvaluations 10000 \
    --useSocket \
    --output NSGAII_DTLZ2_Output4.txt \
    python3 ${SCRIPT_DIR}/dtlz2.py --sockets

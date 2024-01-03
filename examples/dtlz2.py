# Copyright 2009-2024 David Hadka
#
# This file is part of the MOEA Framework.
#
# The MOEA Framework is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published by
# the Free Software Foundation, either version 3 of the License, or (at your
# option) any later version.
#
# The MOEA Framework is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
# License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
import sys
import math
import functools
import operator

nvars = 11
nobjs = 2

def evaluate(vars):
    k = nvars - nobjs + 1
    g = sum([math.pow(x - 0.5, 2.0) for x in vars[nvars-k:]])
    f = [1.0+g]*nobjs

    for i in range(nobjs):
        f[i] *= functools.reduce(operator.mul,
                [math.cos(0.5 * math.pi * x) for x in vars[:nobjs-i-1]],
                1)

        if i > 0:
            f[i] *= math.sin(0.5 * math.pi * vars[nobjs-i-1])

    return f

if __name__ == "__main__":
    if len(sys.argv) > 1:
        if sys.argv[1] == "-h" or sys.argv[1] == "--help":
            sys.exit(f"Usage: python {sys.argv[0]} <nvars> <nobjs>")
        
        if len(sys.argv) == 2:
            nobjs = float(sys.argv[1])
            nvars = nobjs + 9
        elif len(sys.argv) == 3:
            nvars = float(sys.argv[1])
            nobjs = float(sys.argv[2])

    for line in sys.stdin:
        vars = list(map(float, line.split(' ')))

        if len(vars) != nvars:
            sys.exit(f"Incorrect number of variables (expected: {nvars}, actual: {len(vars)})")

        objs = evaluate(vars)
        print(" ".join(map(str, objs)), flush=True)

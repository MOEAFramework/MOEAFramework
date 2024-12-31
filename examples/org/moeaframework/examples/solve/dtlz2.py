# Copyright 2009-2025 David Hadka
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
import argparse
import functools
import operator
import socket

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
    
def process(input, output):
    for line in input:
        vars = list(map(float, line.strip().split(' ')))

        if len(vars) != nvars:
            sys.exit(f"Incorrect number of variables (expected: {nvars}, actual: {len(vars)})")

        objs = evaluate(vars)
        print(" ".join(map(str, objs)), file=output, flush=True)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Python implementation of the DTLZ2 problem")

    parser.add_argument("-o", "--objs", help="number of objectives (default: 2)", type=int, metavar="<N>", default=2)
    parser.add_argument("-s", "--sockets", help="enable communication over sockets", action="store_true")
    parser.add_argument("-p", "--port", help="port (default: 16802)", type=int, metavar="<N>", default=16801)
    
    args = parser.parse_args()
    
    if args.objs:
        nobjs = args.objs
        nvars = args.objs + 9

    if args.sockets:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
            sock.bind(("127.0.0.1", args.port))
            sock.listen()
            connection, address = sock.accept()
        
            with connection:
                with connection.makefile("rw") as f:
                    process(f, f)
    else:
        process(sys.stdin, sys.stdout)

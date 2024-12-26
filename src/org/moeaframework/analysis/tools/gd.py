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

"""
Generalized decomposition solves the inverse of Chebychev's scalarizing function, finding the weights, w, that produce
the minimum value for a target point, x.  We solve the following convex optimization problem:

  minimize c(w) = norm_inf(x*w)
  subject to
    sum(w) = 1
    0 <= w <= 1

We can solve norm_inf(x*w) by introducing a slack variable, t, along with the constraints x*w <= t and x*w >= -t.  The
resulting LP formulation then minimizes the value of t:

  minimize c(w) = t
  subject to
    sum(w) = 1
    0 <= w <= 1
    x*w <= t
    x*w >= -t

References
----------
1. Giagkiozis, I., R. C. Purshouse, and P. J. Fleming (2013).  "Generalized Decomposition."  Evolutionary
   Multi-Criterion Optimization, 7th International Conference, pp. 428-442.
"""

import sys
import argparse

try:
	from cvxopt import matrix, solvers
except ImportError:
	print("Requires cvxopt, please install with `pip install cvxopt`", file=sys.stderr)
	sys.exit(-1)
	
if __name__ == "__main__":
	try:
		for line in sys.stdin:
			x = matrix(list(map(float, line.strip().split())))
			N = x.size[0]
			
			# cost function
			c = matrix([0.0] * N + [1.0])
			
			# equality constraints
			A = matrix([1.0] * N + [0.0]).T
			b = matrix([1.0])
			
			# inequality constraints
			G = matrix(
				list(map(lambda i: [0.0] * i + [-1.0] + [0.0] * (N-i), range(N))) +
				list(map(lambda i: [0.0] * i + [1.0] + [0.0] * (N-i), range(N))) +
				list(map(lambda i: [0.0] * i + [x[i]] + [0.0] * (N-i-1) + [-1.0], range(N))) +
				list(map(lambda i: [0.0] * i + [-x[i]] + [0.0] * (N-i-1) + [-1.0], range(N)))).T
			h = matrix([0.0] * N + [1.0] * N + [0.0] * (2*N))
			
			# solve and print weight
			solution = solvers.lp(c, G, h, A, b, options={"show_progress": False})
			
			weights = solution['x']
			
			if weights is None:
				print("Failed to solve LP:", solution['status'], file=sys.stderr)
				sys.exit(-1)
				
			print(" ".join(map(lambda i: str(weights[i]), range(N))), flush=True)
	except KeyboardInterrupt:
		pass

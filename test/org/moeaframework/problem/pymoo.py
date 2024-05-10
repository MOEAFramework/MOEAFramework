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
import importlib
import inspect
import numpy

def create_instance(name, n_var, n_obj, n_constr):
    class_ = None

    try:
        module_ = importlib.import_module("pymoo.problems.many")
        class_ = getattr(module_, name)
    except AttributeError:
        pass

    try:
        module_ = importlib.import_module("pymoo.problems.multi")
        class_ = getattr(module_, name)
    except AttributeError:
        pass

    try:
        module_ = importlib.import_module("pymoo.problems.single")
        class_ = getattr(module_, name)
    except AttributeError:
        pass

    if class_ is None:
        raise Exception(f"No problem found with name '{name}'")

    argspec = inspect.getfullargspec(class_)
    args = []

    for arg_name in argspec.args[1:]:
        if arg_name == "n_var":
            args.append(n_var)
        elif arg_name == "n_obj":
            args.append(n_obj)
        elif arg_name == "n_constr":
            args.append(n_constr)
        else:
            raise Exception(f"Unrecognized argument to constructor: '{arg_name}'")

    instance = class_(*args)

    if instance.n_var != n_var:
        raise Exception(f"Expected {n_var} variables, but constructed problem has {instance.n_var}")
    if instance.n_obj != n_obj:
        raise Exception(f"Expected {n_obj} objectives, but constructed problem has {instance.n_obj}")
    if instance.n_constr != n_constr:
        raise Exception(f"Expected {n_constr} constraints, but constructed problem has {instance.n_constr}")

    return instance

if __name__ == "__main__":
    if len(sys.argv) != 5:
        raise Exception("Usage: python pymoo.py <name> <n_var> <n_obj> <n_constr>")

    problem = create_instance(sys.argv[1], int(sys.argv[2]), int(sys.argv[3]), int(sys.argv[4]))

    for line in sys.stdin:
        vars = list(map(float, line.split(' ')))

        if len(vars) != problem.n_var:
            sys.exit(f"Incorrect number of variables (expected: {problem.n_var}, actual: {len(vars)})")

        result = problem.evaluate(numpy.array(vars))

        if isinstance(result, tuple):
            result = numpy.concatenate(result)

        print(" ".join(map(str, result)), flush=True)

# Display the available commands
./cli --help

# Display the current version
./cli --version

# Solve the 2-objective DTLZ2 problem using NSGA-II, then compute the hypervolume metric
./cli solve --problem DTLZ2 --algorithm NSGAII --numberOfEvaluations 10000 --output NSGAII_DTLZ2_Runtime.txt
./cli calc --problem DTLZ2 --indicator hypervolume NSGAII_DTLZ2_Runtime.txt

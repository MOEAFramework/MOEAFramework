importPackage(org.moeaframework.core);
importPackage(org.moeaframework.core.variable);

function getName() {
	return "TestScript";
}

function getNumberOfVariables() {
	return 1;
}

function getNumberOfObjectives() {
	return 1;
}

function getNumberOfConstraints() {
	return 0;
}

function evaluate(solution) {
	solution.setObjective(0, solution.getVariable(0).getValue());
}

function newSolution() {
	var solution = Solution(1, 1);
	solution.setVariable(0, RealVariable(0, 1));
	return solution;
}

function close() {

}
var Solution = org.moeaframework.core.Solution;
var RealVariable = org.moeaframework.core.variable.RealVariable;

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
	var solution = new Solution(1, 1);
	solution.setVariable(0, new RealVariable(0, 1));
	return solution;
}

function close() {

}
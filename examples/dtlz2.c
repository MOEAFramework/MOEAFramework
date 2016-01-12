/* Copyright 2009-2016 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
#include <stdlib.h>
#include <math.h>
#include "moeaframework.h"

#define PI 3.14159265358979323846

int nvars;
int nobjs;

/**
 * Function for evaluating the DTLZ2 problem.
 */
void evaluate(double* vars, double* objs) {
	int i;
	int j;
	int k = nvars - nobjs + 1;
	double g = 0.0;

	for (i=nvars-k; i<nvars; i++) {
		g += pow(vars[i] - 0.5, 2.0);
	}

	for (i=0; i<nobjs; i++) {
		objs[i] = 1.0 + g;

		for (j=0; j<nobjs-i-1; j++) {
			objs[i] *= cos(0.5*PI*vars[j]);
		}

		if (i != 0) {
			objs[i] *= sin(0.5*PI*vars[nobjs-i-1]);
		}
	}
}

/**
 * The main routine for parsing the decision variable inputs, evaluating the
 * problem and printing the objectives.
 */
int main(int argc, char* argv[]) {
	if (argc <= 1) {
		nobjs = 2;
		nvars = 11;
	} else if (argc == 2) {
		nobjs = atoi(argv[1]);
		nvars = nobjs + 9;
	} else if (argc >= 3) {
		nobjs = atoi(argv[2]);
		nvars = atoi(argv[1]);
	} 

	double vars[nvars];
	double objs[nobjs];

#ifdef USE_SOCKET
	MOEA_Init_socket(nobjs, 0, NULL);
#else
	MOEA_Init(nobjs, 0);
#endif

	while (MOEA_Next_solution() == MOEA_SUCCESS) {
		MOEA_Read_doubles(nvars, vars);
		evaluate(vars, objs);
		MOEA_Write(objs, NULL);
	}
	
	MOEA_Terminate();

	return EXIT_SUCCESS;
}


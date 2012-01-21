/* Copyright 2009-2012 David Hadka
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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <math.h>
#include "moeaframework.h"

int nvars = 2;
int nobjs = 1;

/**
 * Function for evaluating the Rosenbrock problem.
 */
void evaluate(double* vars, double* objs) {
  int i;
  double sum = 0.0;

  for (i=0; i<nvars-1; i++) {
    sum += pow(1.0 - vars[i], 2.0) + 
        100.0 * pow(vars[i+1] - pow(vars[i], 2.0), 2.0);
  }
  
  objs[0] = sum;
}

/**
 * The main routine for parsing the decision variable inputs, evaluating the
 * problem and printing the objectives.
 */
int main(int argc, char* argv[]) {
  double vars[nvars];
  double objs[nobjs];
  
  MOEA_Init(nobjs, 0);

  while (MOEA_Next_solution() == MOEA_SUCCESS) {
    MOEA_Read_doubles(nvars, vars);
    evaluate(vars, objs);
    MOEA_Write(objs, NULL);
  }
  
  return EXIT_SUCCESS;
}

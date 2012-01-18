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

int nvars = 2;
int nobjs = 1;

/**
 * Function for evaluating the Rosenbrock problem.
 */
int evaluate(double* vars, double* objs) {
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
  int i;
  int response;
  double vars[nvars];
  double objs[nobjs];

  while (1) {
    //parse the decision variable inputs
    for (i=0; i<nvars; i++) {
      response = fscanf(stdin, "%lf", &vars[i]);
      
      if ((response == EOF) && (ferror(stdin) == 0) && (i == 0)) {
        //End of stream reached, connection closed successfully
        exit(0);
      } else if ((response == EOF) && (ferror(stdin) == 0)) {
        fprintf(stderr, "Unexpectedly reached end of file\n");
        exit(-1);
      } else if (response == EOF) {
        fprintf(stderr, "An I/O error occurred: %s\n", strerror(errno));
        exit(-1);
      } else if (response == 0) {
        fprintf(stderr, "Unable to parse decision variable\n");
        exit(-1);
      }
    }

    //evaluate the problem
    evaluate(vars, objs);
    
    //print the objectives
    fprintf(stdout, "%e", objs[0]);
    for (i=1; i<nobjs; i++) {
      fprintf(stdout, " %e", objs[i]);
    }
    fprintf(stdout, "\n");
    fflush(stdout);
  }
}

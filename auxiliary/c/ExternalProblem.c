/* Copyright 2009-2011 David Hadka
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

//Function for evaluating the problem
int evaluate(double* vars, double* objs) {
  //TODO: add evaluation code
}

//Demonstrates the C program interface for communication with
//org.moeaframework.problem.ExternalProblem
int main(int argc, char* argv[]) {
  int response;
  double* vars = (double*)calloc(4, sizeof(double));
  double* objs = (double*)calloc(2, sizeof(double));

  while (1) {
    response = fscanf(stdin, "%lf %lf %lf %lf", &vars[0], &vars[1], &vars[2],
      &vars[3]);

    if ((response == EOF) && (ferror(stdin) == 0)) {
      //End of stream reached, connection closed successfully
      exit(0);
    } else if (response == EOF) {
      //An I/O error occurred
      fprintf(stderr, "An I/O error occurred: %s\n", strerror(errno));
      exit(-1);
    } else if (response != 4) {
      //Invalid number of decision variables in input
      fprintf(stderr, "Expected 4 values on a line\n");
      exit(-1);
    } else {
      //Correct number of inputs; evaluate and output objectives
      evaluate(vars, objs);
      fprintf(stdout, "%e %e\n", objs[0], objs[1]);
      fflush(stdout);
    }
  }
}

/* Copyright 2009-2019 David Hadka
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
#include "moeaframework.h"

/**
 * This file supports unit tests of the moeaframework.h and moeaframework.c
 * files.  See the ExternalProblemWithCTest test class for details.
 */
int main() {
#ifdef USE_SOCKET
  MOEA_Init_socket(2, 1, NULL);
#else
  MOEA_Init(2, 1);
#endif
  
  double doubles[2];
  int binary[5];
  int permutation[3];
  double objectives[2];
  double constraints[1];
  int count = 0;
  
  while (MOEA_Next_solution() == MOEA_SUCCESS) {
    count++;
    
    MOEA_Read_doubles(2, doubles);
    MOEA_Read_binary(5, binary);
    MOEA_Read_permutation(3, permutation);
    
    /* echo variables back for verification */
    MOEA_Debug("%.17g %.17g %d %d %d %d %d %d %d %d\n", doubles[0], doubles[1],
        binary[0], binary[1], binary[2], binary[3], binary[4], permutation[0],
        permutation[1], permutation[2]);

    objectives[0] = count;
    objectives[1] = 1e-10/count;
    constraints[0] = 1e10*count;

    MOEA_Write(objectives, constraints);
  }
  
  MOEA_Terminate();
  
  return EXIT_SUCCESS;
}
#include <stdlib.h>
#include "moeaframework.h"

int main() {
  MOEA_Init(2, 1);
  
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
    
    //echo variables back for verification
    MOEA_Debug("%.17g %.17g %d %d %d %d %d %d %d %d\n", doubles[0], doubles[1],
        binary[0], binary[1], binary[2], binary[3], binary[4], permutation[0],
        permutation[1], permutation[2]);

    objectives[0] = count;
    objectives[1] = 1e-10/count;
    constraints[0] = 1e10*count;

    MOEA_Write(objectives, constraints);
  }
  
  return EXIT_SUCCESS;
}
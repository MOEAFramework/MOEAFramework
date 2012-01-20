#include <stdio.h>
#include <stdlib.h>
#include "moeaframework.h"

int main() {
  MOEA_Init(2, 0);
  
  double doubles[2];
  int binary[5];
  int permutation[3];
  double objectives[2];
  
  while (MOEA_Next_solution() == MOEA_SUCCESS) {
    MOEA_Read_doubles(2, doubles);
    MOEA_Read_binary(5, binary);
    MOEA_Read_permutation(3, permutation);
    
    //do evaluation
    
    MOEA_Write(objectives, NULL);
    
    MOEA_Debug("debugging:\n");
    MOEA_Debug("  Doubles: %f %f\n", doubles[0], doubles[1]);
    MOEA_Debug("  Binary: %d %d %d %d %d\n", binary[0], binary[1], binary[2], binary[3], binary[4]);
    MOEA_Debug("  Permutation: %d %d %d\n", permutation[0], permutation[1], permutation[2]);
  }
  
  return 0;
}
#ifndef MOEAFRAMEWORK_H
#define MOEAFRAMEWORK_H

typedef enum MOEA_Status {
  MOEA_SUCCESS,
  MOEA_EOF,
  MOEA_PARSE_NO_SOLUTION,
  MOEA_PARSE_EOL,
  MOEA_PARSE_DOUBLE_ERROR,
  MOEA_PARSE_BINARY_ERROR,
  MOEA_PARSE_PERMUTATION_ERROR,
  MOEA_MALLOC_ERROR,
  MOEA_NULL_POINTER_ERROR,
} MOEA_Status;

void MOEA_Error_callback_default(const MOEA_Status);
void (*MOEA_Error_callback)(const MOEA_Status) = &MOEA_Error_callback_default;
char* MOEA_Status_message(const MOEA_Status);

MOEA_Status MOEA_Init(const int, const int);
MOEA_Status MOEA_Next_solution();
MOEA_Status MOEA_Read_double(double*);
MOEA_Status MOEA_Read_doubles(const int, double*);
MOEA_Status MOEA_Read_binary(const int, int*);
MOEA_Status MOEA_Read_permutation(const int, int*);
MOEA_Status MOEA_Write(const double*, const double*);
MOEA_Status MOEA_Debug(const char* format, ...);

#endif

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
#include <stdarg.h>
#include <string.h>
#include <math.h>
#include "moeaframework.h"

#define MOEA_WHITESPACE " \t"
#define MOEA_INITIAL_BUFFER_SIZE 1024

int MOEA_Number_objectives;
int MOEA_Number_constraints;

char* MOEA_Line_buffer = NULL;
size_t MOEA_Line_position = 0;
size_t MOEA_Line_limit = 0;

void MOEA_Error_callback_default(const MOEA_Status status) {
  MOEA_Debug("%s\n", MOEA_Status_message(status));
  exit(-1);
}

void (*MOEA_Error_callback)(const MOEA_Status) = MOEA_Error_callback_default;

const char* MOEA_Status_message(const MOEA_Status status) {
  switch (status) {
  case MOEA_SUCCESS:
    return "Success";
  case MOEA_EOF:
    return "Finished";
  case MOEA_PARSE_NO_SOLUTION:
    return "No solution read, missing call to MOEA_Next_solution()";
  case MOEA_PARSE_EOL:
    return "Attempted to parse variable but at end-of-line";
  case MOEA_PARSE_DOUBLE_ERROR:
    return "Unable to parse double variable";
  case MOEA_PARSE_BINARY_ERROR:
    return "Unable to parse binary variable";
  case MOEA_PARSE_PERMUTATION_ERROR:
    return "Unable to parse permutation variable";
  case MOEA_MALLOC_ERROR:
    return "Error while allocating memory";
  case MOEA_NULL_POINTER_ERROR:
    return "Attempted to dereference NULL pointer";
  default:
    return "Unknown error";
  }
}

MOEA_Status MOEA_Error(const MOEA_Status status) {
  if ((status == MOEA_SUCCESS) || (status == MOEA_EOF)) {
    return status;
  } else if (MOEA_Error_callback == NULL) {
    return status;
  } else {
    MOEA_Error_callback(status);
    return status;
  }
}

MOEA_Status MOEA_Init(const int objectives, const int constraints) {
  MOEA_Number_objectives = objectives;
  MOEA_Number_constraints = constraints;
  
  return MOEA_SUCCESS;
}

MOEA_Status MOEA_Debug(const char* format, ...) {
  va_list arguments;
  
  va_start(arguments, format);
  vfprintf(stderr, format, arguments);
  fflush(stderr);
  va_end(arguments);
  
  return MOEA_SUCCESS;
}

MOEA_Status MOEA_Next_solution() {
  size_t position = 0;
  int character;
  
  if (feof(stdin)) {
    return MOEA_EOF;
  }
  
  while (!feof(stdin)) {
    /* increase line buffer if needed */
    if ((MOEA_Line_limit == 0) || (position >= MOEA_Line_limit-1)) {
      MOEA_Line_limit += MOEA_INITIAL_BUFFER_SIZE;
      
      MOEA_Line_buffer = (char*)realloc(MOEA_Line_buffer, 
          MOEA_Line_limit*sizeof(char));
    
      if (MOEA_Line_buffer == NULL) {
        return MOEA_Error(MOEA_MALLOC_ERROR);
      }
    }
  
    /* process next character */
    character = fgetc(stdin);

    if ((character == EOF) || (character == '\r') || (character == '\n')) {
      MOEA_Line_buffer[position++] = '\0';
      break; 
    } else {
      MOEA_Line_buffer[position++] = character;
    }
  }
  
  MOEA_Line_position = 0;
  
  if (position == 1) {
    return MOEA_EOF;
  } else {
    return MOEA_SUCCESS;
  }
}

MOEA_Status MOEA_Read_token(char** token) {
  if (MOEA_Line_buffer == NULL) {
    return MOEA_Error(MOEA_PARSE_NO_SOLUTION);
  }

  /* find start of next token (skipping any leading whitespace) */
  MOEA_Line_position += strspn(MOEA_Line_buffer+MOEA_Line_position,
      MOEA_WHITESPACE);
  
  /* if this is the end of the line, signal an error */
  if (MOEA_Line_buffer[MOEA_Line_position] == '\0') {
    return MOEA_Error(MOEA_PARSE_EOL);
  }
  
  /* find end of token */
  size_t end = strcspn(MOEA_Line_buffer+MOEA_Line_position, MOEA_WHITESPACE);
  
  /* create token */
  MOEA_Line_buffer[MOEA_Line_position+end] = '\0';
  *token = MOEA_Line_buffer+MOEA_Line_position;
  MOEA_Line_position += end + 1;
  
  return MOEA_SUCCESS;
}

MOEA_Status MOEA_Read_binary(const int size, int* values) {
  int i = 0;
  char* token = NULL;
  
  MOEA_Status status = MOEA_Read_token(&token);
  
  if (status != MOEA_SUCCESS) {
    return MOEA_Error(status);
  }
  
  while (1) {
    if ((i < size) && (token[i] != '\0')) {
      if (token[i] == '0') {
        values[i] = 0;
      } else if (token[i] == '1') {
        values[i] = 1;
      } else {
        return MOEA_Error(MOEA_PARSE_BINARY_ERROR);
      }
    } else if ((i != size) || (token[i] != '\0')) {
      return MOEA_Error(MOEA_PARSE_BINARY_ERROR);
    } else {
      break;
    }
    
    i++;
  }
  
  return MOEA_SUCCESS;
}

MOEA_Status MOEA_Read_permutation(const int size, int* values) {
  int i;
  char* token = NULL;
  char* endptr = NULL;
  
  MOEA_Status status = MOEA_Read_token(&token);
  
  if (status != MOEA_SUCCESS) {
    return MOEA_Error(status);
  }
  
  values[0] = strtol(token, &endptr, 10);

  for (i=1; i<size; i++) {
    if ((*endptr != ',') || (*(endptr+1) == '\0')) {
      return MOEA_Error(MOEA_PARSE_PERMUTATION_ERROR);
    }
    
    token = endptr+1;
    values[i] = strtol(token, &endptr, 10);
  }
  
  if (*endptr != '\0') {
    return MOEA_Error(MOEA_PARSE_PERMUTATION_ERROR);
  }
  
  return MOEA_SUCCESS;
}

MOEA_Status MOEA_Read_double(double* value) {
  char* token = NULL;
  char* endptr = NULL;
  
  MOEA_Status status = MOEA_Read_token(&token);
  
  if (status != MOEA_SUCCESS) {
    return MOEA_Error(status);
  }
  
  *value = strtod(token, &endptr);
  
  if (*endptr != '\0') {
    return MOEA_Error(MOEA_PARSE_DOUBLE_ERROR);
  }
  
  return MOEA_SUCCESS;
}

MOEA_Status MOEA_Read_doubles(const int size, double* values) {
  int i;

  for (i=0; i<size; i++) {
    MOEA_Status status = MOEA_Read_double(&values[i]);
    
    if (status != MOEA_SUCCESS) {
      return MOEA_Error(status);
    }
  }
  
  return MOEA_SUCCESS;
}

MOEA_Status MOEA_Write(const double* objectives, const double* constraints) {
  int i;
  
  /* validate inputs before writing results */
  if (((objectives == NULL) && (MOEA_Number_objectives > 0)) ||
      ((constraints == NULL) && (MOEA_Number_constraints > 0))) {
    return MOEA_Error(MOEA_NULL_POINTER_ERROR);   
  }
  
  /* write objectives to output */
  for (i=0; i<MOEA_Number_objectives; i++) {
    if (i > 0) {
      printf(" ");
    }
    
    printf("%.17g", objectives[i]);
  }
  
  /* write constraints to output */
  for (i=0; i<MOEA_Number_constraints; i++) {
    if ((MOEA_Number_objectives > 0) || (i > 0)) {
      printf(" ");
    }
  
    printf("%.17g", constraints[i]);
  }
  
  /* end line and flush to push data out immediately */
  printf("\n");
  fflush(stdout);
  
  return MOEA_SUCCESS;
}

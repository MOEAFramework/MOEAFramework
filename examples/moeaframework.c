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

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <math.h>
#include <errno.h>
#include "moeaframework.h"

#ifdef MOEA_SOCKETS
#  include <unistd.h>
#  include <sys/types.h>
#  include <sys/socket.h>
#  include <netdb.h>
#endif

#define MOEA_WHITESPACE " \t"
#define MOEA_INITIAL_BUFFER_SIZE 1024
#define MOEA_DEFAULT_PORT "16801"

FILE* MOEA_Stream_input = NULL;
FILE* MOEA_Stream_output = NULL;
FILE* MOEA_Stream_error = NULL;
int MOEA_Number_objectives;
int MOEA_Number_constraints;

char* MOEA_Line_buffer = NULL;
size_t MOEA_Line_position = 0;
size_t MOEA_Line_limit = 0;

void MOEA_Error_callback_default(const MOEA_Status status) {
  MOEA_Debug("%s\n", MOEA_Status_message(status));
  MOEA_Terminate();
  exit(EXIT_FAILURE);
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
  case MOEA_SOCKET_ERROR:
    return "Unable to establish socket connection";
  case MOEA_IO_ERROR:
    return "Unable to read/write from stream";
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
  MOEA_Stream_input = stdin;
  MOEA_Stream_output = stdout;
  MOEA_Stream_error = stderr;
  MOEA_Number_objectives = objectives;
  MOEA_Number_constraints = constraints;
  
  return MOEA_SUCCESS;
}

#ifdef MOEA_SOCKETS
MOEA_Status MOEA_Init_socket(const int objectives, const int constraints,
    const char* service) {
  int gai_errno;
  int listenfd;
  int readfd;
  int writefd;
  int yes = 1;
  struct addrinfo hints;
  struct addrinfo *servinfo = NULL;
  struct addrinfo *sp = NULL;
  struct sockaddr_storage their_addr;
  socklen_t addr_size = sizeof(their_addr);

  MOEA_Init(objectives, constraints);

  memset(&hints, 0, sizeof(hints));
  hints.ai_family = AF_UNSPEC;
  hints.ai_socktype = SOCK_STREAM;
  hints.ai_flags = AI_PASSIVE;

  if (service == NULL) {
    service = MOEA_DEFAULT_PORT;
  }

  if ((gai_errno = getaddrinfo(NULL, service, &hints, &servinfo)) != 0) {
    MOEA_Debug("getaddrinfo: %s\n", gai_strerror(gai_errno));
    return MOEA_Error(MOEA_SOCKET_ERROR);
  }

  for (sp = servinfo; sp != NULL; sp = sp->ai_next) {
    if ((listenfd = socket(servinfo->ai_family, servinfo->ai_socktype, servinfo->ai_protocol)) == -1) {
      MOEA_Debug("socket: %s\n", strerror(errno));
      continue;
    }
  
    /* enable socket reuse to avoid socket already in use errors */
    if (setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1) {
      MOEA_Debug("setsockopt: %s\n", strerror(errno));
    } 

    if (bind(listenfd, servinfo->ai_addr, servinfo->ai_addrlen) == -1) {
      MOEA_Debug("bind: %s\n", strerror(errno));
      close(listenfd);
      continue;
    }
    
    break;
  }
  
  freeaddrinfo(servinfo);
  
  if (sp == NULL) {
    return MOEA_Error(MOEA_SOCKET_ERROR);
  }
  
  if (listen(listenfd, 1) == -1) {
    MOEA_Debug("listen: %s\n", strerror(errno));
    close(listenfd);
    return MOEA_Error(MOEA_SOCKET_ERROR);
  }
  
  if ((readfd = accept(listenfd, (struct sockaddr*)&their_addr, &addr_size)) == -1) {
    MOEA_Debug("accept: %s\n", strerror(errno));
    close(listenfd);
    return MOEA_Error(MOEA_SOCKET_ERROR);
  }
  
  close(listenfd);

  if ((writefd = dup(readfd)) == -1) {
    MOEA_Debug("dup: %s\n", strerror(errno));
    close(readfd);
    return MOEA_Error(MOEA_SOCKET_ERROR);
  }

  if ((MOEA_Stream_input = fdopen(readfd, "r")) == NULL) {
    MOEA_Debug("fdopen: %s\n", strerror(errno));
    close(readfd);
    close(writefd);
    return MOEA_Error(MOEA_SOCKET_ERROR);
  }

  if ((MOEA_Stream_output = fdopen(writefd, "w")) == NULL) {
    MOEA_Debug("fdopen: %s\n", strerror(errno));
    fclose(MOEA_Stream_input);
    close(readfd);
    close(writefd);
    return MOEA_Error(MOEA_SOCKET_ERROR);
  }

  return MOEA_SUCCESS;
}
#endif

MOEA_Status MOEA_Debug(const char* format, ...) {
  va_list arguments;
  
  va_start(arguments, format);
  vfprintf(MOEA_Stream_error, format, arguments);
  fflush(MOEA_Stream_error);
  va_end(arguments);
  
  return MOEA_SUCCESS;
}

MOEA_Status MOEA_Next_solution() {
  size_t position = 0;
  int character;

  if (feof(MOEA_Stream_input)) {
    return MOEA_EOF;
  }
  
  while (!feof(MOEA_Stream_input)) {
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
    character = fgetc(MOEA_Stream_input);

    if ((character == EOF) || (character == '\r') || (character == '\n')) {
      if (ferror(MOEA_Stream_input)) {
        return MOEA_Error(MOEA_IO_ERROR);
      }

      MOEA_Line_buffer[position++] = '\0';

      /* handle the windows-style \r\n newline */
      if (character == '\r') {
        character = fgetc(MOEA_Stream_input);

        if ((character == EOF) && ferror(MOEA_Stream_input)) {
          return MOEA_Error(MOEA_IO_ERROR);
        } else if (character != '\n') {
          if (ungetc(character, MOEA_Stream_input) != character) {
            return MOEA_Error(MOEA_IO_ERROR);
          }
        }
      }

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
      if (fprintf(MOEA_Stream_output, " ") < 0) {
        return MOEA_Error(MOEA_IO_ERROR);
      }
    }
    
    if (fprintf(MOEA_Stream_output, "%.17g", objectives[i]) < 0) {
      return MOEA_Error(MOEA_IO_ERROR);
    }
  }
  
  /* write constraints to output */
  for (i=0; i<MOEA_Number_constraints; i++) {
    if ((MOEA_Number_objectives > 0) || (i > 0)) {
      if (fprintf(MOEA_Stream_output, " ") < 0) {
        return MOEA_Error(MOEA_IO_ERROR);
      }
    }
  
    if (fprintf(MOEA_Stream_output, "%.17g", constraints[i]) < 0) {
      return MOEA_Error(MOEA_IO_ERROR);
    }
  }
  
  /* end line and flush to push data out immediately */
  if (fprintf(MOEA_Stream_output, "\n") < 0) {
    return MOEA_Error(MOEA_IO_ERROR);
  }

  if (fflush(MOEA_Stream_output) == EOF) {
    return MOEA_Error(MOEA_IO_ERROR);
  }
  
  return MOEA_SUCCESS;
}

MOEA_Status MOEA_Terminate() {
  if (MOEA_Stream_input != stdin) {
    fclose(MOEA_Stream_input);
  }

  if (MOEA_Stream_output != stdout) {
    fclose(MOEA_Stream_output);
  }

  if (MOEA_Stream_error != stderr) {
    fclose(MOEA_Stream_error);
  }

  return MOEA_SUCCESS;
}


/* Copyright 2009-2024 David Hadka
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

#ifdef __WIN32__
#  include <winsock2.h>
#  include <ws2tcpip.h>
#  include <io.h>
#else
#  include <unistd.h>
#  include <sys/types.h>
#  include <sys/socket.h>
#  include <netdb.h>
#  define SOCKET int
#  define INVALID_SOCKET -1
#  define SOCKET_ERROR -1
#endif

#define MOEA_WHITESPACE " \t"
#define MOEA_INITIAL_BUFFER_SIZE 1024
#define MOEA_DEFAULT_PORT "16801"

FILE* MOEA_Stream_input = NULL;
FILE* MOEA_Stream_output = NULL;
FILE* MOEA_Stream_error = NULL;

SOCKET MOEA_Socket = INVALID_SOCKET;

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
  case MOEA_PARSE_INTEGER_ERROR:
    return "Unable to parse integer variable";
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
  case MOEA_FORMAT_ERROR:
    return "Unable to format value";
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

MOEA_Status MOEA_Init_socket(const int objectives, const int constraints, const char* service) {
  int gai_errno;
  SOCKET listen_sock;
  int yes = 1;
  struct addrinfo hints;
  struct addrinfo *servinfo = NULL;
  struct addrinfo *sp = NULL;
  struct sockaddr_storage their_addr;
  socklen_t addr_size = sizeof(their_addr);

  MOEA_Init(objectives, constraints);
  
#ifdef __WIN32__
  WORD versionWanted = MAKEWORD(1, 1);
  WSADATA wsaData;
  WSAStartup(versionWanted, &wsaData);
#endif

  memset(&hints, 0, sizeof(hints));
  hints.ai_family = AF_UNSPEC;
  hints.ai_socktype = SOCK_STREAM;
  hints.ai_flags = AI_PASSIVE;

  if (service == NULL) {
    service = MOEA_DEFAULT_PORT;
  }

  if ((gai_errno = getaddrinfo("127.0.0.1", service, &hints, &servinfo)) != 0) {
    MOEA_Debug("getaddrinfo: %s\n", gai_strerror(gai_errno));
    return MOEA_Error(MOEA_SOCKET_ERROR);
  }

  for (sp = servinfo; sp != NULL; sp = sp->ai_next) {
    if ((listen_sock = socket(servinfo->ai_family, servinfo->ai_socktype, servinfo->ai_protocol)) == INVALID_SOCKET) {
      MOEA_Debug("socket: %s\n", strerror(errno));
      continue;
    }
  
    /* enable socket reuse to avoid socket already in use errors */
    if (setsockopt(listen_sock, SOL_SOCKET, SO_REUSEADDR, (char*)&yes, sizeof(int)) == SOCKET_ERROR) {
      MOEA_Debug("setsockopt: %s\n", strerror(errno));
    }
    
    if (bind(listen_sock, servinfo->ai_addr, servinfo->ai_addrlen) == SOCKET_ERROR) {
      MOEA_Debug("bind: %s\n", strerror(errno));
      close(listen_sock);
      continue;
    }
    
    break;
  }
  
  freeaddrinfo(servinfo);
  
  if (sp == NULL) {
    return MOEA_Error(MOEA_SOCKET_ERROR);
  }
    
  if (listen(listen_sock, 1) == SOCKET_ERROR) {
    MOEA_Debug("listen: %s\n", strerror(errno));
    close(listen_sock);
    return MOEA_Error(MOEA_SOCKET_ERROR);
  }
    
  if ((MOEA_Socket = accept(listen_sock, (struct sockaddr*)&their_addr, &addr_size)) == INVALID_SOCKET) {
    MOEA_Debug("accept: %s\n", strerror(errno));
    close(listen_sock);
    return MOEA_Error(MOEA_SOCKET_ERROR);
  }
  
  close(listen_sock);

  return MOEA_SUCCESS;
}

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
  size_t recv_len = 0;
  int character;
  
  if (MOEA_Line_limit > 0) {
  	MOEA_Line_buffer[0] = '\0';
  }

  if (feof(MOEA_Stream_input)) {
    return MOEA_EOF;
  }
  
  while (!feof(MOEA_Stream_input)) {
    /* increase line buffer if needed */
    if ((MOEA_Line_limit == 0) || (position >= MOEA_Line_limit-1)) {
      MOEA_Line_limit += MOEA_INITIAL_BUFFER_SIZE;
      MOEA_Line_buffer = (char*)realloc(MOEA_Line_buffer, MOEA_Line_limit*sizeof(char));
    
      if (MOEA_Line_buffer == NULL) {
        return MOEA_Error(MOEA_MALLOC_ERROR);
      }
    }
  
    /* read the next chunk into the buffer */
    if (MOEA_Socket == INVALID_SOCKET) {
      if (fgets(&MOEA_Line_buffer[position], MOEA_Line_limit - position, MOEA_Stream_input) == NULL) {
        if (!feof(MOEA_Stream_input)) {
          MOEA_Debug("fgets: %s\n", strerror(errno));
          return MOEA_Error(MOEA_IO_ERROR);
        }
      }
    } else {
      recv_len = recv(MOEA_Socket, &MOEA_Line_buffer[position], MOEA_Line_limit - position, 0);

      if (recv_len < 0) {
        MOEA_Debug("recv: %s\n", strerror(errno));
        return MOEA_Error(MOEA_SOCKET_ERROR);
      }

      MOEA_Line_buffer[position + recv_len] = '\0';
    }
    
    /* compute the new length and check if a full line was read */
    position = strlen(MOEA_Line_buffer);
    
    if (position < MOEA_Line_limit || MOEA_Line_buffer[position-1] == '\n') {
    	break;	
    }
  }
  
  /* remove any newline characters */
  while (position > 0 && (MOEA_Line_buffer[position-1] == '\n' || MOEA_Line_buffer[position-1] == '\r')) {
    MOEA_Line_buffer[position-1] = '\0';
    position -= 1;
  }
  
  MOEA_Line_position = 0;
  
  if (position == 0) {
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
  MOEA_Line_position += strspn(MOEA_Line_buffer+MOEA_Line_position, MOEA_WHITESPACE);
  
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

MOEA_Status MOEA_Read_int(int* value) {
  char* token = NULL;
  char* endptr = NULL;
  
  MOEA_Status status = MOEA_Read_token(&token);
  
  if (status != MOEA_SUCCESS) {
    return MOEA_Error(status);
  }
  
  *value = strtol(token, &endptr, 10);
  
  if (*endptr != '\0') {
    return MOEA_Error(MOEA_PARSE_INTEGER_ERROR);
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
  size_t position = 0;
  size_t len = 0;
  
  /* validate inputs before writing results */
  if (((objectives == NULL) && (MOEA_Number_objectives > 0)) || ((constraints == NULL) && (MOEA_Number_constraints > 0))) {
    return MOEA_Error(MOEA_NULL_POINTER_ERROR);   
  }
  
  /* determine size of output */
  for (i=0; i<MOEA_Number_objectives; i++) {
    len += snprintf(NULL, 0, "%.17g", objectives[i]);
    len += 1;
  }

  for (i=0; i<MOEA_Number_constraints; i++) {
    len += snprintf(NULL, 0, "%.17g", constraints[i]);
    len += 1;
  }

  /* increase line buffer if needed */
  if ((MOEA_Line_limit == 0) || (len >= MOEA_Line_limit-1)) {
    MOEA_Line_limit += len + MOEA_INITIAL_BUFFER_SIZE + 1;
    MOEA_Line_buffer = (char*)realloc(MOEA_Line_buffer, MOEA_Line_limit*sizeof(char));
    
    if (MOEA_Line_buffer == NULL) {
      return MOEA_Error(MOEA_MALLOC_ERROR);
    }
  }

  /* write content to the buffer */
  for (i=0; i<MOEA_Number_objectives; i++) {
    if (i > 0) {
      if ((len = sprintf(&MOEA_Line_buffer[position], " ")) < 0) {
        return MOEA_Error(MOEA_FORMAT_ERROR);
      }

      position += len;
    }

    if ((len = sprintf(&MOEA_Line_buffer[position], "%.17g", objectives[i])) < 0) {
      return MOEA_Error(MOEA_FORMAT_ERROR);
    }

    position += len;
  }
  
  for (i=0; i<MOEA_Number_constraints; i++) {
    if ((MOEA_Number_objectives > 0) || (i > 0)) {
      if ((len = sprintf(&MOEA_Line_buffer[position], " ")) < 0) {
        return MOEA_Error(MOEA_FORMAT_ERROR);
      }

      position += len;
    }
  
    if ((len = sprintf(&MOEA_Line_buffer[position], "%.17g", constraints[i])) < 0) {
      return MOEA_Error(MOEA_FORMAT_ERROR);
    }

    position += len;
  }
  
  /* terminate line with newline */
  if ((len = sprintf(&MOEA_Line_buffer[position], "\n")) < 0) {
    return MOEA_Error(MOEA_FORMAT_ERROR);
  }

  position += len;

  /* send content */
  if (MOEA_Socket == INVALID_SOCKET) {
    if (fputs(MOEA_Line_buffer, MOEA_Stream_output) == EOF) {
      return MOEA_Error(MOEA_IO_ERROR);
    }

    if (fflush(MOEA_Stream_output) == EOF) {
      return MOEA_Error(MOEA_IO_ERROR);
    }
  } else {
    if (send(MOEA_Socket, MOEA_Line_buffer, position, 0) < 0) {
      MOEA_Debug("send: %s\n", strerror(errno));
      return MOEA_Error(MOEA_SOCKET_ERROR);
    }
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

  if (MOEA_Socket > 0) {
    close(MOEA_Socket);
  }
  
#ifdef __WIN32__
  WSACleanup();
#endif

  return MOEA_SUCCESS;
}


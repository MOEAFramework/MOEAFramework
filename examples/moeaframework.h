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

#ifndef MOEAFRAMEWORK_H
#define MOEAFRAMEWORK_H

#ifdef __cplusplus
extern "C" {
#endif

#ifdef _POSIX_SOURCE
#define MOEA_SOCKETS
#endif

/**
 * The status and error codes that are returned by functions provided by this
 * library.
 */
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
  MOEA_SOCKET_ERROR,
  MOEA_IO_ERROR
} MOEA_Status;

/**
 * The callback function that is invoked whenever an error occurs.  A default
 * callback function is provided that 1) reports the error message; and 2) 
 * immediately exits the program.  This default behavior means that none of the
 * functions below return if an error occurs.  It is therefore not necessary to
 * check the return value of any functions below.
 *
 * However, the callback function can be overridden to provide customized error
 * handling.  If the callback function does not exit the program, then checking
 * return values will be necessary.
 */
extern void (*MOEA_Error_callback)(const MOEA_Status);

/**
 * Returns a human-readable message detailing the specified status code.
 *
 * @param status the status code
 * @return a message detailing the specified status code
 */
const char* MOEA_Status_message(const MOEA_Status);

/**
 * Initializes the MOEA Framework to support a problem with the specified
 * number of objectives and constraints.  This function should be invoked prior
 * to all other functions provided by this library.
 *
 * @param objectives the number of objectives defined by this problem
 * @param constraints the number of constraints defined by this problem
 * @return MOEA_SUCCESS if this function call completed successfully; or the
 *         specific error code causing failure
 */
MOEA_Status MOEA_Init(const int, const int);

#ifdef MOEA_SOCKETS
/**
 * Initializes the MOEA Framework to support a problem with the specified
 * number of objectives and constraints.  This initializer establishes a
 * socket at the specified port to receive and send the data.  This function
 * should be invoked prior to all other functions provided by this library.
 *
 * @param objectives the number of objectives defined by this problem
 * @param constraints the number of constraints defined by this problem
 * @param service the port number or service name
 * @return MOEA_SUCCESS if this function call completed successfully; or the
 *         specific error code causing failure
 */
MOEA_Status MOEA_Init_socket(const int, const int, const char*);
#endif

/**
 * Begins reading the next solution from the MOEA Framework.
 *
 * @return MOEA_SUCCESS if there is a next solution to read; MOEA_EOF if there
 *         exists no more solutions; or the specific error code causing failure
 */
MOEA_Status MOEA_Next_solution();

/**
 * Reads the next real-valued decision variable from the current solution.
 *
 * @param value a reference to the double that is assigned the value of the
 *        decision variable
 * @return MOEA_SUCCESS if the decision variable was successfully read; or the
 *         specific error code causing failure
 */
MOEA_Status MOEA_Read_double(double*);

/**
 * Reads one or more real-valued decision variables from the current solution.
 * This method is equivalent to calling MOEA_Read_Double multiple times.
 *
 * @param values a reference to the double array that is filled with the values
 *        of the decision variables
 * @return MOEA_SUCCESS if the decision variables were successfully read; or the
 *         specific error code causing failure
 */
MOEA_Status MOEA_Read_doubles(const int, double*);

/**
 * Reads the next binary decision variable from the current solution.  Upon
 * the successful completion of this function call, the i-th index in the int
 * array will store the value of the i-th bit, either 0 or 1.
 *
 * @param size the number of bits comprising the binary decision variable
 * @param values a reference to the int array that is filled with the bit values
 *        from the binary decision variable
 * @return MOEA_SUCCESS if the decision variable was successfully read; or the
 *         specific error code causing failure
 */
MOEA_Status MOEA_Read_binary(const int, int*);

/**
 * Reads the next permutation decision variable from the current solution.  Upon
 * the successful completion of this function call, the i-th index in the int
 * array will store the value of the i-th element in the permutation, ranging
 * from 0 to size-1.
 *
 * @param size the number of elements in the permutation
 * @param values a reference to the int array that is filled with the elements
 *        in the permutation
 * @return MOEA_SUCCESS if the decision variable was successfully read; or the
 *         specific error code causing failure
 */
MOEA_Status MOEA_Read_permutation(const int, int*);

/**
 * Writes the objectives and constraints back to the MOEA Framework.
 *
 * @param objectives the objective values
 * @param constraints the constraint values
 * @return MOEA_SUCCESS if this function call completed successfully; or the
 *         specific error code causing failure
 */
MOEA_Status MOEA_Write(const double*, const double*);

/**
 * Writes a debug or other status message back to the MOEA Framework.  This
 * message will typically be displayed by the MOEA Framework, but the message
 * should not affect the execution of the program.
 *
 * @param format the message format, see fprintf for details
 * @param arguments the values to be inserted into the message in place of each
 *        %-tag in the format, see fprintf for details
 * @return MOEA_SUCCESS if this function call completed successfully; or the
 *         specific error code causing failure
 */
MOEA_Status MOEA_Debug(const char* format, ...);

/**
 * Closes the communication channel with the MOEA Framework.  No methods from
 * this library should be invoked after being terminated.
 *
 * @return MOEA_SUCCESS if this function call completed successfully; or the
 *         specific error code causing failure
 */
MOEA_Status MOEA_Terminate();

#ifdef __cplusplus
}
#endif

#endif

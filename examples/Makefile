# This make file compiles the C/C++ executables used by the external problem
# examples.
#
# This make file assumes it is being run on a Unix-like environment with GCC
# and Make installed.  Windows users can install MinGW/MSYS or Cygwin.
#
# Run 'make' to build all files

main: dtlz2_stdio.exe dtlz2_socket.exe

clean:
	rm -f dtlz2_stdio.exe dtlz2_socket.exe

dtlz2_stdio.exe:
	gcc -o dtlz2_stdio.exe dtlz2.c moeaframework.c -lm

dtlz2_socket.exe:
ifneq ($(OS),Windows_NT)
ifeq ($(shell uname -s),Linux)
	gcc -DUSE_SOCKET -o dtlz2_socket.exe dtlz2.c moeaframework.c -lm
endif
endif

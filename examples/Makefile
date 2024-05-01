# This make file compiles the C/C++ executables used by the external problem
# examples.
#
# This make file assumes it is being run on a Unix-like environment with GCC
# and Make installed.  Windows users can install MinGW/MSYS or Cygwin.
#
# Run 'make' to build all files

LIBS = -lm

ifeq ($(OS),Windows_NT)
	LIBS += -lwsock32 -lWs2_32
endif

main: dtlz2_stdio.exe dtlz2_socket.exe

clean:
	rm -f dtlz2_stdio.exe dtlz2_socket.exe

dtlz2_stdio.exe:
	gcc -o dtlz2_stdio.exe dtlz2.c moeaframework.c $(LIBS)

dtlz2_socket.exe:
	gcc -DUSE_SOCKET -o dtlz2_socket.exe dtlz2.c moeaframework.c $(LIBS)


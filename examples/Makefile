# This make file compiles the C/C++ executables used by examples/Example5.java
# and examples/Example6.java.
#
# This make file assumes it is being run on a Unix-like environment with GCC
# and Make installed.  Windows users can install MinGW/MSYS or Cygwin.
#
# Run 'make' to build all files

CC = gcc
FLAGS = -Wall -O3
UNAME := $(shell uname -s)

main: dtlz2_stdio.exe dtlz2_socket.exe

clean:
	rm -f moeaframework.o libmoea.a dtlz2_stdio.exe dtlz2_socket.exe

libmoea.a: moeaframework.c moeaframework.h
	$(CC) $(FLAGS) -o moeaframework.o -c moeaframework.c
	ar rcs libmoea.a moeaframework.o
	rm -f moeaframework.o

dtlz2_stdio.exe: libmoea.a dtlz2.c
	${CC} ${FLAGS} -o dtlz2_stdio.exe dtlz2.c -L. -lmoea -lm

dtlz2_socket.exe: libmoea.a dtlz2.c
ifeq ($(UNAME),Linux)
	${CC} ${FLAGS} -DUSE_SOCKET -o dtlz2_socket.exe dtlz2.c -L. -lmoea -lm
endif

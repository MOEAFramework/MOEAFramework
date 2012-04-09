# This make file compiles the Rosenbrock demo (see examples/Example4.java) and
# two test executables facilitating unit testing the C/C++ interface:
# test_stdio.exe and test_socket.exe.
#
# Run 'make' to build all files

CC = gcc
FLAGS = -Wall -O3
UNAME := $(shell uname -s)

main: test_stdio.exe test_socket.exe rosenbrock.exe

clean:
	rm -f moeaframework.o libmoea.a test_stdio.exe test_socket.exe rosenbrock.exe

libmoea.a: moeaframework.c moeaframework.h
	$(CC) $(FLAGS) -o moeaframework.o -c moeaframework.c
	ar rcs libmoea.a moeaframework.o

test_stdio.exe: libmoea.a test.c
	$(CC) $(FLAGS) -o test_stdio.exe test.c -L. -lmoea
	
test_socket.exe: libmoea.a test.c
ifeq ($(UNAME),Linux)
	${CC} ${FLAGS} -D_SOCKET -o test_socket.exe test.c -L. -lmoea
endif

rosenbrock.exe: libmoea.a rosenbrock.c
	$(CC) $(FLAGS) -o rosenbrock.exe rosenbrock.c -L. -lmoea
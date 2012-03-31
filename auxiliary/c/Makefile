# This make file compiles the Rosenbrock demo (see examples/Example4.java) and
# test.exe, an executable facilitating unit testing the C/C++ interface.
#
# Run 'make' to build all files

CC = gcc
FLAGS = -Wall -O3 -ansi

main: test.exe rosenbrock.exe

clean:
	rm moeaframework.o libmoea.a test.exe rosenbrock.exe

libmoea.a: moeaframework.c moeaframework.h
	$(CC) $(FLAGS) -o moeaframework.o -c moeaframework.c
	ar rcs libmoea.a moeaframework.o

test.exe: libmoea.a test.c
	$(CC) $(FLAGS) -o test.exe test.c -L. -lmoea

rosenbrock.exe: libmoea.a rosenbrock.c
	$(CC) $(FLAGS) -o rosenbrock.exe rosenbrock.c -L. -lmoea
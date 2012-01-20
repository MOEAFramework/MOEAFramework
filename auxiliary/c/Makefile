main:
	gcc -Wall -o moeaframework.o -c moeaframework.c
	ar rcs libmoea.a moeaframework.o
	gcc -Wall -o test.exe test.c -L. -lmoea
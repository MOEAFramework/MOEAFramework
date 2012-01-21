main:
	gcc -Wall -O3 -o moeaframework.o -c moeaframework.c
	ar rcs libmoea.a moeaframework.o
	gcc -Wall -O3 -o test.exe test.c -L. -lmoea
# This make file compiles the C/C++ executables used for unit testing.
#
# This make file assumes it is being run on a Unix-like environment.  Windows
# users can install MinGW/MSYS or Cygwin.
#
# Run 'make' to build all files

EXAMPLES := $(shell dir=$$(pwd); \
	while [ "$$dir" != "/" ] && [ ! -d "$$dir/examples" ]; \
    	do dir=$$(dirname "$$dir"); \
	done; \
	echo "$$dir/examples")

main: test_stdio.exe test_socket.exe

clean:
	rm -f test_stdio.exe test_socket.exe

test_stdio.exe:
	gcc -o test_stdio.exe -I $(EXAMPLES) test.c $(EXAMPLES)/moeaframework.c
	
test_socket.exe:
ifneq ($(OS),Windows_NT)
ifeq ($(shell uname -s),Linux)
	gcc -DUSE_SOCKET -o test_socket.exe -I $(EXAMPLES) test.c $(EXAMPLES)/moeaframework.c
endif
endif

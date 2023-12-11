# This make file compiles the C/C++ executables used for unit testing.
#
# This make file assumes it is being run on a Unix-like environment.  Windows
# users can install MinGW/MSYS or Cygwin.
#
# Run 'make' to build all files

CC = gcc
FLAGS = -Wall -O3
UNAME := $(shell uname -s)

# Search file tree for examples/ folder - the relative path can change based on the
# execution environment (Ant vs Maven)
EXAMPLES := $(shell dir=$$(pwd); \
	while [ "$$dir" != "/" ] && [ ! -d "$$dir/examples" ]; \
    	do dir=$$(dirname "$$dir"); \
	done; \
	echo "$$dir/examples")

main: test_stdio.exe test_socket.exe

clean:
	rm -f test_stdio.exe test_socket.exe

test_stdio.exe: test.c $(EXAMPLES)/moeaframework.c
	$(CC) $(FLAGS) -o test_stdio.exe -I $(EXAMPLES) -lm test.c $(EXAMPLES)/moeaframework.c
	
test_socket.exe: test.c $(EXAMPLES)/moeaframework.c
ifeq ($(UNAME),Linux)
	${CC} ${FLAGS} -DUSE_SOCKET -o test_socket.exe -I $(EXAMPLES) -lm test.c $(EXAMPLES)/moeaframework.c
endif

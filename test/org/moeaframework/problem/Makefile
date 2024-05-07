# This make file compiles the C/C++ executables used for unit testing.
#
# This make file assumes it is being run on a Unix-like environment.  Windows
# users can install MinGW/MSYS or Cygwin.
#
# Run 'make' to build all files

# Find the examples/ folder, which changes if using Ant or Maven
ROOT := ../../../..
CC := gcc
CPP := g++
CFLAGS := -O3 -Wall -Werror
LIBS = 

ifeq ("$(wildcard $(ROOT)/examples)","")
    override ROOT := $(ROOT)/../..
endif

ifeq ("$(wildcard $(ROOT)/examples)","")
    $(error Unable to locate the MOEA Framework root directory.  Specify the path by running 'make ROOT=<path>')
else
    $(info Found the MOEA Framework root directory at "$(ROOT)")
endif

ifeq ($(OS),Windows_NT)
    LIBS += -lwsock32 -lWs2_32
endif

main: test_stdio.exe test_socket.exe test_cpp.exe

clean:
	rm -f test_stdio.exe test_socket.exe test_cpp.exe

test_stdio.exe: test.c $(ROOT)/examples/moeaframework.c
	$(CC) $(CFLAGS) -o test_stdio.exe -I $(ROOT)/examples test.c $(ROOT)/examples/moeaframework.c $(LIBS)
	
test_socket.exe: test.c $(ROOT)/examples/moeaframework.c
	$(CC) $(CFLAGS) -DUSE_SOCKET -o test_socket.exe -I $(ROOT)/examples test.c $(ROOT)/examples/moeaframework.c $(LIBS)

test_cpp.exe: test.c $(ROOT)/examples/moeaframework.c
	$(CPP) $(CFLAGS) -o test_cpp.exe -I $(ROOT)/examples test.c $(ROOT)/examples/moeaframework.c $(LIBS)

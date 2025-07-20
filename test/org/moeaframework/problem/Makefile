# This make file compiles the C/C++ executables used for unit testing.  Use MinGW/MSYS or Cygwin to build on Windows.
# The relative path changes when built using Ant vs Maven, hence the need to locate the root directory.
#
# Run 'make' to build all files
ROOT := ../../../..
CC := gcc
CPP := g++
CFLAGS := -O3 -Wall -Werror -DMOEA_BUFFER_SIZE=16
LIBS = 
EXTERNAL = src/org/moeaframework/builder/external

ifeq ("$(wildcard $(ROOT)/src)","")
    override ROOT := $(ROOT)/../..
endif

ifeq ("$(wildcard $(ROOT)/src)","")
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

test_stdio.exe: test.c $(ROOT)/$(EXTERNAL)/moeaframework.c
	$(CC) $(CFLAGS) -o test_stdio.exe -I $(ROOT)/$(EXTERNAL) test.c $(ROOT)/$(EXTERNAL)/moeaframework.c $(LIBS)
	
test_socket.exe: test.c $(ROOT)/$(EXTERNAL)/moeaframework.c
	$(CC) $(CFLAGS) -DUSE_SOCKET -o test_socket.exe -I $(ROOT)/$(EXTERNAL) test.c $(ROOT)/$(EXTERNAL)/moeaframework.c $(LIBS)

test_cpp.exe: test.c $(ROOT)/$(EXTERNAL)/moeaframework.c
	$(CPP) $(CFLAGS) -o test_cpp.exe -I $(ROOT)/$(EXTERNAL) test.c $(ROOT)/$(EXTERNAL)/moeaframework.c $(LIBS)

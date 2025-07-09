#!/usr/bin/env bash

set -e

COCO_VERSION=${1:-v2.6.3}
JDK_INCLUDE=${JAVA_HOME}/include

if [ "$(uname -s)" == "Darwin" ]; then
	JDK_OS_INCLUDE=${JDK_INCLUDE}/darwin
	LIBNAME=libCocoJNI.dylib
	CFLAGS=( -fPIC )
elif [ "$(uname -s)" == "Linux" ]; then
	JDK_OS_INCLUDE=${JDK_INCLUDE}/linux
	LIBNAME=libCocoJNI.so
	CFLAGS=( -fPIC )
else
	JDK_OS_INCLUDE=${JDK_INCLUDE}/win32
	LIBNAME=CocoJNI.dll
	CFLAGS=( -fPIC -Wl,--kill-at )
fi

if [ ! -d "coco" ]; then
	git clone --branch ${COCO_VERSION} --single-branch https://github.com/numbbo/coco
fi

cd coco

# Fix JDK discovery on MacOS, as the original does not support installations using SDKMAN!
cat <<- EOF | patch --forward --reject-file=- || true
	diff --git a/do.py b/do.py
	index 46286641..6847655d 100755
	--- a/do.py
	+++ b/do.py
	@@ -700,9 +700,8 @@ def build_java():
	                                   env=os.environ, universal_newlines=True)
	         jdkversion = jdkversion.split()[1]
	         jdkpath = '/System/Library/Frameworks/JavaVM.framework/Headers'
	-        jdkpath1 = ('/Library/Java/JavaVirtualMachines/jdk' +
	-                    jdkversion + '.jdk/Contents/Home/include')
	-        jdkpath2 = jdkpath1 + '/darwin'
	+        jdkpath1 = abspath(join(executable_path('javac'), os.pardir, os.pardir, 'include'))
	+        jdkpath2 = join(jdkpath1, 'darwin')
	         run('code-experiments/build/java',
	             ['gcc', '-I', jdkpath, '-I', jdkpath1, '-I', jdkpath2, '-c', 'CocoJNI.c'],
	             verbose=_verbosity)
EOF

python3 do.py build-java
cd code-experiments/build/java
sed 's/Java_CocoJNI_/Java_org_moeaframework_problem_BBOB2016_CocoJNI_/g' CocoJNI.c > org_moeaframework_problem_BBOB2016_CocoJNI.c
sed 's/Java_CocoJNI_/Java_org_moeaframework_problem_BBOB2016_CocoJNI_/g' CocoJNI.h > org_moeaframework_problem_BBOB2016_CocoJNI.h
gcc "${CFLAGS[@]}" -I "${JDK_INCLUDE}" -I "${JDK_OS_INCLUDE}" -o "../../../../${LIBNAME}" -shared org_moeaframework_problem_BBOB2016_CocoJNI.c

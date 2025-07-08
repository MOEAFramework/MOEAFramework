#!/usr/bin/env bash

set -e

COCO_VERSION=${1:-v2.6.3}

if [ ! -d "coco" ]; then
	git clone --branch ${COCO_VERSION} --single-branch https://github.com/numbbo/coco
fi

cd coco

cat <<- EOF | git apply
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

if [ "$(uname -s)" == "Darwin" ]; then
	gcc -I ${JAVA_HOME}/include -I ${JAVA_HOME}/include/darwin -o ../../../../libCocoJNI.dylib -fPIC -shared org_moeaframework_problem_BBOB2016_CocoJNI.c
elif [ "$(uname -s)" == "Linux" ]; then
	gcc -I ${JAVA_HOME}/include -I ${JAVA_HOME}/include/linux -o ../../../../libCocoJNI.so -fPIC -shared org_moeaframework_problem_BBOB2016_CocoJNI.c

	#sudo mkdir -p /usr/java/packages/lib/
	#sudo mv libCocoJNI.so /usr/java/packages/lib/
else
	gcc "-Wl,--kill-at" -I $env:JAVA_HOME/include -I $env:JAVA_HOME/include/win32 -shared -o ../../../../CocoJNI.dll org_moeaframework_problem_BBOB2016_CocoJNI.c
fi

#!/bin/bash

COCO_VERSION=${1:-v2.6.3}

git clone --branch ${COCO_VERSION} --single-branch https://github.com/numbbo/coco
cd coco
python do.py run-java
          
cd code-experiments/build/java
sed 's/Java_CocoJNI_/Java_org_moeaframework_problem_BBOB2016_CocoJNI_/g' CocoJNI.c > org_moeaframework_problem_BBOB2016_CocoJNI.c
sed 's/Java_CocoJNI_/Java_org_moeaframework_problem_BBOB2016_CocoJNI_/g' CocoJNI.h > org_moeaframework_problem_BBOB2016_CocoJNI.h

# Windows
#gcc "-Wl,--kill-at" -I $env:JAVA_HOME/include -I $env:JAVA_HOME/include/win32 -shared -o CocoJNI.dll org_moeaframework_problem_BBOB2016_CocoJNI.c

# Linux / Mac
gcc -I $JAVA_HOME/include -I $JAVA_HOME/include/linux -o libCocoJNI.so -fPIC -shared org_moeaframework_problem_BBOB2016_CocoJNI.c

sudo mkdir -p /usr/java/packages/lib/
sudo mv libCocoJNI.so /usr/java/packages/lib/
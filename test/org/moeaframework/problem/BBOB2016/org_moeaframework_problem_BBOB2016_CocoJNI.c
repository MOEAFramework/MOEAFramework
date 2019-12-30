/**
 * This file contains all necessary interfaces to the COCO code in C. The structures coco_problem,
 * coco_suite and coco_observer are accessed by means of "pointers" of type long.
 *
 * TODO: Check if the casts from pointer to C structure actually work (how can this be done?)
 */
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <stdlib.h>

#include <jni.h>

#include "coco.h"
#include "coco.c"
#include "CocoJNI.h"

/*
 * Class:     CocoJNI
 * Method:    cocoSetLogLevel
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoSetLogLevel
(JNIEnv *jenv, jclass interface_cls, jstring jlog_level) {

  const char *log_level;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoGetObserver\n");
  }

  log_level = (*jenv)->GetStringUTFChars(jenv, jlog_level, NULL);

  coco_set_log_level(log_level);

  return;
}

/*
 * Class:     CocoJNI
 * Method:    cocoGetObserver
 * Signature: (Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoGetObserver
(JNIEnv *jenv, jclass interface_cls, jstring jobserver_name, jstring jobserver_options) {

  coco_observer_t *observer = NULL;
  const char *observer_name;
  const char *observer_options;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoGetObserver\n");
  }

  observer_name = (*jenv)->GetStringUTFChars(jenv, jobserver_name, NULL);
  observer_options = (*jenv)->GetStringUTFChars(jenv, jobserver_options, NULL);

  observer = coco_observer(observer_name, observer_options);

  return (jlong) observer;
}

/*
 * Class:     CocoJNI
 * Method:    cocoFinalizeObserver
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoFinalizeObserver
(JNIEnv *jenv, jclass interface_cls, jlong jobserver_pointer) {

  coco_observer_t *observer = NULL;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoFinalizeObserver\n");
  }

  observer = (coco_observer_t *) jobserver_pointer;
  coco_observer_free(observer);
  return;
}

/*
 * Class:     CocoJNI
 * Method:    cocoGetSuite
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoGetSuite
(JNIEnv *jenv, jclass interface_cls, jstring jsuite_name, jstring jsuite_instance, jstring jsuite_options) {

  coco_suite_t *suite = NULL;
  const char *suite_name;
  const char *suite_instance;
  const char *suite_options;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoGetSuite\n");
  }

  suite_name = (*jenv)->GetStringUTFChars(jenv, jsuite_name, NULL);
  suite_instance = (*jenv)->GetStringUTFChars(jenv, jsuite_instance, NULL);
  suite_options = (*jenv)->GetStringUTFChars(jenv, jsuite_options, NULL);

  suite = coco_suite(suite_name, suite_instance, suite_options);

  return (jlong) suite;
}

/*
 * Class:     CocoJNI
 * Method:    cocoFinalizeSuite
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoFinalizeSuite
(JNIEnv *jenv, jclass interface_cls, jlong jsuite_pointer) {

  coco_suite_t *suite = NULL;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoFinalizeSuite\n");
  }

  suite = (coco_suite_t *) jsuite_pointer;
  coco_suite_free(suite);
  return;
}

/*
 * Class:     CocoJNI
 * Method:    cocoGetNextProblem
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoGetNextProblem
(JNIEnv *jenv, jclass interface_cls, jlong jsuite_pointer, jlong jobserver_pointer) {

  coco_problem_t *problem = NULL;
  coco_suite_t *suite = NULL;
  coco_observer_t *observer = NULL;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoGetNextProblem\n");
  }

  suite = (coco_suite_t *) jsuite_pointer;
  observer = (coco_observer_t *) jobserver_pointer;
  problem = coco_suite_get_next_problem(suite, observer);

  if (problem == NULL)
    return 0;

  return (jlong) problem;
}

/*
 * Class:     CocoJNI
 * Method:    cocoEvaluateFunction
 * Signature: (J[D)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoEvaluateFunction
(JNIEnv *jenv, jclass interface_cls, jlong jproblem_pointer, jdoubleArray jx) {

  coco_problem_t *problem = NULL;
  double *y = NULL;
  double *x = NULL;
  int number_of_objectives;
  jdoubleArray jy;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoEvaluateFunction\n");
  }

  problem = (coco_problem_t *) jproblem_pointer;
  number_of_objectives = (int) coco_problem_get_number_of_objectives(problem);

  /* Call coco_evaluate_function */
  x = (*jenv)->GetDoubleArrayElements(jenv, jx, NULL);
  y = coco_allocate_vector(number_of_objectives);
  coco_evaluate_function(problem, x, y);

  /* Prepare the return value */
  jy = (*jenv)->NewDoubleArray(jenv, number_of_objectives);
  (*jenv)->SetDoubleArrayRegion(jenv, jy, 0, number_of_objectives, y);

  /* Free resources */
  coco_free_memory(y);
  (*jenv)->ReleaseDoubleArrayElements(jenv, jx, x, JNI_ABORT);
  return jy;
}
/*
 * Class:     CocoJNI
 * Method:    cocoEvaluateConstraint
 * Signature: (J[D)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoEvaluateConstraint
(JNIEnv *jenv, jclass interface_cls, jlong jproblem_pointer, jdoubleArray jx) {

  coco_problem_t *problem = NULL;
  double *y = NULL;
  double *x = NULL;
  int number_of_objectives;
  jdoubleArray jy;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoEvaluateConstraint\n");
  }

  problem = (coco_problem_t *) jproblem_pointer;
  number_of_objectives = (int) coco_problem_get_number_of_objectives(problem);

  /* Call coco_evaluate_constraint */
  x = (*jenv)->GetDoubleArrayElements(jenv, jx, NULL);
  y = coco_allocate_vector(number_of_objectives);
  coco_evaluate_constraint(problem, x, y);

  /* Prepare the return value */
  jy = (*jenv)->NewDoubleArray(jenv, number_of_objectives);
  (*jenv)->SetDoubleArrayRegion(jenv, jy, 0, number_of_objectives, y);

  /* Free resources */
  coco_free_memory(y);
  (*jenv)->ReleaseDoubleArrayElements(jenv, jx, x, JNI_ABORT);
  return jy;
}

/*
 * Class:     CocoJNI
 * Method:    cocoProblemGetDimension
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoProblemGetDimension
(JNIEnv *jenv, jclass interface_cls, jlong jproblem_pointer) {

  coco_problem_t *problem = NULL;
  jint jresult;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoProblemGetDimension\n");
  }

  problem = (coco_problem_t *) jproblem_pointer;
  jresult = (jint) coco_problem_get_dimension(problem);
  return jresult;
}

/*
 * Class:     CocoJNI
 * Method:    cocoProblemGetNumberOfObjectives
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoProblemGetNumberOfObjectives
(JNIEnv *jenv, jclass interface_cls, jlong jproblem_pointer) {

  coco_problem_t *problem = NULL;
  jint jresult;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoProblemGetNumberOfObjectives\n");
  }

  problem = (coco_problem_t *) jproblem_pointer;
  jresult = (jint) coco_problem_get_number_of_objectives(problem);
  return jresult;
}

/*
 * Class:     CocoJNI
 * Method:    cocoProblemGetNumberOfConstraints
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoProblemGetNumberOfConstraints
(JNIEnv *jenv, jclass interface_cls, jlong jproblem_pointer) {

  coco_problem_t *problem = NULL;
  jint jresult;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoProblemGetNumberOfConstraints\n");
  }

  problem = (coco_problem_t *) jproblem_pointer;
  jresult = (jint) coco_problem_get_number_of_constraints(problem);
  return jresult;
}

/*
 * Class:     CocoJNI
 * Method:    cocoProblemGetSmallestValuesOfInterest
 * Signature: (J)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoProblemGetSmallestValuesOfInterest
(JNIEnv *jenv, jclass interface_cls, jlong jproblem_pointer) {

  coco_problem_t *problem = NULL;
  const double *result;
  jdoubleArray jresult;
  jint dimension;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoProblemGetSmallestValuesOfInterest\n");
  }

  problem = (coco_problem_t *) jproblem_pointer;
  dimension = (int) coco_problem_get_dimension(problem);
  result = coco_problem_get_smallest_values_of_interest(problem);

  /* Prepare the return value */
  jresult = (*jenv)->NewDoubleArray(jenv, dimension);
  (*jenv)->SetDoubleArrayRegion(jenv, jresult, 0, dimension, result);
  return jresult;
}

/*
 * Class:     CocoJNI
 * Method:    cocoProblemGetLargestValuesOfInterest
 * Signature: (J)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoProblemGetLargestValuesOfInterest
(JNIEnv *jenv, jclass interface_cls, jlong jproblem_pointer) {

  coco_problem_t *problem = NULL;
  const double *result;
  jdoubleArray jresult;
  jint dimension;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoProblemGetLargestValuesOfInterest\n");
  }

  problem = (coco_problem_t *) jproblem_pointer;
  dimension = (int) coco_problem_get_dimension(problem);
  result = coco_problem_get_largest_values_of_interest(problem);

  /* Prepare the return value */
  jresult = (*jenv)->NewDoubleArray(jenv, dimension);
  (*jenv)->SetDoubleArrayRegion(jenv, jresult, 0, dimension, result);
  return jresult;
}

/*
 * Class:     CocoJNI
 * Method:    cocoProblemGetId
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoProblemGetId
(JNIEnv *jenv, jclass interface_cls, jlong jproblem_pointer) {

  coco_problem_t *problem = NULL;
  const char *result;
  jstring jresult;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoProblemGetId\n");
  }

  problem = (coco_problem_t *) jproblem_pointer;
  result = coco_problem_get_id(problem);
  jresult = (*jenv)->NewStringUTF(jenv, result);
  return jresult;
}

/*
 * Class:     CocoJNI
 * Method:    cocoProblemGetName
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoProblemGetName
(JNIEnv *jenv, jclass interface_cls, jlong jproblem_pointer) {

  coco_problem_t *problem = NULL;
  const char *result;
  jstring jresult;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoProblemGetName\n");
  }

  problem = (coco_problem_t *) jproblem_pointer;
  result = coco_problem_get_name(problem);
  jresult = (*jenv)->NewStringUTF(jenv, result);
  return jresult;
}

/*
 * Class:     CocoJNI
 * Method:    cocoProblemGetIndex
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_moeaframework_problem_BBOB2016_CocoJNI_cocoProblemGetIndex
(JNIEnv *jenv, jclass interface_cls, jlong jproblem_pointer) {

  coco_problem_t *problem = NULL;
  jlong jresult;

  /* This test is both to prevent warning because interface_cls was not used and to check for exceptions */
  if (interface_cls == NULL) {
    jclass Exception = (*jenv)->FindClass(jenv, "java/lang/Exception");
    (*jenv)->ThrowNew(jenv, Exception, "Exception in cocoProblemGetIndex\n");
  }

  problem = (coco_problem_t *) jproblem_pointer;
  jresult = (jlong) coco_problem_get_suite_dep_index(problem);
  return jresult;
}

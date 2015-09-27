package org.moeaframework;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Annotation for ignoring unit tests on Travis CI.  This can be used to ignore
 * tests that frequently fail due to their stochastic nature, are not configured
 * to run correctly on Travis' build environment, or exceed the 10 minute
 * timeout.  Test classes using this annotation must also specify
 * \code{@RunWith(TravisRunner.clsas)}.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreOnTravis {
	
	String value() default "No comment";

}

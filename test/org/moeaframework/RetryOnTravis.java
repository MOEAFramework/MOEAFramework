package org.moeaframework;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Annotation for retrying tests.  Test classes using this annotation must also
 * specify \code{@RunWith(TravisRunner.clsas)}.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryOnTravis {

	int value() default 5;

}

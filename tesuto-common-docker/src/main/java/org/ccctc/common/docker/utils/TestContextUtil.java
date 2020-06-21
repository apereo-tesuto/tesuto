package org.ccctc.common.docker.utils;

import java.lang.annotation.Annotation;

import org.springframework.test.context.TestContext;

public class TestContextUtil {

	private TestContextUtil() {
	}

	public static Object getAnnotationConfiguration(TestContext context, Class<? extends Annotation> annotation) {
		Object result = null;
		// METHOD level has priority over CLASS level
		result = context.getTestMethod().getAnnotation(annotation);
		if (result == null) {
			// METHOD level is null, using CLASS level now
			result = context.getTestClass().getAnnotation(annotation);
		}
		return result;
	}

	public static Object getClassAnnotationConfiguration(TestContext context, Class<? extends Annotation> annotation) {
		return context.getTestClass().getAnnotation(annotation);
	}
}
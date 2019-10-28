package org.ccctc.common.docker.utils;

import org.apache.commons.lang.StringUtils;

public class ObjectUtil {

	private ObjectUtil() {
	}

	public static boolean anyNullOrEmpty(String... args) {
		for (String arg : args) {
			if (StringUtils.isEmpty(arg)) {
				return true;
			}
		}
		return false;
	}

	public static boolean allNullOrEmpty(String... args) {
		return !anyNullOrEmpty(args);
	}
}
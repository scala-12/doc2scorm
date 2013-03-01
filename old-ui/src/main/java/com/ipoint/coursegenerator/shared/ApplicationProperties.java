package com.ipoint.coursegenerator.shared;

public class ApplicationProperties {
	public static boolean debugEnabled() {
		return "true".equals(System.getProperty("debugging"));
	}
}

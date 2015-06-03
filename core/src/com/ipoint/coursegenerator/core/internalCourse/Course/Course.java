package com.ipoint.coursegenerator.core.internalCourse.Course;

public class Course extends CourseTree {

	private String courseName;
	private String manifest;

	public String getCaption() {
		return this.courseName;
	}

	public String getManifest() {
		return manifest;
	}

	public void setCaption(String courseName) {
		this.courseName = courseName;
	}

	public void setManifest(String manifest) {
		this.manifest = manifest;
	}

}

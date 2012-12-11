package com.ipoint.coursegenerator.shared;

import com.gwtplatform.dispatch.shared.Result;

public class GenerateCourseResult implements Result {

    private String courseFileName;
    
    public GenerateCourseResult() {
    }
    
    public GenerateCourseResult(String courseFileName) {
	this.courseFileName = courseFileName;
    }

    public String getCourseFileName() {
	return courseFileName;
    }

    public void setCourseFileName(String courseFileName) {
	this.courseFileName = courseFileName;
    }
}

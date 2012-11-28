package com.ipoint.coursegenerator.shared;

import com.gwtplatform.dispatch.shared.UnsecuredActionImpl;
import com.ipoint.coursegenerator.shared.GenerateCourseResult;

public class GenerateCourse extends UnsecuredActionImpl<GenerateCourseResult> {
    
    private String sourceDocFileUuid;

    private String headerLevel;

    private String templateForCoursePages;

    private String courseName;

    public GenerateCourse() {
    }
    
    public String getSourceDocFileUuid() {
	return sourceDocFileUuid;
    }

    public String getHeaderLevel() {
	return headerLevel;
    }

    public String getTemplateForCoursePages() {
	return templateForCoursePages;
    }

    public String getCourseName() {
	return courseName;
    }

    public void setSourceDocFileUuid(String sourceDocFileUuid) {
        this.sourceDocFileUuid = sourceDocFileUuid;
    }

    public void setHeaderLevel(String headerLevel) {
        this.headerLevel = headerLevel;
    }

    public void setTemplateForCoursePages(String templateForCoursePages) {
        this.templateForCoursePages = templateForCoursePages;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}

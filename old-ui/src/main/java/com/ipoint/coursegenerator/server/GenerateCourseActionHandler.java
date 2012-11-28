package com.ipoint.coursegenerator.server;

import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.ipoint.coursegenerator.shared.GenerateCourse;
import com.ipoint.coursegenerator.shared.GenerateCourseResult;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GenerateCourseActionHandler implements
	ActionHandler<GenerateCourse, GenerateCourseResult> {

    public GenerateCourseActionHandler() {
    }

    @Override
    public GenerateCourseResult execute(GenerateCourse action,
    	ExecutionContext context) throws ActionException {
	System.out.println("hello");
        return null;
    }

    @Override
    public void undo(GenerateCourse action, GenerateCourseResult result,
    	ExecutionContext context) throws ActionException {
    }

    @Override
    public Class<GenerateCourse> getActionType() {
        return GenerateCourse.class;
    }
}

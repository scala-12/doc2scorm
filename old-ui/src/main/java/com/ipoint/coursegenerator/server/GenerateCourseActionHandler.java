package com.ipoint.coursegenerator.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.core.Parser;
import com.ipoint.coursegenerator.shared.GenerateCourse;
import com.ipoint.coursegenerator.shared.GenerateCourseResult;

public class GenerateCourseActionHandler implements
	ActionHandler<GenerateCourse, GenerateCourseResult> {
    
    private final ApplicationContext context;

    public GenerateCourseActionHandler() {
	context = new ClassPathXmlApplicationContext("beans.xml");
    }

    @Override
    public GenerateCourseResult execute(GenerateCourse action,
    	ExecutionContext context) throws ActionException {	
	Parser parser = this.context.getBean("parser", Parser.class);
	try {
	    parser.parse(new FileInputStream(action.getSourceDocFileUuid()), action.getHeaderLevel(),
	    	action.getTemplateForCoursePages(), action.getCourseName());
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
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

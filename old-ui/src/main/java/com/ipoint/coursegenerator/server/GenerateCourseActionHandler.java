package com.ipoint.coursegenerator.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ServletContextAware;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.core.Parser;
import com.ipoint.coursegenerator.shared.GenerateCourse;
import com.ipoint.coursegenerator.shared.GenerateCourseResult;

public class GenerateCourseActionHandler implements
	ActionHandler<GenerateCourse, GenerateCourseResult>,
	ServletContextAware {

    private final ApplicationContext context;

    private ServletContext servletContext;

    public GenerateCourseActionHandler() {
	context = new ClassPathXmlApplicationContext("beans.xml");
    }

    @Override
    public GenerateCourseResult execute(GenerateCourse action,
	    ExecutionContext context) throws ActionException {
	Parser parser = this.context.getBean("parser", Parser.class);
	GenerateCourseResult generateCourseResult = null;
	try {
	    String tmpPath = servletContext.getRealPath(File.separator + "tmp");
	    String templateDir = servletContext.getRealPath(File.separator
		    + "templates" + File.separator
		    + action.getTemplateForCoursePages());
	    String courseFile = parser.parse(new FileInputStream(tmpPath
		    + File.separator + action.getSourceDocFileUuid()),
		    action.getHeaderLevel(), templateDir,
		    action.getCourseName(),
		    tmpPath + File.separator + action.getSourceDocFileUuid()
			    + "_dir", action.getFileType());
	    generateCourseResult = new GenerateCourseResult("/tmp/" + action.getSourceDocFileUuid()
			    + "_dir/" + courseFile);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return generateCourseResult;
    }

    @Override
    public void undo(GenerateCourse action, GenerateCourseResult result,
	    ExecutionContext context) throws ActionException {
    }

    @Override
    public Class<GenerateCourse> getActionType() {
	return GenerateCourse.class;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
	this.servletContext = servletContext;
    }
}

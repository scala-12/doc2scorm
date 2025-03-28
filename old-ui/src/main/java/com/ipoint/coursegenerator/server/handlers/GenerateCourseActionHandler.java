package com.ipoint.coursegenerator.server.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ServletContextAware;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.core.Parser;
import com.ipoint.coursegenerator.shared.GenerateCourse;
import com.ipoint.coursegenerator.shared.GenerateCourseResult;

public class GenerateCourseActionHandler implements ActionHandler<GenerateCourse, GenerateCourseResult>,
		ServletContextAware {

	private final ApplicationContext context;

	private ServletContext servletContext;

	private final static Logger log = Logger.getLogger(GenerateCourseActionHandler.class.getName());

	@Autowired
	private HttpSession httpSession;

	@Inject
	public GenerateCourseActionHandler() {
		context = new ClassPathXmlApplicationContext("beans.xml");
	}

	@Override
	public GenerateCourseResult execute(GenerateCourse action, ExecutionContext context) throws ActionException {
		//TODO: add check authorization
		Parser parser = this.context.getBean("parser", Parser.class);
		GenerateCourseResult generateCourseResult = null;
		log.warning("Processing the document for course \"" + action.getCourseName() + "\".");
		
		//TODO: add checking user, domain, time, ipoint-domain
		try {				
			String tmpPath = servletContext.getRealPath(File.separator + "tmp");
			String templateDir = servletContext.getRealPath(File.separator + "templates" + File.separator
					+ action.getTemplateForCoursePages());
			String courseFile = parser.parse(
					new FileInputStream(tmpPath + File.separator + action.getSourceDocFileUuid()),
					action.getHeaderLevel(), 
					templateDir, 
					action.getCourseName(),
					tmpPath + File.separator + action.getSourceDocFileUuid() + "_dir", action.getFileType()
			);
			log.warning("Proccessing finished.");
			generateCourseResult = new GenerateCourseResult("/tmp/" + action.getSourceDocFileUuid() + "_dir/"
					+ courseFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return generateCourseResult;
	}

	@Override
	public void undo(GenerateCourse action, GenerateCourseResult result, ExecutionContext context)
			throws ActionException {
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

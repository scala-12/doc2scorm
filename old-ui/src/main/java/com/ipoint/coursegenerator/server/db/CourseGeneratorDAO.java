package com.ipoint.coursegenerator.server.db;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class CourseGeneratorDAO {
	
	
	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
		
	public static PersistenceManager getPersistenceManager() {
		return pmfInstance.getPersistenceManager();
	}
	
	public static PersistenceManagerFactory getPersistenceManagerFactory() {
		return pmfInstance;
	}
}

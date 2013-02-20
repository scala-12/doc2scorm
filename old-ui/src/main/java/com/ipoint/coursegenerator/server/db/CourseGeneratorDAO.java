package com.ipoint.coursegenerator.server.db;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.api.client.extensions.jdo.auth.oauth2.JdoCredentialStore;

public class CourseGeneratorDAO {
	
	
	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	
	private static final JdoCredentialStore jdoCredentialStore = new JdoCredentialStore(pmfInstance);
	
	public static PersistenceManager getPersistenceManager() {
		return pmfInstance.getPersistenceManager();
	}
	
	public static PersistenceManagerFactory getPersistenceManagerFactory() {
		return pmfInstance;
	}
	
	public static JdoCredentialStore getJdoCredentialStore() {
		return jdoCredentialStore;
	}
}

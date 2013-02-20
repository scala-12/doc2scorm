package com.ipoint.coursegenerator.server.handlers;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.ipoint.coursegenerator.server.db.CourseGeneratorDAO;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class CourseGeneratorServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			List<OrderPlan> plans = objectMapper.readValue(classLoader.getResourceAsStream("plans.json"),
					new TypeReference<List<OrderPlan>>() {
					});
			PersistenceManager pm = CourseGeneratorDAO.getPersistenceManager();
			pm.newQuery(OrderPlan.class).deletePersistentAll();
			pm.makePersistentAll(plans);
			pm.flush();
			pm.close();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

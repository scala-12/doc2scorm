package com.ipoint.coursegenerator.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.gwtplatform.dispatch.server.spring.HandlerModule;
import com.gwtplatform.dispatch.server.spring.configuration.DefaultModule;
import com.ipoint.coursegenerator.server.handlers.CheckoutWithPaypalActionHandler;
import com.ipoint.coursegenerator.server.handlers.GenerateCourseActionHandler;
import com.ipoint.coursegenerator.shared.CheckoutWithPaypal;
import com.ipoint.coursegenerator.shared.GenerateCourse;
import com.ipoint.coursegenerator.shared.GetPurchaseInfo;
import com.ipoint.coursegenerator.server.handlers.GetPurchaseInfoActionHandler;

@Import({ DefaultModule.class })
public class CourseGeneratorServerModule extends HandlerModule {

	@Override
	protected void configureHandlers() {
		bindHandler(GenerateCourse.class, GenerateCourseActionHandler.class);

		bindHandler(CheckoutWithPaypal.class, CheckoutWithPaypalActionHandler.class);

		bindHandler(GetPurchaseInfo.class, GetPurchaseInfoActionHandler.class);
	}

	@Bean
	public GenerateCourseActionHandler getGenerateCourseActionHandler() {
		return new GenerateCourseActionHandler();
	}

	@Bean
	public CheckoutWithPaypalActionHandler getCheckoutWithPaypalActionHandler() {
		return new CheckoutWithPaypalActionHandler();
	}

	@Bean
	public GetPurchaseInfoActionHandler getGetPurchaseInfoActionHandler() {
		return new GetPurchaseInfoActionHandler();
	}
}

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
import com.ipoint.coursegenerator.shared.GetSubscribed;
import com.ipoint.coursegenerator.server.handlers.GetSubscribedActionHandler;
import com.ipoint.coursegenerator.shared.BuyNow;
import com.ipoint.coursegenerator.server.handlers.BuyNowActionHandler;

@Import({ DefaultModule.class })
public class CourseGeneratorServerModule extends HandlerModule {

	@Override
	protected void configureHandlers() {
		bindHandler(GenerateCourse.class, GenerateCourseActionHandler.class);

		bindHandler(CheckoutWithPaypal.class, CheckoutWithPaypalActionHandler.class);

		bindHandler(GetPurchaseInfo.class, GetPurchaseInfoActionHandler.class);

		bindHandler(GetSubscribed.class, GetSubscribedActionHandler.class);

		bindHandler(BuyNow.class, BuyNowActionHandler.class);
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

	@Bean
	public GetSubscribedActionHandler getGetOrderPlanListActionHandler() {
		return new GetSubscribedActionHandler();
	}

	@Bean
	public BuyNowActionHandler getBuyNowActionHandler() {
		return new BuyNowActionHandler();
	}
}
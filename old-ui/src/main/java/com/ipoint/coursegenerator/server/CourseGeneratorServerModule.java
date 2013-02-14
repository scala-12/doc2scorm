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
import com.ipoint.coursegenerator.shared.GetOrderPlanList;
import com.ipoint.coursegenerator.server.handlers.GetOrderPlanListActionHandler;
import com.ipoint.coursegenerator.shared.BuyNow;
import com.ipoint.coursegenerator.server.handlers.BuyNowActionHandler;
import com.ipoint.coursegenerator.shared.GetTrialPeriod;
import com.ipoint.coursegenerator.server.handlers.GetTrialPeriodActionHandler;

@Import({ DefaultModule.class })
public class CourseGeneratorServerModule extends HandlerModule {

	@Override
	protected void configureHandlers() {
		bindHandler(GenerateCourse.class, GenerateCourseActionHandler.class);

		bindHandler(CheckoutWithPaypal.class, CheckoutWithPaypalActionHandler.class);

		bindHandler(GetPurchaseInfo.class, GetPurchaseInfoActionHandler.class);
	
		bindHandler(GetOrderPlanList.class, GetOrderPlanListActionHandler.class);

		bindHandler(BuyNow.class, BuyNowActionHandler.class);

		bindHandler(GetTrialPeriod.class, GetTrialPeriodActionHandler.class);
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
	public GetOrderPlanListActionHandler getGetOrderPlanListActionHandler() {
		return new GetOrderPlanListActionHandler();
	}

	@Bean
	public BuyNowActionHandler getBuyNowActionHandler() {
		return new BuyNowActionHandler();
	}

	@Bean
	public GetTrialPeriodActionHandler getGetTrialPeriodActionHandler() {
		return new GetTrialPeriodActionHandler();
	}
}

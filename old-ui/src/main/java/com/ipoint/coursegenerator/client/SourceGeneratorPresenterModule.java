package com.ipoint.coursegenerator.client;

import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import com.ipoint.coursegenerator.client.presenter.CourseGeneratorFormPresenter;
import com.ipoint.coursegenerator.client.presenter.CourseGeneratorMainPresenter;
import com.ipoint.coursegenerator.client.presenter.OrderPresenter;
import com.ipoint.coursegenerator.client.presenter.PurchaseConfirmingPresenter;
import com.ipoint.coursegenerator.client.view.CourseGeneratorFormView;
import com.ipoint.coursegenerator.client.view.CourseGeneratorMainView;
import com.ipoint.coursegenerator.client.view.OrderView;
import com.ipoint.coursegenerator.client.view.PurchaseConfirmingView;

public class SourceGeneratorPresenterModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		// Default implementation of standard resources
		// install(new DefaultModule(CourseGeneratorPlaceManager.class));
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);
		// bind(RootPresenter.class).asEagerSingleton();
		bind(PlaceManager.class).to(CourseGeneratorPlaceManager.class).in(Singleton.class);
		bind(RootPresenter.class).asEagerSingleton();

		bind(GoogleAnalytics.class).to(GoogleAnalyticsImpl.class).in(Singleton.class);

		// Presenters

		bindPresenter(CourseGeneratorMainPresenter.class, CourseGeneratorMainPresenter.MyView.class,
				CourseGeneratorMainView.class, CourseGeneratorMainPresenter.MyProxy.class);

		bindPresenter(CourseGeneratorFormPresenter.class, CourseGeneratorFormPresenter.MyView.class,
				CourseGeneratorFormView.class, CourseGeneratorFormPresenter.MyProxy.class);

		bindPresenter(OrderPresenter.class, OrderPresenter.MyView.class, OrderView.class, OrderPresenter.MyProxy.class);
		
		bindPresenter(PurchaseConfirmingPresenter.class, PurchaseConfirmingPresenter.MyView.class, PurchaseConfirmingView.class, PurchaseConfirmingPresenter.MyProxy.class);
	}
}
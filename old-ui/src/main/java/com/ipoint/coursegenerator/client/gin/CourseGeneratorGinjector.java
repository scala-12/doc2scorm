package com.ipoint.coursegenerator.client.gin;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.ipoint.coursegenerator.client.SourceGeneratorPresenterModule;
import com.ipoint.coursegenerator.client.presenter.CourseGeneratorFormPresenter;
import com.ipoint.coursegenerator.client.presenter.CourseGeneratorMainPresenter;
import com.ipoint.coursegenerator.client.presenter.PurchaseConfirmingPresenter;

@GinModules({ DispatchAsyncModule.class, SourceGeneratorPresenterModule.class })
public interface CourseGeneratorGinjector extends Ginjector {
    EventBus getEventBus();

    PlaceManager getPlaceManager();

    AsyncProvider<CourseGeneratorMainPresenter> getCourseGeneratorMainPresenter();

    AsyncProvider<CourseGeneratorFormPresenter> getCourseGeneratorFormPresenter();
    
    AsyncProvider<PurchaseConfirmingPresenter> getPurchaseConfirmingPresenter();
}
package com.ipoint.coursegenerator.client;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

public class CourseGeneratorPlaceManager extends PlaceManagerImpl {

    @Inject
    public CourseGeneratorPlaceManager(EventBus eventBus,
	    TokenFormatter tokenFormatter) {
	super(eventBus, tokenFormatter);
    }

    @Override
    public void revealDefaultPlace() {
	revealPlace(new PlaceRequest(NameTokens.coursegeneratorform));
    }

}

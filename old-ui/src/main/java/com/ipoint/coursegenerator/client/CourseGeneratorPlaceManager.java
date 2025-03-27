package com.ipoint.coursegenerator.client;

import com.google.gwt.user.client.Window.Location;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

public class CourseGeneratorPlaceManager extends PlaceManagerImpl {

	@Inject
	public CourseGeneratorPlaceManager(EventBus eventBus, TokenFormatter tokenFormatter) {
		super(eventBus, tokenFormatter);
	}

	@Override
	public void revealDefaultPlace() {
		if (Location.getHref().matches("^(https?)://[^\\s/$.?#].[^\\s]*/purchase(([\\?\\#].*)|)")) {
			revealPlace(new PlaceRequest(NameTokens.purchase), false);
		} else if (Location.getHref().matches("^(https?)://[^\\s/$.?#].[^\\s]*/orderchoice(([\\?\\#].*)|)")) {
			revealPlace(new PlaceRequest(NameTokens.order), false);
		} else {
			revealPlace(new PlaceRequest(NameTokens.coursegeneratorform), false);
		}
	}

}

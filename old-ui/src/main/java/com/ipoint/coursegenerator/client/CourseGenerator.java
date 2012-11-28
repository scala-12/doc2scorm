package com.ipoint.coursegenerator.client;

import com.gwtplatform.mvp.client.DelayedBindRegistry;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.ipoint.coursegenerator.client.gin.CourseGeneratorGinjector;


public class CourseGenerator implements EntryPoint {
  public final CourseGeneratorGinjector ginjector = GWT.create(CourseGeneratorGinjector.class);

  public void onModuleLoad() {

    DelayedBindRegistry.bind(ginjector);

    ginjector.getPlaceManager().revealDefaultPlace();
  }
}

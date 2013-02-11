package com.ipoint.coursegenerator.client.presenter;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.ipoint.coursegenerator.client.NameTokens;
import com.ipoint.coursegenerator.shared.GenerateCourse;
import com.ipoint.coursegenerator.shared.GenerateCourseResult;

public class CourseGeneratorFormPresenter extends
		Presenter<CourseGeneratorFormPresenter.MyView, CourseGeneratorFormPresenter.MyProxy> implements
		FileSelectUIHandler {

	public interface MyView extends View, HasUiHandlers<FileSelectUIHandler> {
		public String getSourceDocFileUuid();

		public String getHeaderLevel();

		public String getTemplateForCoursePages();

		public String getCourseName();

		public String getFileType();

		public void setGenerateProgressBarCompleted();

		public void setGenerateProgressBarFailed();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.coursegeneratorform)
	public interface MyProxy extends ProxyPlace<CourseGeneratorFormPresenter> {
	}

	private final DispatchAsync dispatcher;

	@Inject
	public CourseGeneratorFormPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
			PlaceManager placeManager, DispatchAsync dispatcher) {
		super(eventBus, view, proxy);
		this.dispatcher = dispatcher;
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, CourseGeneratorMainPresenter.SLOT_mainContent, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		getView().setUiHandlers(this);
	}

	@Override
	public void generateButtonClicked() {
		GenerateCourse generateCourse = new GenerateCourse();
		generateCourse.setSourceDocFileUuid(getView().getSourceDocFileUuid());
		generateCourse.setHeaderLevel(getView().getHeaderLevel());
		generateCourse.setTemplateForCoursePages(getView().getTemplateForCoursePages());
		generateCourse.setCourseName(getView().getCourseName());
		generateCourse.setFileType(getView().getFileType());
		dispatcher.execute(generateCourse, new AsyncCallback<GenerateCourseResult>() {
			@Override
			public void onFailure(Throwable caught) {
				getView().setGenerateProgressBarFailed();
			}

			@Override
			public void onSuccess(GenerateCourseResult result) {
				Window.Location.replace(result.getCourseFileName());
				getView().setGenerateProgressBarCompleted();
			}
		});
	}
}

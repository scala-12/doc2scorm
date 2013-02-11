package com.ipoint.coursegenerator.client.view;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.ipoint.coursegenerator.client.presenter.CourseGeneratorMainPresenter;

public class CourseGeneratorMainView extends ViewImpl implements CourseGeneratorMainPresenter.MyView {

	private final Widget widget;

	@UiField
	HTMLPanel mainContent;

	public interface Binder extends UiBinder<Widget, CourseGeneratorMainView> {
	}

	@Inject
	public CourseGeneratorMainView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(Object slot, Widget content) {
		if (slot == CourseGeneratorMainPresenter.SLOT_mainContent) {
			mainContent.clear();
			mainContent.add(content);
		}
	}
}

package com.ipoint.coursegenerator.client.view;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.ipoint.coursegenerator.client.presenter.OrderWidgetPresenter;

public class OrderWidgetView extends ViewImpl implements OrderWidgetPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, OrderWidgetView> {
	}

	@Inject
	public OrderWidgetView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
}

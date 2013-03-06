package com.ipoint.coursegenerator.client.view;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.ipoint.coursegenerator.client.presenter.PlanChoiceWidgetPresenter;
import com.ipoint.coursegenerator.client.presenter.uihandlers.OrderUiHandlers;

public class PlanChoiceWidgetView extends ViewWithUiHandlers<OrderUiHandlers> implements
		PlanChoiceWidgetPresenter.MyView {

	private final Widget widget;

	FormPanel form;

	@UiField
	HTMLPanel subscribe;
	
	@UiField
	HTMLPanel unsubscribe;
	
	@Override
	public Widget asWidget() {
		return widget;
	}

	public interface Binder extends UiBinder<Widget, PlanChoiceWidgetView> {
	}

	@Inject
	public PlanChoiceWidgetView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public void setSubscribed(boolean subscribed, String userEmail) {
		if (subscribed) {
			subscribe.setVisible(false);
			unsubscribe.setVisible(true);
		} else {
			unsubscribe.setVisible(false);
			subscribe.setVisible(true);			
			form = FormPanel.wrap(DOM.getElementById("weekForm"));
			form.add(new Hidden("custom", userEmail));
			form = FormPanel.wrap(DOM.getElementById("monthForm"));
			form.add(new Hidden("custom", userEmail));
		}
	}
}

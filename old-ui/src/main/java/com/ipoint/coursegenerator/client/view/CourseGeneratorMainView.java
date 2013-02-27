package com.ipoint.coursegenerator.client.view;

import com.github.gwtbootstrap.client.ui.DropdownContainer;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.Nav;
import com.github.gwtbootstrap.client.ui.NavText;
import com.github.gwtbootstrap.client.ui.base.Style;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.ipoint.coursegenerator.client.Messages;
import com.ipoint.coursegenerator.client.presenter.CourseGeneratorMainPresenter;
import com.ipoint.coursegenerator.client.presenter.uihandlers.MessagesHolder;

public class CourseGeneratorMainView extends ViewWithUiHandlers<MessagesHolder> implements CourseGeneratorMainPresenter.MyView,
		NativePreviewHandler {

	private final Widget widget;

	@UiField
	HTMLPanel mainContent;

	@UiField
	FluidContainer buyPanel;

	@UiField
	Modal lockScreen;

	@UiField
	HTMLPanel panel;

	@UiField
	NavText username;

	@UiField
	NavText subscriptionStatus;

	@UiField
	DropdownContainer dropdown;

	@UiField
	Nav buyProNav;
	
	public interface Binder extends UiBinder<Widget, CourseGeneratorMainView> {
	}

	@Inject
	public CourseGeneratorMainView(final Binder binder) {
		widget = binder.createAndBindUi(this);
		Event.addNativePreviewHandler(this);		
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
		} else if (slot == CourseGeneratorMainPresenter.SLOT_NAVBAR_CONTENT) {
			buyPanel.clear();
			buyPanel.add(content);
		}
	}

	@Override
	public void showLockingDialog() {
		lockScreen.show();
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		NativeEvent ne = event.getNativeEvent();
		if (ne.getType().equalsIgnoreCase("click")) {
			if (buyPanel.getAbsoluteLeft() != 0) {
				if ((ne.getClientX() < buyPanel.getAbsoluteLeft() || ne.getClientX() > (buyPanel.getAbsoluteLeft() + buyPanel
						.getOffsetWidth()))
						|| (ne.getClientY() < buyPanel.getAbsoluteTop() || (ne.getClientY() > buyPanel.getAbsoluteTop()
								+ buyPanel.getOffsetHeight()))) {
					dropdown.getWidget(1).getElement().setAttribute("style", "display:none;");
					dropdown.removeStyleName("open");
					event.cancel();
				}
			}
		}
	}

	@UiHandler("dropdown")
	public void onClick(ClickEvent event) {
		if (dropdown.getWidget(1).getElement().getAttribute("style").equals("display:none;")) {
			dropdown.getWidget(1).getElement().setAttribute("style", "display:block;");
		}
	}

	@Override
	public void setUsername(String username) {
		this.username.setText(username);
	}

	@Override
	public void setDaysRemains(int daysRemains) {
		if (daysRemains > 0) {
			this.subscriptionStatus.setText(getUiHandlers().getMessages().remainingPrefix() +" "+ daysRemains  +" "+ getUiHandlers().getMessages().remainingSuffix());
		}
	}
}

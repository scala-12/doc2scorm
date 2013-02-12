package com.ipoint.coursegenerator.client.view;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.ipoint.coursegenerator.client.presenter.OrderPresenter;
import com.ipoint.coursegenerator.client.presenter.OrderUiHandlers;

public class OrderView extends ViewWithUiHandlers<OrderUiHandlers> implements OrderPresenter.MyView {

	private final Widget widget;
	
	@UiField
	Image paypalButton;
	
	@UiField
	Modal lockScreen;
	
	@Override
	public Widget asWidget() {
		return widget;
	}
	
	public interface Binder extends UiBinder<Widget, OrderView> {
    }

    @Inject
    public OrderView(final Binder binder) {
        widget = binder.createAndBindUi(this);
        paypalButton.getElement().getStyle().setCursor(Cursor.POINTER); 
    }

    @UiHandler("paypalButton")
    public void onPayPalButtonClicked(ClickEvent event) {
    	lockScreen.show();
    	this.getUiHandlers().onPayPalButtonClicked("0");
    }
}

package com.ipoint.coursegenerator.client.view;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.ipoint.coursegenerator.client.presenter.OrderPresenter;
import com.ipoint.coursegenerator.client.presenter.uihandlers.OrderUiHandlers;

public class OrderView extends ViewWithUiHandlers<OrderUiHandlers> implements OrderPresenter.MyView {

	private final Widget widget;

	
	@Override
	public Widget asWidget() {
		return widget;
	}
	
	public interface Binder extends UiBinder<Widget, OrderView> {
    }

    @Inject
    public OrderView(final Binder binder) {
        widget = binder.createAndBindUi(this);
        
    }

	@Override
	public void setSubscribed() {
		
	}
}

package com.ipoint.coursegenerator.client.view;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.ipoint.coursegenerator.client.presenter.OrderPresenter;

public class OrderView extends ViewImpl implements OrderPresenter.MyView {

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
}

package com.ipoint.coursegenerator.client.view;

import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.ipoint.coursegenerator.client.presenter.PurchaseConfirmingPresenter;
import com.ipoint.coursegenerator.client.presenter.PurchaseConfirmingUiHandlers;

public class PurchaseConfirmingView extends ViewWithUiHandlers<PurchaseConfirmingUiHandlers> implements PurchaseConfirmingPresenter.MyView {

	private final Widget widget;

	@UiField
	Heading price;

	public interface Binder extends UiBinder<Widget, PurchaseConfirmingView> {
	}

	@Inject
	public PurchaseConfirmingView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setDetails() {
		price.setText("10");
	}

	@UiHandler("submit")
	public void onBuyNowClicked(ClickEvent event) {
		this.getUiHandlers().buyNow();
	}
}

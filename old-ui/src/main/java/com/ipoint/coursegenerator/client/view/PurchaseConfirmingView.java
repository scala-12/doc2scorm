package com.ipoint.coursegenerator.client.view;

import com.github.gwtbootstrap.client.ui.Container;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.ipoint.coursegenerator.client.presenter.PurchaseConfirmingPresenter;
import com.ipoint.coursegenerator.client.presenter.uihandlers.PurchaseConfirmingUiHandlers;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class PurchaseConfirmingView extends ViewWithUiHandlers<PurchaseConfirmingUiHandlers> implements PurchaseConfirmingPresenter.MyView {

	private final Widget widget;
	
	@UiField
	Container name;
	
	@UiField
	Container description;

	@UiField
	Container price;
	
	@UiField
	Modal lockScreen;

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
	public void setDetails(OrderPlan orderPlan) {
		name.clear();
		price.clear();
		description.clear();
		name.add(new CaptionPanel(orderPlan.getName()));
		price.add(new CaptionPanel(orderPlan.getAmount() + " USD"));
		description.add(new CaptionPanel(orderPlan.getDescription()));
	}

	@UiHandler("submit")
	public void onBuyNowClicked(ClickEvent event) {
		lockScreen.show();
		this.getUiHandlers().buyNow();
	}
}

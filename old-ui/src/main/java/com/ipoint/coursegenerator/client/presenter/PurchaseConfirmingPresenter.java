package com.ipoint.coursegenerator.client.presenter;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
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
import com.ipoint.coursegenerator.client.presenter.uihandlers.PurchaseConfirmingUiHandlers;
import com.ipoint.coursegenerator.shared.BuyNow;
import com.ipoint.coursegenerator.shared.BuyNowResult;
import com.ipoint.coursegenerator.shared.GetPurchaseInfo;
import com.ipoint.coursegenerator.shared.GetPurchaseInfoResult;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class PurchaseConfirmingPresenter extends
		Presenter<PurchaseConfirmingPresenter.MyView, PurchaseConfirmingPresenter.MyProxy> implements
		PurchaseConfirmingUiHandlers {

	public interface MyView extends View, HasUiHandlers<PurchaseConfirmingUiHandlers> {
		public void setDetails(OrderPlan orderPlan);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.purchase)
	public interface MyProxy extends ProxyPlace<PurchaseConfirmingPresenter> {
	}

	private final DispatchAsync dispatcher;

	private final PlaceManager placeManager;
	
	@Inject
	public PurchaseConfirmingPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
			DispatchAsync dispatcher, PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.dispatcher = dispatcher;
		this.placeManager = placeManager;
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
	protected void onReveal() {
		super.onReveal();
		GetPurchaseInfo getPurchaseInfo = new GetPurchaseInfo(Location.getParameter("subscriptionId"));		
		dispatcher.execute(getPurchaseInfo, new AsyncCallback<GetPurchaseInfoResult>() {
			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(GetPurchaseInfoResult result) {
				getView().setDetails(result.getPlan());
			}
		});
	}

	@Override
	public void buyNow() {
		BuyNow buyNow = new BuyNow();
		dispatcher.execute(buyNow, new AsyncCallback<BuyNowResult>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			
			@Override
			public void onSuccess(BuyNowResult result) {
				Window.open(result.getRedirectUrl(), "_self", null);
			}
		});
	}
}

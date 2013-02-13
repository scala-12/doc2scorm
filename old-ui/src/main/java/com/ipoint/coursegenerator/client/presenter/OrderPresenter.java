package com.ipoint.coursegenerator.client.presenter;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.ipoint.coursegenerator.client.NameTokens;
import com.ipoint.coursegenerator.client.presenter.uihandlers.OrderUiHandlers;
import com.ipoint.coursegenerator.shared.CheckoutWithPaypal;
import com.ipoint.coursegenerator.shared.CheckoutWithPaypalResult;
import com.ipoint.coursegenerator.shared.GetOrderPlanList;
import com.ipoint.coursegenerator.shared.GetOrderPlanListResult;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class OrderPresenter extends Presenter<OrderPresenter.MyView, OrderPresenter.MyProxy> implements OrderUiHandlers {

	private final DispatchAsync dispatcher;

	public interface MyView extends View, HasUiHandlers<OrderUiHandlers> {
		public void showOrderPlanList(List<OrderPlan> orderPlanList);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.order)
	public interface MyProxy extends ProxyPlace<OrderPresenter> {
	}

	@Inject
	public OrderPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, DispatchAsync dispatcher) {
		super(eventBus, view, proxy);
		this.dispatcher = dispatcher;
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.getView().setUiHandlers(this);
	}
	
	@Override
	protected void onReveal() {
		super.onReveal();
		GetOrderPlanList getOrderPlanList = new GetOrderPlanList();
		dispatcher.execute(getOrderPlanList, new AsyncCallback<GetOrderPlanListResult>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(GetOrderPlanListResult result) {
				getView().showOrderPlanList(result.getOrderPlanList());
			}
		});
	}
	
	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, CourseGeneratorMainPresenter.SLOT_mainContent, this);
	}

	@Override
	public void onPayPalButtonClicked(String subscriptionId) {
		CheckoutWithPaypal checkoutWithPaypal = new CheckoutWithPaypal(subscriptionId);
		dispatcher.execute(checkoutWithPaypal, new AsyncCallback<CheckoutWithPaypalResult>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(CheckoutWithPaypalResult result) {
				Window.open(
						"https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="
								+ result.getToken(), "_self", null);
			}
		});
	}
}

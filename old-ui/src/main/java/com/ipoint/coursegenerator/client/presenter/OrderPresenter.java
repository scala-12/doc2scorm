package com.ipoint.coursegenerator.client.presenter;


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
import com.ipoint.coursegenerator.shared.GetSubscribed;
import com.ipoint.coursegenerator.shared.GetSubscribedResult;

public class OrderPresenter extends Presenter<OrderPresenter.MyView, OrderPresenter.MyProxy> implements OrderUiHandlers {

	private final DispatchAsync dispatcher;

	public interface MyView extends View, HasUiHandlers<OrderUiHandlers> {
		public void setSubscribed();
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
		GetSubscribed getSubscribed = new GetSubscribed();
		dispatcher.execute(getSubscribed, new AsyncCallback<GetSubscribedResult>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(GetSubscribedResult result) {
				getView().setSubscribed();
			}
		});
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, CourseGeneratorMainPresenter.SLOT_mainContent, this);
	}

	@Override
	public void showLockingDialog() {
		
	}
}

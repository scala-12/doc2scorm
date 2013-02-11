package com.ipoint.coursegenerator.client.presenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.ipoint.coursegenerator.client.NameTokens;

public class OrderPresenter extends Presenter<OrderPresenter.MyView, OrderPresenter.MyProxy> {
	public interface MyView extends View {		
	}
	
	@ProxyCodeSplit
	@NameToken(NameTokens.order)
	public interface MyProxy extends ProxyPlace<OrderPresenter> {
	}
	
	@Inject
	public OrderPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy) {
		super(eventBus, view, proxy);
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, CourseGeneratorMainPresenter.SLOT_mainContent, this);
	}
}

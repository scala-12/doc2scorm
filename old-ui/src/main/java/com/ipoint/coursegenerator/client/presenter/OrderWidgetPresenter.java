package com.ipoint.coursegenerator.client.presenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class OrderWidgetPresenter extends PresenterWidget<OrderWidgetPresenter.MyView> {

	public interface MyView extends View {
	}

	@Inject
	public OrderWidgetPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}
}

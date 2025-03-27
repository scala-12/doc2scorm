package com.ipoint.coursegenerator.client.presenter;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.ipoint.coursegenerator.client.presenter.uihandlers.OrderUiHandlers;
import com.ipoint.coursegenerator.shared.GetSubscribed;
import com.ipoint.coursegenerator.shared.GetSubscribedResult;

public class PlanChoiceWidgetPresenter extends PresenterWidget<PlanChoiceWidgetPresenter.MyView> implements OrderUiHandlers {

	private final DispatchAsync dispatcher;
	
	private CourseGeneratorMainPresenter courseGeneratorMainPresenter;

	public interface MyView extends View, HasUiHandlers<OrderUiHandlers> {
		public void setSubscribed(boolean subscribed, String userEmail);
	}

	@Inject
	public PlanChoiceWidgetPresenter(final EventBus eventBus, final MyView view, DispatchAsync dispatcher) {
		super(eventBus, view);
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
				getView().setSubscribed(result.isSubscribed(), result.getUserId());
				courseGeneratorMainPresenter.setUsername(result.getUsername());
				courseGeneratorMainPresenter.setDaysRemains(result.getDaysRemains());
			}
		});
	}


	@Override
	public void showLockingDialog() {
		courseGeneratorMainPresenter.showLockingDialog();
	}

	public void setMainPresenter(CourseGeneratorMainPresenter courseGeneratorMainPresenter) {
		this.courseGeneratorMainPresenter = courseGeneratorMainPresenter;		
	}
}

package com.ipoint.coursegenerator.client.presenter;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.ipoint.coursegenerator.client.presenter.uihandlers.OrderUiHandlers;
import com.ipoint.coursegenerator.shared.CheckoutWithPaypal;
import com.ipoint.coursegenerator.shared.CheckoutWithPaypalResult;
import com.ipoint.coursegenerator.shared.GetOrderPlanList;
import com.ipoint.coursegenerator.shared.GetOrderPlanListResult;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class PlanChoiceWidgetPresenter extends PresenterWidget<PlanChoiceWidgetPresenter.MyView> implements OrderUiHandlers {

	private final DispatchAsync dispatcher;
	
	private CourseGeneratorMainPresenter courseGeneratorMainPresenter;

	public interface MyView extends View, HasUiHandlers<OrderUiHandlers> {
		public void showOrderPlanList(List<OrderPlan> orderPlanList);
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
		GetOrderPlanList getOrderPlanList = new GetOrderPlanList();
		dispatcher.execute(getOrderPlanList, new AsyncCallback<GetOrderPlanListResult>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(GetOrderPlanListResult result) {
				getView().showOrderPlanList(result.getOrderPlanList());
				courseGeneratorMainPresenter.setUsername(result.getUsername());
				courseGeneratorMainPresenter.setDaysRemains(result.getDaysRemains());
			}
		});
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

	@Override
	public void showLockingDialog() {
		courseGeneratorMainPresenter.showLockingDialog();
	}

	public void setMainPresenter(CourseGeneratorMainPresenter courseGeneratorMainPresenter) {
		this.courseGeneratorMainPresenter = courseGeneratorMainPresenter;		
	}
}

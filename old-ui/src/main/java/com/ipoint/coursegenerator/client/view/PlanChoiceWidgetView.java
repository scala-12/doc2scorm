package com.ipoint.coursegenerator.client.view;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.ipoint.coursegenerator.client.presenter.PlanChoiceWidgetPresenter;
import com.ipoint.coursegenerator.client.presenter.uihandlers.OrderUiHandlers;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class PlanChoiceWidgetView extends ViewWithUiHandlers<OrderUiHandlers> implements PlanChoiceWidgetPresenter.MyView {

	private final Widget widget;
	
	@UiField
	Image paypalButton;
	

	@UiField
	Controls radioGroup;
	
	List<RadioButton> radioItems;
	
	@Override
	public Widget asWidget() {
		return widget;
	}
	
	public interface Binder extends UiBinder<Widget, PlanChoiceWidgetView> {
    }

    @Inject
    public PlanChoiceWidgetView(final Binder binder) {
        widget = binder.createAndBindUi(this);
        paypalButton.getElement().getStyle().setCursor(Cursor.POINTER);        
    }

    @UiHandler("paypalButton")
    public void onPayPalButtonClicked(ClickEvent event) {
    	this.getUiHandlers().showLockingDialog();
    	String checkId = "";
    	for (RadioButton button : radioItems) {
    		if (button.getValue()) {
    			checkId = button.getId();
    		}
    	}
    	this.getUiHandlers().onPayPalButtonClicked(checkId);
    }
    
    @Override
	public void showOrderPlanList(List<OrderPlan> orderPlanList) {
		radioItems = new ArrayList<RadioButton>();
		radioGroup.clear();
		for (OrderPlan plan : orderPlanList) {			
			RadioButton button = new RadioButton("group1");
			button.setHTML(plan.getName() + " - " + plan.getAmount() + " USD");			
			button.setTitle(plan.getDescription());
			button.setId(String.valueOf(plan.getId()));
			radioGroup.add(button);
			radioItems.add(button);
		}
		if (radioItems.size() > 0) {
			radioItems.get(0).setValue(true);
		}
	}
}

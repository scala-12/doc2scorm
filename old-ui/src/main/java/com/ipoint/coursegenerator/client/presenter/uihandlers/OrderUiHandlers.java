package com.ipoint.coursegenerator.client.presenter.uihandlers;

import com.gwtplatform.mvp.client.UiHandlers;

public interface OrderUiHandlers extends UiHandlers {
	public void onPayPalButtonClicked(String subscriptionId);
	public void showLockingDialog();
}

package com.ipoint.coursegenerator.client.presenter;

import com.gwtplatform.mvp.client.UiHandlers;

public interface OrderUiHandlers extends UiHandlers {
	public void onPayPalButtonClicked(String subscriptionId);
}

package com.ipoint.coursegenerator.server.handlers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.ipoint.coursegenerator.server.paypal.PaypalUtils;
import com.ipoint.coursegenerator.shared.GetPurchaseInfo;
import com.ipoint.coursegenerator.shared.GetPurchaseInfoResult;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetPurchaseInfoActionHandler implements ActionHandler<GetPurchaseInfo, GetPurchaseInfoResult> {

	@Autowired
	private HttpSession httpSession;
	
	public GetPurchaseInfoActionHandler() {
	}

	@Override
	public GetPurchaseInfoResult execute(GetPurchaseInfo action, ExecutionContext context) throws ActionException {
		PaypalUtils paypal = new PaypalUtils();
		paypal.getCheckoutCode((String)httpSession.getAttribute("paypalToken"));
		return null;
	}

	@Override
	public void undo(GetPurchaseInfo action, GetPurchaseInfoResult result, ExecutionContext context) throws ActionException {
	}

	@Override
	public Class<GetPurchaseInfo> getActionType() {
		return GetPurchaseInfo.class;
	}
}

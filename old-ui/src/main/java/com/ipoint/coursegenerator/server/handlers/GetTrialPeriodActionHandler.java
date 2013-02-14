package com.ipoint.coursegenerator.server.handlers;

import java.util.Calendar;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.ipoint.coursegenerator.server.db.model.User;
import com.ipoint.coursegenerator.shared.GetTrialPeriod;
import com.ipoint.coursegenerator.shared.GetTrialPeriodResult;

public class GetTrialPeriodActionHandler implements ActionHandler<GetTrialPeriod, GetTrialPeriodResult> {

	@Autowired
	private HttpSession httpSession;

	public static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	public GetTrialPeriodActionHandler() {
	}

	@Override
	public GetTrialPeriodResult execute(GetTrialPeriod action, ExecutionContext context) throws ActionException {
		GetTrialPeriodResult result = null;
		PersistenceManager pm = pmfInstance.getPersistenceManager();
		Transaction trans = pm.currentTransaction();
		trans.begin();
		User user = pm.getObjectById(User.class, httpSession.getAttribute("userId"));
		pm.refresh(user);
		if (!user.isTrialUsed()) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, 3);
			user.setExpirationDate(calendar.getTime());
			user.setTrialUsed(true);
			result = new GetTrialPeriodResult(false);
		} else {
			result = new GetTrialPeriodResult(true);
		}
		trans.commit();
		return result;
	}

	@Override
	public void undo(GetTrialPeriod action, GetTrialPeriodResult result, ExecutionContext context)
			throws ActionException {
	}

	@Override
	public Class<GetTrialPeriod> getActionType() {
		return GetTrialPeriod.class;
	}
}

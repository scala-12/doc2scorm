package com.ipoint.coursegenerator.shared;

import com.gwtplatform.dispatch.shared.Result;

public class GetTrialPeriodResult implements Result {

	private boolean trialUsed;
	
	public GetTrialPeriodResult() {
	}
	
	public GetTrialPeriodResult(boolean trialUsed) {
		this.trialUsed = trialUsed;
	}
	
	public boolean isTrialUsed() {
		return trialUsed;
	}
}

package com.ipoint.coursegenerator.server.marketplace;

import java.util.List;

public class CustomerLicense {

	enum State {
		
		ACTIVE("ACTIVE"), UNLICENSED("UNLICENSED"), EXPIRED("EXPIRED");
		private String state;

		private State(String state) {
			this.state = state;
		}
	}

	enum SubscriptionState {

		ACTIVE("ACTIVE"), TRIAL("TRIAL"), CANCELED("CANCELED"), LOCKED("LOCKED"), NOTEXIST("NOTEXIST");
		private String subscriptionState;

		private SubscriptionState(String subscriptionState) {
			this.subscriptionState = subscriptionState;
		}
	}

	String id;
	String applicationId;
	String customerId;
	String state;

	List<MarketplaceEdition> editions;
	// subscription object The most recent subscription, corresponding to the
	// subscription state.
	String subscriptionState;

	long nextRenewalTimestamp;
}

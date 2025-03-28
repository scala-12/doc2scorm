package com.ipoint.coursegenerator.server.paypal;

import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import com.ipoint.coursegenerator.server.exception.PaypalSetCheckoutCodeException;
import com.paypal.sdk.core.nvp.NVPDecoder;
import com.paypal.sdk.core.nvp.NVPEncoder;
import com.paypal.sdk.profiles.APIProfile;
import com.paypal.sdk.profiles.ProfileFactory;
import com.paypal.sdk.services.NVPCallerServices;

@Service
public class PaypalUtils implements ServletContextAware {

	private NVPCallerServices caller = null;

	private ServletContext servletContext;

	private final static Logger log = Logger.getLogger(PaypalUtils.class.getName());

	public String setCheckoutCode(String returnURL, String cancelURL, String amount, String paymentType,
			String currencyCode) throws PaypalSetCheckoutCodeException {

		NVPEncoder encoder = new NVPEncoder();
		NVPDecoder decoder = new NVPDecoder();

		try {
			caller = new NVPCallerServices();
			APIProfile profile = ProfileFactory.createSignatureAPIProfile();
			profile.setAPIUsername(servletContext.getInitParameter("PAYPAL_API_USERNAME"));
			profile.setAPIPassword(servletContext.getInitParameter("PAYPAL_API_PASSWORD"));
			profile.setSignature(servletContext.getInitParameter("PAYPAL_API_SIGNATURE"));
			profile.setEnvironment("live");
			profile.setSubject("");
			caller.setAPIProfile(profile);
			encoder.add("VERSION", "51.0");
			encoder.add("METHOD", "SetExpressCheckout");

			// Add request-specific fields to the request string.
			encoder.add("RETURNURL", returnURL);
			encoder.add("CANCELURL", cancelURL);
			encoder.add("AMT", amount);
			encoder.add("PAYMENTACTION", paymentType);
			encoder.add("CURRENCYCODE", currencyCode);

			// Execute the API operation and obtain the response.
			String NVPRequest = encoder.encode();
			String NVPResponse = caller.call(NVPRequest);
			decoder.decode(NVPResponse);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (decoder.get("ACK") != null && decoder.get("ACK").equals("Success")) {
			return decoder.get("TOKEN");
		} else {
			log.warning(decoder.getMap().values().toString());
			throw new PaypalSetCheckoutCodeException("Failed to set PayPal Checkout Code.");
		}
	}

	public String executeCheckoutCode(String token, String payerID, String amount) {

		NVPEncoder encoder = new NVPEncoder();
		NVPDecoder decoder = new NVPDecoder();

		try {

			caller = new NVPCallerServices();
			APIProfile profile = ProfileFactory.createSignatureAPIProfile();
			profile.setAPIUsername(servletContext.getInitParameter("PAYPAL_API_USERNAME"));
			profile.setAPIPassword(servletContext.getInitParameter("PAYPAL_API_PASSWORD"));
			profile.setSignature(servletContext.getInitParameter("PAYPAL_API_SIGNATURE"));
			profile.setEnvironment("live");
			profile.setSubject("");
			caller.setAPIProfile(profile);
			encoder.add("VERSION", "51.0");
			encoder.add("METHOD", "DoExpressCheckoutPayment");

			// Add request-specific fields to the request string.
			// Pass the token value by actual value returned in the
			// SetExpressCheckout.
			encoder.add("TOKEN", token);
			encoder.add("PAYERID", payerID);
			encoder.add("AMT", amount);
			encoder.add("PAYMENTACTION", "Sale");
			encoder.add("CURRENCYCODE", "USD");
			// Execute the API operation and obtain the response.
			String NVPRequest = encoder.encode();
			String NVPResponse = caller.call(NVPRequest);
			decoder.decode(NVPResponse);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return decoder.get("ACK");
	}

	public String getCheckoutCode(String token) {

		NVPEncoder encoder = new NVPEncoder();
		NVPDecoder decoder = new NVPDecoder();

		try {
			caller = new NVPCallerServices();
			APIProfile profile = ProfileFactory.createSignatureAPIProfile();

			profile.setAPIUsername(servletContext.getInitParameter("PAYPAL_API_USERNAME"));
			profile.setAPIPassword(servletContext.getInitParameter("PAYPAL_API_PASSWORD"));
			profile.setSignature(servletContext.getInitParameter("PAYPAL_API_SIGNATURE"));
			profile.setEnvironment("live");
			profile.setSubject("");
			caller.setAPIProfile(profile);
			encoder.add("VERSION", "51.0");
			encoder.add("METHOD", "GetExpressCheckoutDetails");

			// Add request-specific fields to the request string.
			// Pass the token value returned in SetExpressCheckout.
			encoder.add("TOKEN", token);

			// Execute the API operation and obtain the response.
			String NVPRequest = encoder.encode();
			String NVPResponse = caller.call(NVPRequest);
			decoder.decode(NVPResponse);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return decoder.get("ACK");
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}

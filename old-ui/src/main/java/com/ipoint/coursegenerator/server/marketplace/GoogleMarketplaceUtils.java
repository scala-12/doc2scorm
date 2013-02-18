package com.ipoint.coursegenerator.server.marketplace;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.ipoint.coursegenerator.server.authorization.GoogleAuthorizationUtils;
import com.ipoint.coursegenerator.shared.model.OrderPlan;

public class GoogleMarketplaceUtils {

	private static final String CUSTOMER_LICENSE_URL = "https://www.googleapis.com/appsmarket/v2/customerLicense/";

	public static String getCustomerLicense(String customerId) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(CUSTOMER_LICENSE_URL + GoogleAuthorizationUtils.APPLICATION_ID + "/" + customerId);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// connection.setRequestProperty("Content-Length", "" +
			// Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			// wr.writeBytes (urlParameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			ObjectMapper objectMapper = new ObjectMapper();
			List<CustomerLicense> license = objectMapper.readValue(is, new TypeReference<CustomerLicense>() {
			});
			return "";

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}

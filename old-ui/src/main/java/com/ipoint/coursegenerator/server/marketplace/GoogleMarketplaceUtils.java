package com.ipoint.coursegenerator.server.marketplace;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletContext;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.ServletContextAware;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.authn.oauth.OAuthParameters.OAuthType;
import com.google.gdata.client.authn.oauth.OAuthSigner;
import com.google.gdata.client.authn.oauth.TwoLeggedOAuthHelper;
import com.ipoint.coursegenerator.server.authorization.GoogleAuthorizationUtils;
import com.ipoint.coursegenerator.server.db.CourseGeneratorDAO;
import com.ipoint.coursegenerator.server.db.model.GoogleAppsDomain;

public class GoogleMarketplaceUtils implements ServletContextAware {

	private static final String APP_NAME = "ipoint-ilogos-course-generator-0.1";

	private static final String CUSTOMER_LICENSE_URL = "https://www.googleapis.com/appsmarket/v2/customerLicense/";

	private static final String LICENSE_NOTIFICATION_LIST_URL = "https://www.googleapis.com/appsmarket/v2/licenseNotification/";

	private static final String GOOGLE_USER_EMAIL_SCOPE = "https://apps-apis.google.com/a/feeds/user/#readonly";

	private boolean firstTimeCall = true;

	private static final int MARKETPLACE_TRIAL_DAY_COUNT = 3;
	
	private static final int GET_NOTIFICATIONS_INTERVAL = 300000;
	
	private ServletContext servletContext;
	
	public GoogleMarketplaceUtils() {
		firstTimeCall = true;
	}

	private InputStream getHttpURLConnectionInputStream(HttpURLConnection connection, String url) {
		InputStream result = null;
		try {
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/atom+xml");
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("GData-Version", "2.0");
			OAuthSigner signer = new OAuthHmacSha1Signer();
			GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
			oauthParameters.setOAuthConsumerKey(servletContext.getInitParameter("appsmarketplace_consumer_key"));
			oauthParameters.setOAuthConsumerSecret(servletContext.getInitParameter("appsmarketplace_consumer_secret"));
			oauthParameters.setOAuthType(OAuthType.TWO_LEGGED_OAUTH);
			oauthParameters.addCustomBaseParameter("oauth_version", "1.0");
			TwoLeggedOAuthHelper twoLeggedOAuthHelper = new TwoLeggedOAuthHelper(signer, oauthParameters);
			String authorizationHeader = twoLeggedOAuthHelper
					.getAuthorizationHeader(url, connection.getRequestMethod());
			connection.setRequestProperty("Authorization", authorizationHeader);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
			result = connection.getInputStream();
		} catch (OAuthException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public CustomerLicense getCustomerLicense(String customerId) {
		URL url;
		HttpURLConnection connection = null;
		CustomerLicense license = null;
		try {
			// Create connection
			url = new URL(CUSTOMER_LICENSE_URL + GoogleAuthorizationUtils.APPLICATION_ID + "/" + customerId);
			connection = (HttpURLConnection) url.openConnection();
			InputStream is = getHttpURLConnectionInputStream(connection, url.toString());
			if (is != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				license = objectMapper.readValue(is, new TypeReference<CustomerLicense>() {
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return license;
	}

	public NotificationList getLicenseNotifications(long timestamp, String startToken) {
		URL url;
		HttpURLConnection connection = null;
		NotificationList notificationList = null;
		try {
			String charset = "UTF-8";
			String query = "";
			if (timestamp != 0) {
				query = String.format("timestamp=%s", URLEncoder.encode(Long.toString(timestamp), charset));
			}
			if (startToken != null) {
				if (query.length() > 0) {
					query += "&";
				}
				query += String.format("start-token=%s", URLEncoder.encode(startToken, charset));
			}
			// Create connection
			url = new URL(LICENSE_NOTIFICATION_LIST_URL + GoogleAuthorizationUtils.APPLICATION_ID + (query.length() < 1 ? "" : "?" + query));

			connection = (HttpURLConnection) url.openConnection();
			InputStream is = getHttpURLConnectionInputStream(connection, url.toString());
			if (is != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				notificationList = objectMapper.readValue(is, new TypeReference<NotificationList>() {
				});
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return notificationList;
	}

	@Scheduled(fixedRate = GET_NOTIFICATIONS_INTERVAL)
	public void fetchLicenseNotifications() {
		long currentTime = System.currentTimeMillis() - GET_NOTIFICATIONS_INTERVAL;
		if (firstTimeCall) {
			currentTime = 0;
			firstTimeCall = false;
		}
		List<Notification> notifications = new ArrayList<Notification>();
		NotificationList list = getLicenseNotifications(currentTime, null);
		while (list != null && list.getNotifications() != null && list.getNotifications().size() > 0) {
			notifications.addAll(list.getNotifications());
			list = getLicenseNotifications(currentTime, list.getNextPageToken());
		}
		PersistenceManager pm = CourseGeneratorDAO.getPersistenceManager();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, MARKETPLACE_TRIAL_DAY_COUNT);
		for (Notification notification : notifications) {
			if (notification.getProvisions() != null && notification.getProvisions().size() > 0) {
				Query query = pm.newQuery(GoogleAppsDomain.class);
				query.setFilter("name == nameParam");
				query.declareParameters("String nameParam");
				@SuppressWarnings("unchecked")
				List<GoogleAppsDomain> domainList = (List<GoogleAppsDomain>) query
						.execute(notification.getCustomerId());
				GoogleAppsDomain domain = null;
				if (domainList != null && domainList.size() > 0) {
					domain = domainList.get(0);
					pm.refresh(domain);
				}
				if (domain == null) {
					domain = new GoogleAppsDomain(notification.getCustomerId());
				}
				for (MarketplaceEdition edition : notification.getProvisions()) {
					if (edition.getEditionId().equals(MarketplaceEdition.FREE_EDITION) && !domain.isTrialUsed()) {
						domain.setExpirationDate(calendar.getTime());
						domain.setTrialUsed(true);
					}
				}
				pm.makePersistent(domain);
			}
		}
		pm.flush();
		pm.close();
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}

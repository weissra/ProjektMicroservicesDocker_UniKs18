package uniks_project_microservice.purgerservice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uniks_project_microservice.baseservice.DefaultService;
import uniks_project_microservice.baseservice.ServiceProperty;
import uniks_project_microservice.purgerservice.model.EsResponse;
import uniks_project_microservice.purgerservice.model.Hit;
import uniks_project_microservice.util.MicroserviceHttpUtil;

/**
 * Microservice which periodically deletes all unresponsive services from the es
 * registry
 * 
 * @author Raphael Weiss
 *
 */
public class PurgerApp extends DefaultService {

	private static final long DELAY_MS = 1000;
	private static final long PERIOD_MS = 30000;

	public static void main(String[] args) {

		ServiceProperty serviceProperty = new ServiceProperty(args);
		serviceProperty.setFormat("null");
		serviceProperty.setName("purger");
		serviceProperty.setReturnType("null");
		serviceProperty.setSpeed(0);
		serviceProperty.setServiceType("purger");

		setServiceProperty(serviceProperty);

		PurgerHandler purgerHandler = new PurgerHandler(
				serviceProperty.getHostname(), serviceProperty.getName());

		// kill all unresponsive services all DELAY_MS milliseconds
		try {
			PurgerApp.startMicroService(purgerHandler, args, serviceProperty);
			System.out.println("TODO APP Connected");
			while (serviceProperty.getId() == null) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			ScheduledExecutorService executor = Executors
					.newScheduledThreadPool(1);
			executor.scheduleAtFixedRate(new ScheduledPurge(serviceProperty),
					DELAY_MS, PERIOD_MS, TimeUnit.MILLISECONDS);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

/**
 * Inner class which defines the Runnable that the Purger uses with a
 * ScheduledExecutorService to delete unresponsive Services
 * 
 * @author Raphael Weiss
 *
 */
final class ScheduledPurge implements Runnable {

	private ServiceProperty serviceProperty;

	public ScheduledPurge(ServiceProperty serviceProperty) {
		this.serviceProperty = serviceProperty;
	}

	private void findUnresponsiveServices(String jsonString) {
		Gson gson = new GsonBuilder().create();
		EsResponse esResponse = gson.fromJson(jsonString, EsResponse.class);
		HttpClient httpClient = HttpClientBuilder.create().build();
		System.out.println("TODO response: " + esResponse.getHits());
		for (Hit hit : esResponse.getHits().getHits()) {

			String urlString = hit.getSource().getUrlString();

			// legacy services
			if (urlString == null || urlString.equals("unreachable")) {
				killUnresponsiveServices(hit, esResponse, urlString);
				continue;
			}

			if ("localhost".equals(serviceProperty.getHostname())) {
				urlString = urlString.replaceFirst(
						hit.getSource().getHostname(),
						serviceProperty.getHostname());
			}
			urlString += "/id";

			HttpGet get = new HttpGet(urlString);
			ServiceProperty responseServiceProperty = null;
			try {
				HttpResponse response = httpClient.execute(get);
				String responseString = MicroserviceHttpUtil
						.readAll(response.getEntity().getContent());

				responseServiceProperty = new Gson().fromJson(responseString,
						ServiceProperty.class);

				String responsiveUrl = responseServiceProperty.getUrl();
				String responsiveId = responseServiceProperty.getId();
				// check if a service with different id but same URL exists
				for (Hit duplicate : esResponse.getHits().getHits()) {
					String duplicateUrl = duplicate.getSource().getUrlString();
					String duplicateId = duplicate.getId();

					if (!duplicateId.equals(responsiveId)
							&& responsiveUrl.equals(duplicateUrl)) {
						// System.out.println("TODO found duplicate: org "
						// + responseServiceProperty.getId() + " | "
						// + responseServiceProperty.getUrl() + " || dup "
						// + duplicate.getId() + " | "
						// + duplicate.getSource().getUrlString());
						killUnresponsiveServices(duplicate, esResponse,
								urlString);
					}
				}

			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Purger failed to connect to " + urlString
						+ " | " + hit.getId());

				killUnresponsiveServices(hit, esResponse, urlString);
			}

			if (responseServiceProperty != null) {
				System.out.println("MS is alive = " + responseServiceProperty);
			}
		}
	}

	private void killUnresponsiveServices(Hit hit, EsResponse esResponse,
			String urlString) {

		StringBuilder result = new StringBuilder();
		try {
			serviceProperty.getBaseHandler();
			String urlStringToKill = serviceProperty.getEsregistryUrl()
					+ "/microservices/microservice/" + hit.getId();
			URL urlEsRegistry = new URL(urlStringToKill);
			HttpURLConnection urlConnection = (HttpURLConnection) urlEsRegistry
					.openConnection();
			urlConnection.setRequestMethod("DELETE");
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConnection.connect();

			BufferedReader rd = null;
			try {
				rd = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));
			} catch (FileNotFoundException e) {
				System.out.println("esregistry unresponsive for DELETE");
			}

			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			System.out.println("DELETE Response: " + result.toString());
			urlConnection.disconnect();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println("TODO trying to connect with purger to es");
		try {
			StringBuilder result = new StringBuilder();
			serviceProperty.getBaseHandler();
			URL url = new URL(serviceProperty.getEsregistryUrl()
					+ "/_all/_search?q=_type:microservice&size=100");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			conn.disconnect();
			System.out.println("TODO RESPONSE :" + result.toString());
			findUnresponsiveServices(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

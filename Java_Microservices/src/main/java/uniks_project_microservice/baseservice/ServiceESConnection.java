package uniks_project_microservice.baseservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uniks_project_microservice.util.MicroserviceHttpUtil;

/**
 * Handles the connection to the elastic search registry. Tries establishing a
 * connection and register on the esregistry
 * 
 * @author Raphael Weiss
 *
 */
public class ServiceESConnection {

	// how long between each connection attempt
	private static final int CON_RETRY_WAIT_MS = 5000;

	// how long after startup for first connection attempt
	private static final int CON_STARTUP_DELAY_MS = 15000;

	// service information
	private ServiceProperty serviceProperty;

	// es registry url
	private String esRegistryUrl;

	// this services hostname
	private String hostname;

	private boolean isConnected = false;

	/**
	 * The esRegistryHandle and hostname are both required to establish a
	 * connection to the esregistry
	 * 
	 * @param esRegistryUrl
	 * @param hostname
	 */
	public ServiceESConnection(String esRegistryUrl, String hostname) {
		this.esRegistryUrl = esRegistryUrl;
		this.hostname = hostname;
	}

	/**
	 * Connect to Elasticsearch and register this service
	 * 
	 * @param baseServiceProperty
	 */
	public void registerOnES(ServiceProperty baseServiceProperty) {

		// check if service is stared in docker container or outside
		if (hostname != null && hostname.equals("localhost")) {
			/*
			 * if started outside docker environment the addressspace is
			 * different outside => localhost:8000 addressing needed inside =>
			 * hostname(e.g. auth:8000) addressing needed
			 */
			esRegistryUrl = hostname + esRegistryUrl.substring(
					esRegistryUrl.lastIndexOf(":"), esRegistryUrl.length());
		}

		// check if esregistry is available and viable
		this.isConnected = checkESConnection(esRegistryUrl);

		serviceProperty = baseServiceProperty;

		try {

			// send a http post to esregistry to register this service
			URL url = new URL(
					"http://" + esRegistryUrl + "/microservices/microservice/");

			HttpClient httpClient = HttpClientBuilder.create().build();

			HttpPost post = new HttpPost(url.toURI());

			StringEntity postingString = new StringEntity(new Gson().toJson(
					baseServiceProperty, baseServiceProperty.getClass()));

			post.setEntity(postingString);

			post.setHeader("Content-type", "application/json");

			// received a http response containing the id for this service
			HttpResponse response = httpClient.execute(post);

			// save id
			parseId(response);

			// registering failed for any reason
		} catch (Exception e) {

			e.printStackTrace();

			System.out
					.println("Couldnt register microservice on ElasticSearch | "
							+ serviceProperty.getName() + " | trying again in "
							+ CON_STARTUP_DELAY_MS + " ms");

			// retry registering with a specified delay
			Executors.newSingleThreadScheduledExecutor().schedule(
					() -> registerOnES(serviceProperty), CON_STARTUP_DELAY_MS,
					TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Checks online status of esregistry. Tries to continuously establishing a
	 * connection. Only exits if a connection was established once.
	 * 
	 * @param esRegistryHandle
	 * @return
	 */
	private boolean checkESConnection(String esRegistryHandle) {

		try {

			// open a http connection to esregistry
			URL urlToPost = new URL("http://" + esRegistryHandle);

			HttpURLConnection urlConnection = (HttpURLConnection) urlToPost
					.openConnection();

			urlConnection.connect();

			urlConnection.disconnect();

			// successfully connected
			System.out.println("ES registry is online");

		} catch (Exception e) {

			// failed to connect
			System.out.println(
					"Failed to open connection to ES registry | trying again in "
							+ CON_RETRY_WAIT_MS + " ms");

			// TODO fix shit code -> need scheduled executor
			try {

				// wait
				Thread.sleep(CON_RETRY_WAIT_MS);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			// retry connecting
			checkESConnection(esRegistryHandle);
		}
		return true;
	}

	/**
	 * Parses the Response Object and returns a Json object
	 * 
	 * @param response
	 * @return
	 */
	private JsonObject parseJsonResponse(HttpResponse response) {

		BufferedReader br = null;
		try {
			// read response content
			br = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

		} catch (UnsupportedOperationException | IOException e) {
			e.printStackTrace();
		}

		// convert to String
		String readString = MicroserviceHttpUtil.readAll(br).toString();
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// convert string to json and return
		return new JsonParser().parse(readString).getAsJsonObject();
	}

	/**
	 * Parses the http response and reads/saves the contained id
	 * 
	 * @param response
	 */
	private void parseId(HttpResponse response) {

		// get content as json
		JsonObject jsonResponse = parseJsonResponse(response);

		// get the id value
		String id = jsonResponse.get("_id").getAsString();

		// check if successfully received id
		if (id != null) {
			serviceProperty.setId(id);
			System.out.println("Received ID: " + id
					+ " -> Successfully registered microservice on ElasticSearch | "
					+ serviceProperty.getName());
		} else {
			System.out.println(
					"Failure to receive ID | " + serviceProperty.getName());
		}
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

}

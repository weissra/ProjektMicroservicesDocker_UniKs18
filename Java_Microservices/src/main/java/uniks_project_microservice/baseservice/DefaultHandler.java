package uniks_project_microservice.baseservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import uniks_project_microservice.util.MicroserviceHttpUtil;

/**
 * Microservice handler for Http messages received through sun.httpserver
 * 
 * @author Raphael Weiss
 *
 */
public class DefaultHandler implements HttpHandler, ProcessMessage {
	private static String hostname;
	private static String serviceName;

	public DefaultHandler(String hostname, String serviceName) {
		DefaultHandler.serviceName = serviceName;
		DefaultHandler.hostname = hostname;
	}

	/**
	 * Handles received http messages
	 * 
	 * @param HttpExchange
	 *            exchange
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {

		// adress of the sender
		String remoteAdr = exchange.getRemoteAddress().toString();

		System.out.println(
				serviceName + " service-> recieved msg from: " + remoteAdr);

		try {
			// http request method GET/POST/...
			String requestMethod = exchange.getRequestMethod();

			// handle POST
			if (requestMethod.equalsIgnoreCase("POST")) {

				Headers responseHeaders = exchange.getResponseHeaders();

				responseHeaders.set("Content-Type", "application/json");

				exchange.sendResponseHeaders(200, 0);

				BufferedReader in = new BufferedReader(
						new InputStreamReader(exchange.getRequestBody()));

				// calls specific implementation of processMessage for each
				// service
				String resp = processMessage(MicroserviceHttpUtil.readAll(in));

				OutputStream responseBody = exchange.getResponseBody();

				responseBody.write(resp.toString().getBytes());

				responseBody.close();

				// handle http get with url /id
			} else if (requestMethod.equalsIgnoreCase("GET")
					&& exchange.getRequestURI().toString().endsWith("/id")) {

				Headers responseHeaders = exchange.getResponseHeaders();

				// sending back json
				responseHeaders.set("Content-Type", "application/json");

				exchange.sendResponseHeaders(200, 0);

				BufferedReader in = new BufferedReader(
						new InputStreamReader(exchange.getRequestBody()));

				MicroserviceHttpUtil.readAll(in);

				OutputStream responseBody = exchange.getResponseBody();

				// send the service property for this service back as json
				// string
				responseBody.write(new Gson()
						.toJson(DefaultService.getServiceProperty(),
								DefaultService.getServiceProperty().getClass())
						.getBytes());
				responseBody.close();

				// handle general http get requests
			} else if (requestMethod.equalsIgnoreCase("GET")) {

				Headers responseHeaders = exchange.getResponseHeaders();

				responseHeaders.set("Content-Type", "application/json");

				exchange.sendResponseHeaders(200, 0);

				BufferedReader in = new BufferedReader(
						new InputStreamReader(exchange.getRequestBody()));

				// calls specific implementation of processMessage for each
				// service
				String resp = processMessage(MicroserviceHttpUtil.readAll(in));

				OutputStream responseBody = exchange.getResponseBody();

				// send back processed answer
				responseBody.write(resp.getBytes());

				responseBody.close();

			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldnt handle message: " + exchange);
		}
	}

	@Override
	public String processMessage(String message) {
		return message;
	}

	public static String getHostname() {
		return hostname;
	}

	public static void setHostname(String hostname) {
		DefaultHandler.hostname = hostname;
	}

}

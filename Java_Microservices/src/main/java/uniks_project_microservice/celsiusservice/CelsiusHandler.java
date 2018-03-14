package uniks_project_microservice.celsiusservice;

import java.text.DecimalFormat;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import uniks_project_microservice.baseservice.DefaultHandler;

public class CelsiusHandler extends DefaultHandler {

	public CelsiusHandler(String hostname, String serviceName) {
		super(hostname, serviceName);
	}

	@Override
	public String processMessage(String message) {
		int tempInt = new Random().nextInt(80) - 30;
		int temp = new Random().nextInt(9);
		double tempC = tempInt + (double) temp / 100;

		try {
			return new JSONObject()
					.put("temp", new DecimalFormat("00.00").format(tempC))
					.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return message;
	}
}

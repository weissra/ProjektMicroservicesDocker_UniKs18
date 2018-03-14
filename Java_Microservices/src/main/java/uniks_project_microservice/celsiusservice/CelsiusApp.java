package uniks_project_microservice.celsiusservice;

import java.io.IOException;

import uniks_project_microservice.authenticationservice.AuthenticationApp;
import uniks_project_microservice.baseservice.DefaultService;
import uniks_project_microservice.baseservice.ServiceProperty;

public class CelsiusApp extends DefaultService {

	public static void main(String[] args) {

		ServiceProperty serviceProperty = new ServiceProperty(args);
		serviceProperty.setFormat("Float");
		serviceProperty.setName("CelsiusService");
		serviceProperty.setReturnType("Float");
		serviceProperty.setSpeed(100);
		serviceProperty.setServiceType("temperature");

		setServiceProperty(serviceProperty);

		CelsiusHandler handler = new CelsiusHandler(
				serviceProperty.getHostname(), serviceProperty.getName());

		try {
			AuthenticationApp.startMicroService(handler, args, serviceProperty);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

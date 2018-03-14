package uniks_project_microservice.authenticationservice;

import java.io.IOException;

import uniks_project_microservice.baseservice.DefaultService;
import uniks_project_microservice.baseservice.ServiceProperty;

public class AuthenticationApp extends DefaultService {

	public static void main(String[] args) {

		ServiceProperty serviceProperty = new ServiceProperty(args);
		serviceProperty.setFormat("String");
		serviceProperty.setName("Authentication");
		serviceProperty.setReturnType("String");
		serviceProperty.setSpeed(1000);
		serviceProperty.setServiceType("authentication");

		setServiceProperty(serviceProperty);

		AuthenticationHandler authenticationHandler = new AuthenticationHandler(
				serviceProperty.getHostname(), serviceProperty.getName());

		try {
			startMicroService(authenticationHandler, args, serviceProperty);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//TODO fix
		authenticationHandler.createDatabase();
		authenticationHandler.createTable();
		authenticationHandler.insertData();
	}
}

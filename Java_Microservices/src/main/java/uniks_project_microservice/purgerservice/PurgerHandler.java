package uniks_project_microservice.purgerservice;

import uniks_project_microservice.baseservice.DefaultHandler;

public class PurgerHandler extends DefaultHandler {

	public PurgerHandler(String hostname, String serviceName) {
		super(hostname, serviceName);

	}

	@Override
	public String processMessage(String message) {
		System.out.println("auth process message");

		return message;
	}

}

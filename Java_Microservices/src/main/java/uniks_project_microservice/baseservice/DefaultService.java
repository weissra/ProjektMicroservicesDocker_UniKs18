package uniks_project_microservice.baseservice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

/**
 * Basis service class which contains the all functionality to start the
 * service. It starts a http server on the in the launch argument specified
 * port. Also links a http-handler to process received requests. Connects to the
 * elasitc search registry with the specified url in the launch arguments.
 * Receives a id given by the esregistry and saves it.
 * 
 * @author raphael weiss
 *
 */
public class DefaultService {

	// contains basic information about this service
	private static ServiceProperty serviceProperty;
	// connection the esregistry
	private static ServiceESConnection serviceESConnection;

	/**
	 * Starts the microservice with a http-server and connects the service to
	 * the esregistry
	 * 
	 * @param httpHandler
	 * @param args
	 * @param serviceName
	 * @param baseServiceProperty
	 * @throws IOException
	 */
	public static void startMicroService(DefaultHandler httpHandler,
			String[] args, ServiceProperty baseServiceProperty)
			throws IOException {

		// create default-properties if unset/empty
		if (baseServiceProperty == null) {

			baseServiceProperty = new ServiceProperty();
			baseServiceProperty.setFormat("empty");
			baseServiceProperty.setName("TestService");
			baseServiceProperty.setReturnType("Double");
			baseServiceProperty.setSpeed(5000);
			baseServiceProperty.setServiceType("EMPTY");
			baseServiceProperty.setHostname("null");
			baseServiceProperty.setPort(0);
			baseServiceProperty
					.setUrl("http://" + baseServiceProperty.getHostname() + ":"
							+ baseServiceProperty.getPort());
			DefaultService.serviceProperty = baseServiceProperty;
		}
		baseServiceProperty.setBaseHandler(httpHandler);

		// create http server to receive requests
		if (serviceProperty.getPort() > 0) {

			// specify port
			InetSocketAddress addr = new InetSocketAddress(
					serviceProperty.getPort());

			// create server
			HttpServer server = HttpServer.create(addr, 0);

			// save server
			baseServiceProperty.setHttpServer(server);

			// specify with urls the http server responds/acknowledges
			server.createContext("/", httpHandler);

			server.createContext("/id", httpHandler);

			server.setExecutor(Executors.newSingleThreadExecutor());

			server.start();

			System.out.println(serviceProperty.getName()
					+ " service is listening on: " + serviceProperty.getPort());
		}

		// register on ES registry
		final ServiceProperty exServiceProperty = baseServiceProperty;

		// create executor for asynchronus connecting to esregistry
		ExecutorService connectEsRegistryExecutor = Executors
				.newSingleThreadExecutor();

		connectEsRegistryExecutor.submit(new Runnable() {
			@Override
			public void run() {

				serviceESConnection = new ServiceESConnection(
						serviceProperty.getEsregistryUrl(),
						serviceProperty.getHostname());

				// try establishing a connection to esregistr and registers on
				// esregistry
				serviceESConnection.registerOnES(exServiceProperty);
			}
		});
	}

	public static ServiceProperty getServiceProperty() {
		return serviceProperty;
	}

	public static void setServiceProperty(ServiceProperty baseServiceProperty) {
		DefaultService.serviceProperty = baseServiceProperty;
	}

	public static ServiceESConnection getServiceESConnection() {
		return serviceESConnection;
	}

	public static void setServiceESConnection(
			ServiceESConnection serviceESConnection) {
		DefaultService.serviceESConnection = serviceESConnection;
	}
}
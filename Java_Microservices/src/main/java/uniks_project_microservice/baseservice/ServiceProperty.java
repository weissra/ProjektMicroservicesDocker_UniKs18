package uniks_project_microservice.baseservice;

import com.sun.net.httpserver.HttpServer;

/**
 * Information for each service
 * 
 * @author Raphael Weiss
 *
 */
public class ServiceProperty {

	private transient HttpServer httpServer;
	private transient DefaultHandler baseHandler;
	private transient String esregistryUrl;
	private String id;
	private String name;
	private String serviceType;
	private String format;
	private String returnType;
	private String urlString;
	private String hostname;
	private long speed;
	private int port;

	/**
	 * Reads the launch arguments and checks if all needed fields are set
	 * 
	 * @param args
	 */
	public ServiceProperty(String[] args) {

		if (args.length >= 1) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println(
						"1st launch argument in wrong format! Not a number");
			}
		}
		if (args.length >= 3) {
			hostname = args[1];
			esregistryUrl = args[2];
		} else {
			hostname = "docker";
			esregistryUrl = args[1];
		}

		urlString = "http://" + hostname + ":" + port;

	}

	public ServiceProperty() {
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public long getSpeed() {
		return speed;
	}
	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public HttpServer getHttpServer() {
		return httpServer;
	}
	public void setHttpServer(HttpServer httpServer) {
		this.httpServer = httpServer;
	}
	public DefaultHandler getBaseHandler() {
		return baseHandler;
	}
	public void setBaseHandler(DefaultHandler baseHandler) {
		this.baseHandler = baseHandler;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public int getPort() {
		return port;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUrl() {
		return urlString;
	}
	public void setUrl(String url) {
		this.urlString = url;
	}

	public String getEsregistryUrl() {
		return esregistryUrl;
	}

	public void setEsregistryUrl(String esregistryUrl) {
		this.esregistryUrl = esregistryUrl;
	}

	@Override
	public String toString() {
		return "ServiceProperty [esregistryUrl=" + esregistryUrl + ", id=" + id
				+ ", name=" + name + ", serviceType=" + serviceType
				+ ", format=" + format + ", returnType=" + returnType
				+ ", urlString=" + urlString + ", hostname=" + hostname
				+ ", speed=" + speed + ", port=" + port + "]";
	}

}

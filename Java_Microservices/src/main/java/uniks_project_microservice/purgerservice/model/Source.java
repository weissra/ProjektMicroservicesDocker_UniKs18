
package uniks_project_microservice.purgerservice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Source {

	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("type")
	@Expose
	private String serviceType;
	@SerializedName("format")
	@Expose
	private String format;
	@SerializedName("returnType")
	@Expose
	private String returnType;
	@SerializedName("urlString")
	@Expose
	private String urlString;
	@SerializedName("hostname")
	@Expose
	private String hostname;
	@SerializedName("speed")
	@Expose
	private int speed;
	@SerializedName("port")
	@Expose
	private int port;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getserviceType() {
		return serviceType;
	}

	public void setserviceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getUrlString() {
		return urlString;
	}

	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}

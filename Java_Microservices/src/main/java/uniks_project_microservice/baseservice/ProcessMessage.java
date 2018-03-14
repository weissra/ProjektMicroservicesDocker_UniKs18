package uniks_project_microservice.baseservice;

/**
 * Interface for processing received http requests. Each service needs to have
 * its own handler which implements this interface to handle messages.
 * 
 * @author Raphael Weiss
 *
 */
public interface ProcessMessage {
	public String processMessage(String message);
}

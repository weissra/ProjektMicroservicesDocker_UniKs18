
package uniks_project_microservice.purgerservice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shards {

	@SerializedName("total")
	@Expose
	private int total;
	@SerializedName("successful")
	@Expose
	private int successful;
	@SerializedName("skipped")
	@Expose
	private int skipped;
	@SerializedName("failed")
	@Expose
	private int failed;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getSuccessful() {
		return successful;
	}

	public void setSuccessful(int successful) {
		this.successful = successful;
	}

	public int getSkipped() {
		return skipped;
	}

	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

}

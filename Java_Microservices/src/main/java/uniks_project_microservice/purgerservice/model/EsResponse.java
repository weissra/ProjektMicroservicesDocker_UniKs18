
package uniks_project_microservice.purgerservice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EsResponse {

	@SerializedName("took")
	@Expose
	private int took;
	@SerializedName("timed_out")
	@Expose
	private boolean timedOut;
	@SerializedName("_shards")
	@Expose
	private Shards shards;
	@SerializedName("hits")
	@Expose
	private Hits hits;

	public int getTook() {
		return took;
	}

	public void setTook(int took) {
		this.took = took;
	}

	public boolean isTimedOut() {
		return timedOut;
	}

	public void setTimedOut(boolean timedOut) {
		this.timedOut = timedOut;
	}

	public Shards getShards() {
		return shards;
	}

	public void setShards(Shards shards) {
		this.shards = shards;
	}

	public Hits getHits() {
		return hits;
	}

	public void setHits(Hits hits) {
		this.hits = hits;
	}

}


package uniks_project_microservice.purgerservice.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hits {

	@SerializedName("total")
	@Expose
	private int total;
	@SerializedName("max_score")
	@Expose
	private double maxScore;
	@SerializedName("hits")
	@Expose
	private List<Hit> hits = null;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(double maxScore) {
		this.maxScore = maxScore;
	}

	public List<Hit> getHits() {
		return hits;
	}

	public void setHits(List<Hit> hits) {
		this.hits = hits;
	}

}

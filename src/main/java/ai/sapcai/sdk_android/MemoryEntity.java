package ai.sapcai.sdk_android;

import org.json.JSONObject;

public class MemoryEntity {

	private String name;
	private String raw;
	private String value;
	private double confidence;

	public MemoryEntity(String entityName, JSONObject obj) {
		this.name = entityName;
		if(obj != null){
			this.raw = obj.optString("raw");
			this.value = obj.optString("value");
			this.confidence = obj.optDouble("confidence");
		}
	}

	public MemoryEntity(String raw, String value, double confidence){
		this.raw = raw;
		this.value = value;
		this.confidence = confidence;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}



}

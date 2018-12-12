package ai.sapcai.sdk_android;

import org.json.JSONObject;

public class Intent {
    private String name;
    private double confidence;

    private Intent(String name, double confidence) {
        this.name = name;
        this.confidence = confidence;
    }

    Intent(JSONObject obj) {
        this(obj.optString("slug"), obj.optDouble("confidence"));
    }

    public double getConfidence() {
        return confidence;
    }

    public String getName() {
        return name;
    }
}

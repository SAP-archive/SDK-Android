package ai.recast.sdk_android;

import org.json.JSONObject;

/**
 * The Entity class represents an entity found by Recast.AI in the user input.
 *
 * @author Francois Triquet
 * @version 2.0.0
 * @since 2016-05-17
 *
 */
public class Entity {

    private String name;
    private double confidence;
    private JSONObject data;

    Entity (String name, JSONObject o) {
        this.data = o;
        this.name = name;
        if(o != null){
        	this.confidence = o.optDouble("confidence");
        }
    }

    /**
     * Returns the name of the entity
     * @return The name of the entity
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the confidence of the entity
     * @return The confidence of the entity
     */
    public double getConfidence() {
        return this.confidence;
    }


    /**
     * Returns the fields described by the parameter if it exists or null otherwise
     * @param name The name of the field
     * @return The value of the field or null
     */
}

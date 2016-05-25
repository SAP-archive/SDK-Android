package ai.recast.sdk_android;

import org.json.JSONObject;

/**
 * The Entity class represents an entity found by Recast.AI in the user input.
 *
 * @author Francois Triquet
 * @version 1.0.0
 * @since 2016-05-17
 *
 */
public class Entity {

    private String name;
    private JSONObject data;


    Entity (String name, JSONObject o) {
        this.data = o;
        this.name = name;
    }

    /**
     * Returns the name of the entity
     * @return The name of the entity
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the raw value of the entity, as it was in the output
     * Same as getField("raw")
     * @return The raw value of the entity
     */
    public String getRaw() {
        return (String)this.getField("raw");
    }


    /**
     * Returns the fields described by the parameter if it exists or null otherwise
     * @param name The name of the field
     * @return The value of the field or null
     */
    public Object getField(String name) {
        return this.data.opt(name);
    }

    public String toString() {
        return this.getName();
    }

}

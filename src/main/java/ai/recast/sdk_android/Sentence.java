package ai.recast.sdk_android;


import org.json.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The Sentence class handles a recast sentence and provides utility methods
 * for accessing the sentence properties
 *
 * @author Francois Triquet
 * @version 1.0.0
 * @since 2016-05-17
 *
 */
public class Sentence {

    public static final String	TYPE_ASSERT			= "assert";
    public static final String	TYPE_COMMAND		= "command";
    public static final String	TYPE_YES_NO			= "command";
    public static final String	TYPE_WHAT			= "what";
    public static final String	TYPE_WHERE			= "where";
    public static final String	TYPE_WHO			= "who";
    public static final String	TYPE_WHEN			= "when";
    public static final String	TYPE_HOW			= "how";
    public static final String	POLARITY_POSITIVE	= "positive";
    public static final String	POLARITY_NEGATIVE	= "negative";

    private String				source;
    private String				type;
    private String				action;
    private String				agent;
    private String				polarity;
    private Map<String,Entity[]>	entities;

    Sentence (JSONObject data) {
        JSONObject resultEntities;

        this.source = data.optString("source");
        this.type = data.optString("type");
        this.polarity = data.optString("polarity");
        this.action = data.optString("action");
        this.agent = data.optString("agent");
        resultEntities = data.optJSONObject("entities");
        this.entities = new HashMap<String,Entity[]>();
        if (resultEntities.length() != 0) {
            Iterator<String> it = resultEntities.keys();

            while (it.hasNext()) {
                String entityName = it.next();
                JSONArray entity = resultEntities.optJSONArray(entityName);
                Entity[] values = new Entity[entity.length()];
                for (int i = 0; i < values.length; i++) {
                    values[i] = new Entity(entityName, entity.optJSONObject(i));
                }
                this.entities.put(entityName, values);
            }
        }
    }

    /**
     * Returns the source of the sentence
     * @return The source of the sentence
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Returns the type of the sentence
     * @return The type of the sentence
     * @see Sentence
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the action of the sentence or null if there is no action
     * @return The action of the sentence
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Returns the agent of the sentence if there is one, or null otherwise
     * @return The agent of the sentence
     */
    public String getAgent() {
        return this.agent;
    }

    /**
     * Returns the polarity of the sentence,
     * this may be Sentence.POLARITY_POSITIVE or Sentence.POLARITY_NEGATIVE
     * @return The polarity of the sentence
     */
    public String getPolarity() {
        return this.polarity;
    }

    /**
     * Returns all the entities detected by Recast.AI in the sentence as a map
     * @return A map with entities names as keys and arrays of entities as values
     * @see Entity
     */
    public Map<String,Entity[]> getEntities() {
        return this.entities;
    }

    /**
     * Returns all the entities matching -name-
     * @param name The name of the entity
     * @return An array of Entity
     * @see Entity
     */
    public Entity[] getEntities(String name) {
        return this.entities.get(name);
    }

    /**
     * Returns the first entity matching -name-
     * @param name The name of the entity
     * @return The first entity matching -name-
     * @see Entity
     */
    public Entity getEntity(String name) {
        Entity[] ents = this.entities.get(name);
        if (ents == null) {
            return null;
        }
        return ents[0];
    }
}
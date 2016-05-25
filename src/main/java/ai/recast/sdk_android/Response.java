package ai.recast.sdk_android;

import org.json.*;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The Response class handles responses from Recast.AI API
 *
 * @author Francois Triquet
 * @version 1.0.0
 * @since 2016-05-17
 *
 */
public class Response {
    private String		source;
    private String[]	intents;
    private Sentence[]	sentences;
    private String		version;
    private String		timestamp;
    private	String		raw;
    private int			status;

    Response(String json) throws RecastException {
        JSONArray	resultIntents = null;
        JSONArray	resultSentences = null;
        JSONObject	result;
        this.raw = json;

        try {
            result = new JSONObject(json).getJSONObject("results");

            this.source = result.getString("source");
            this.version = result.getString("version");
            this.timestamp = result.getString("timestamp");
            this.status = result.getInt("status");

            resultIntents = result.getJSONArray("intents");
            resultSentences = result.getJSONArray("sentences");

            this.sentences = new Sentence[resultSentences.length()];
            for (int i = 0; i < this.sentences.length; ++i) {
                this.sentences[i] = new Sentence(resultSentences.getJSONObject(i));
            }

            this.intents = new String[resultIntents.length()];
            for (int i = 0; i < this.intents.length; ++i) {
                this.intents[i] = resultIntents.getString(i);
            }
        } catch (Exception e) {
            throw new RecastException("Invalid JSON", e);
        }
    }

    /**
     * Returns the user input
     * @return The input sent to Recast
     */
    public String getSource() {
        return this.source;
    }


    /**
     * Returns the intent that matches with the input or null otherwise
     * @return The matched intent
     */
    public String getIntent() {
        if (this.intents.length > 0) {
            return this.intents[0];
        }
        return null;
    }

    /**
     * Returns an array of all the intents, ordered by propability
     * @return All matched intents
     */
    public String[] getIntents() {
        return this.intents;
    }

    /**
     * Returns the status of the request
     * @return The request status
     */
    public int	getStatus() {
        return this.status;
    }

    /**
     * Returns the version of the JSON
     * @return The Recast version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Returns all sentences
     * @return An array of Sentences
     * @see Sentence
     */
    public Sentence[] getSentences() {
        return this.sentences;
    }

    /**
     * Returns the first sentence
     * @return The first sentence
     * @see Sentence
     */
    public Sentence getSentence() {
        return this.sentences[0];
    }

    /**
     * Returns the timestamp of the request
     * @return The timestampt of the request
     */
    public String getRequest() {
        return this.timestamp;
    }

    /**
     * Returns the first entity that matches with the parameter if ther is one, or null otherwise
     * @param name The name of the entity
     * @return The entity matched or null
     * @see Entity
     */
    public Entity getEntity(String name) {
        Entity e;

        for (Sentence s : this.sentences) {
            e = s.getEntity(name);
            if (e != null) {
                return e;
            }
        }
        return null;
    }

    /**
     * Returns an array of all the entities in the input if there are some, or null otherwise
     * @param name The name of the Entity
     * @return An array of entities
     * @see Entity
     */
    public Entity[] getEntities(String name) {
        Map<String, Entity[]> tmp  = this.getEntities();
        return tmp.get(name);
    }

    /**
     * Returns an Map of all entities with names as keys and arrays of entities as values
     * @return A map of all entities
     * @see Entity
     */
    public Map<String, Entity[]> getEntities() {
        Map<String,Entity[]> res = new HashMap<String,Entity[]>();
        Map<String,LinkedList<Entity>> tmp = new HashMap<String,LinkedList<Entity>>();
        for (Sentence s : this.sentences) {
            Map<String, Entity[]> sentenceEntities = s.getEntities();
            for (Map.Entry<String, Entity[]> entry : sentenceEntities.entrySet()) {
                if (tmp.get(entry.getKey()) == null) {
                    tmp.put(entry.getKey(), new LinkedList<Entity>());
                }
                Entity[] ent = entry.getValue();
                for (Entity e : ent) {
                    tmp.get(entry.getKey()).add(e);
                }
            }

        }
        for (Map.Entry<String, LinkedList<Entity>> entry : tmp.entrySet()) {
            String name = entry.getKey();
            res.put(name, entry.getValue().toArray(new Entity[0]));
        }
        return res;
    }
}
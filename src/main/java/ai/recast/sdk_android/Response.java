package ai.recast.sdk_android;

import org.json.*;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * The Response class handles responses from Recast.AI API
 *
 * @author Francois Triquet
 * @version 2.0.0
 * @since 2016-05-17
 *
 */
public class Response {
    private String		source;
    private Intent[]	intents;
    private Map<String, Entity[]> entities;
    private String		version;
    private String		timestamp;
    private	String		raw;
    private int			status;
	private String		language;
    private String      uuid;


    private String sentiment;
    private String act;
    private String type;
    private String subtype;

    public static final String ACT_ASSERT = "assert";
    public static final String ACT_COMMAND = "command";
    public static final String ACT_WH_QUERY = "wh-query";
    public static final String ACT_YN_QUERY = "yn-query";

    public static final String TYPE_ABBREVIATION = "abbr:";
    public static final String TYPE_ENTITY = "enty:";
    public static final String TYPE_DESCRIPTION = "desc:";
    public static final String TYPE_HUMAN = "hum:";
    public static final String TYPE_NUMBER = "loc:";
    public static final String TYPE_LOCATION = "num:";

    public static final String SENTIMENT_POSITIVE = "positive";
    public static final String SENTIMENT_VERY_POSITIVE = "vpositive";
    public static final String SENTIMENT_NEGATIVE = "negative";
    public static final String SENTIMENT_VERY_NEGATIVE = "vnegative";
    public static final String SENTIMENT_NEUTRAL = "neutral";


    Response(String json) throws RecastException {
        JSONArray	resultIntents = null;
        JSONObject	result;
        Pattern typePattern;
        this.raw = json;

        try {
            result = new JSONObject(json).getJSONObject("results");

            this.source = result.getString("source");
            this.version = result.getString("version");
            this.timestamp = result.getString("timestamp");
            this.status = result.getInt("status");
			this.language = result.getString("language");
            this.type = result.getString("type");
            this.act = result.getString("act");
            this.sentiment = result.getString("sentiment");
            this.uuid = result.getString("uuid");

            JSONObject resultEntities = result.optJSONObject("entities");
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

            resultIntents = result.getJSONArray("intents");
            this.intents = new Intent[resultIntents.length()];
            for (int i = 0; i < this.intents.length; ++i) {
                this.intents[i] = new Intent(resultIntents.getJSONObject(i));
            }

            typePattern = Pattern.compile("(\\w+:).*");
            Matcher m = typePattern.matcher(this.type);
            if (m.find()) {
                this.subtype = m.group(1);
            } else {
                this.subtype = this.type;
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
     * Returns the uuid of the response
     * @return The uuid of the response
     */
    public String getUuid() { return this.uuid; }

    /**
     * Returns the intent that matches with the input or null otherwise
     * @return The matched intent
     */
    public Intent getIntent() {
        if (this.intents.length > 0) {
            return this.intents[0];
        }
        return null;
    }

	public String getLanguage() {
		return this.language;
	}

    /**
     * Returns an array of all the intents, ordered by propability
     * @return All matched intents
     */
    public Intent[] getIntents() {
        return this.intents;
    }

    /**
     * Returns the json received from Recast
     * @return The raw json string
     */
    public String getRaw() {
        return this.raw;
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
     * Returns the timestamp of the request
     * @return The timestampt of the request
     */
    public String getTimestamp() {
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

        Entity[] ents = this.entities.get(name);
        if (ents == null || ents.length == 0) {
            return null;
        }
        return ents[0];
    }

    /**
     * Returns an array of all the entities in the input if there are some, or null otherwise
     * @param name The name of the Entity
     * @return An array of entities
     * @see Entity
     */
    public Entity[] getEntities(String name) {
        return this.entities.get(name);
    }

    public String getAct() {
        return act;
    }

    public String getType() {
        return type;
    }

    public String getSentiment() {
        return sentiment;
    }

    public boolean isCommand() {
        return this.act.equals(ACT_COMMAND);
    }

    public boolean isAssert() {
        return this.act.equals(ACT_ASSERT);
    }

    public boolean isWhQuery() {
        return this.act.equals(ACT_WH_QUERY);
    }

    public boolean isYesNoQuery() {
        return this.act.equals(ACT_YN_QUERY);
    }

    public boolean isPositive() { return this.sentiment.equals(SENTIMENT_POSITIVE); }

    public boolean isVeryPositive() {
        return this.sentiment.equals(SENTIMENT_VERY_POSITIVE);
    }

    public boolean isNeutral() {
        return this.sentiment.equals(SENTIMENT_NEUTRAL);
    }

    public boolean isNegative() {
        return this.sentiment.equals(SENTIMENT_NEGATIVE);
    }

    public boolean isVeryNegative() {
        return this.sentiment.equals(SENTIMENT_VERY_NEGATIVE);
    }

    public boolean isAbbreviation() {
        return this.subtype.equals(TYPE_ABBREVIATION);
    }

    public boolean isEntity() { return this.subtype.equals(TYPE_ENTITY); }

    public boolean isDescription() { return this.subtype.equals(TYPE_DESCRIPTION); }

    public boolean isHuman() {
        return this.subtype.equals(TYPE_HUMAN);
    }

    public boolean isLocation() {
        return this.subtype.equals(TYPE_LOCATION);
    }

    public boolean isNumber() {
        return this.subtype.equals(TYPE_NUMBER);
    }

}

package ai.recast.sdk_android;

import org.json.*;

import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class Conversation {

	private static final String converseAPI = "https://api.recast.ai/v2/converse";

	private String raw; // String: the raw unparsed json response								ok
	private String uuid; // String: the universal unique id of the api call						ok
	private String source; // String: the user input											ok
	private String[] replies; // Array[String]: all the replies									ok
	private Action action; // Object: the action of the conversation							ok
	private Action[] nextActions; // Array[Object]: the next actions of the conversation		ok
	private Memory memory; // Object: the memory of the conversation							ok
	private Map<String, Entity[]> entities; // Array[Entity]: the array of entities				ok
	private Intent[] intents; // Array[Object]: all the matched intents							ok
	private String conversationToken; // String: the conversation token							ok
	private String language; // String: the language of the input								ok
	private String version; // String: the API version											ok
	private String timestamp; // String: the timestamp at the end of the processing				ok
	private int status; // String: the status of the response									ok

	private String token;

	public Conversation(String json, String token) throws RecastException {
		this.setToken(token);
		JSONArray	resultIntents = null;
		JSONArray   resultNextActions = null;
		JSONArray   resultReplies = null;
        JSONObject	result;
        this.raw = json;

        try {
            result = new JSONObject(json).getJSONObject("results");

            this.source = result.getString("source");
            this.version = result.getString("version");
            this.timestamp = result.getString("timestamp");
            this.status = result.getInt("status");
			this.language = result.getString("language");
            this.uuid = result.getString("uuid");
            this.conversationToken = result.getString("conversation_token");

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

            this.action = new Action(result.getJSONObject("action"));

            resultNextActions = result.getJSONArray("next_actions");
            this.nextActions = new Action[resultNextActions.length()];
            for(int i = 0; i < this.nextActions.length; ++i){
            	this.nextActions[i] = new Action(resultNextActions.getJSONObject(i));
            }

            this.memory = new Memory(result.getJSONObject("memory"));

            resultReplies = result.getJSONArray("replies");
            this.replies = new String[resultReplies.length()];
            for(int i = 0; i < this.replies.length; ++i){
            	this.replies[i] = resultReplies.optString(i);
            }

        } catch (Exception e) {
            throw new RecastException("Invalid JSON", e);
        }
	}

	public String reply(){
		return this.replies[0];
	}

	public Action nextAction(){
		return this.nextActions[0];
	}

	public String joinedReplies(){
		String replies = "";
		for(String rep : this.replies){
			replies = replies + rep;
		}
		return replies;
	}

	public void setMemoryEntity(int index, Entity newEntity){
		this.memory.setMemory(index, newEntity);
		Entity ent = this.memory.getEntities().get(index);
		JSONObject obj = new JSONObject(ent);
	}

	public String doApiRequest(String text, String token) throws RecastException {
      URL					obj;
      HttpsURLConnection	con;
      OutputStream		os;
      int					responseCode;
      String				inputLine;
      StringBuffer		responseBuffer;
      String				recastJson;

      try {
          obj = new URL(converseAPI);
          con = (HttpsURLConnection) obj.openConnection();
          con.setRequestMethod("POST");
          con.setRequestProperty("Authorization",  "Token " + token);
          con.setDoOutput(true);
          text = "text=" + text;
          os = con.getOutputStream();
          os.write(text.getBytes());
          os.flush();
          os.close();

          responseCode = con.getResponseCode();
      } catch (MalformedURLException e) {
          throw new RecastException("Invalid URL", e);
      } catch (IOException e) {
          throw new RecastException("Unable to read response from Recast", e);
      }

      if (responseCode == HttpsURLConnection.HTTP_OK) {
          try {
              BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
              responseBuffer = new StringBuffer();
              while ((inputLine = reader.readLine()) != null) {
                  responseBuffer.append(inputLine);
              }
              reader.close();
          } catch (IOException e) {
              throw new RecastException("Unable to read response from Recast", e);
          }
          recastJson = responseBuffer.toString();
      } else {
          System.out.println(responseCode);
          throw new RecastException(responseCode);
      }
      return recastJson;
	}

	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String[] getReplies() {
		return replies;
	}

	public void setReplies(String[] replies) {
		this.replies = replies;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action[] getNextActions() {
		return nextActions;
	}

	public void setNextActions(Action[] nextActions) {
		this.nextActions = nextActions;
	}

	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	public Map<String, Entity[]> getEntities() {
		return entities;
	}

	public void setEntities(Map<String, Entity[]> entities) {
		this.entities = entities;
	}

	public Intent[] getIntents() {
		return intents;
	}

	public void setIntents(Intent[] intents) {
		this.intents = intents;
	}

	public String getConversationToken() {
		return conversationToken;
	}

	public void setConversationToken(String conversationToken) {
		this.conversationToken = conversationToken;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}

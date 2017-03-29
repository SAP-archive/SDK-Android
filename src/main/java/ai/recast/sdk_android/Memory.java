package ai.recast.sdk_android;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Memory {

	private ArrayList<Entity> entities;


	Memory(JSONObject obj) {
		this.entities = new ArrayList<Entity>();
		Iterator<String> it = obj.keys();

        while (it.hasNext()) {
            String entityName = it.next();
            JSONObject entity = obj.optJSONObject(entityName);

            Entity ent = new Entity(entityName, entity);
            this.entities.add(ent);
        }
	}

	public ArrayList<Entity> getEntities(){
		return this.entities;
	}

	public void setMemory(int index, Entity newEntity){
		this.entities.remove(index);
		this.entities.add(newEntity);
	}

}

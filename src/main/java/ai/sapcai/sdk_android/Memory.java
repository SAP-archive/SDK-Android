package ai.sapcai.sdk_android;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

public class Memory {

	private Map<String, MemoryEntity> entities;


	Memory(JSONObject obj) {


        this.entities = new HashMap<String,MemoryEntity>();
        if (obj.length() != 0) {
            Iterator<String> it = obj.keys();

            while (it.hasNext()) {
                String entityName = it.next();
                JSONObject entity = obj.optJSONObject(entityName);
                if(entity != null){
                	this.entities.put(entityName, new MemoryEntity(entityName, entity));
                }else{
                	this.entities.put(entityName, null);
                }
            }
        }
	}

	public String convertMemory(){
		String memory = "{";
		for(Map.Entry<String, MemoryEntity> memap : this.entities.entrySet()){
			String entityName = memap.getKey();
			MemoryEntity me = memap.getValue();
			if(me != null){
				memory = memory + "\"" + entityName + "\":{\"raw\":\"" + me.getRaw() +
			            "\", \"value\":\"" + me.getValue() + "\", \"confidence\":\"" +
			            me.getConfidence() + "\"},";
			}else{
				memory = memory + "\"" + entityName + "\": null,";
			}
		}
		memory = memory.substring(0, memory.length()-1);
		memory = memory + "}";
		return memory;
	}

	public void setMemory(String name, MemoryEntity newEntity){
		this.entities.put(name, newEntity);
	}

	public void resetMemory(){
		this.entities = new HashMap<String,MemoryEntity>();
	}

	public Map<String, MemoryEntity> getEntities() {
		return entities;
	}


	public void setEntities(Map<String, MemoryEntity> entities) {
		this.entities = entities;
	}

}

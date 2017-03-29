package ai.recast.sdk_android;

import org.json.JSONObject;

public class Action {

	private String slug;
	private Boolean done;
	private String reply;

	private Action(String slug, Boolean done, String reply) {
		this.slug = slug;
		this.done = done;
		this.reply = reply;
	}

	Action (JSONObject obj) {
        this(obj.optString("slug"), obj.optBoolean("done"), obj.optString("reply"));
    }

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

}

package de.haw.md.akka.msg;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "nodeId", "active" })
public class ActorController {
	
	@JsonProperty("nodeId")
	private String nodeId;
	
	@JsonProperty("active")
	private boolean active;
	
	public ActorController(String nodeId, boolean active) {
		this.nodeId = nodeId;
		this.active = active;
	}

	@JsonProperty("nodeId")
	public String getNodeId() {
		return nodeId;
	}

	@JsonProperty("nodeId")
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	@JsonProperty("active")
	public boolean isActive() {
		return active;
	}

	@JsonProperty("active")
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public String toString(){
		ObjectMapper om = new ObjectMapper();
		try {
			return om.writeValueAsString(this);
		} catch (IOException e) {
			return "";
		}
	}

}

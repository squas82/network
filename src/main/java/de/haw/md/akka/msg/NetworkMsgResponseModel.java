package de.haw.md.akka.msg;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.haw.md.helper.MDHelper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "originalId", "recieved" })
public class NetworkMsgResponseModel extends MsgModel{
	
	@JsonProperty("originalId")
	private String originalId;
	
	@JsonProperty("recieved")
	private boolean recieved;
	
	public NetworkMsgResponseModel() {
		this(MDHelper.generatePackageID(), "", "", "", false);
	}

	public NetworkMsgResponseModel(String id, String src, String dst, String originalId, boolean recieved) {
		super(id, src, dst);
		this.originalId = originalId;
		this.recieved = recieved;
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

	@JsonProperty("originalId")
	public String getOriginalId() {
		return originalId;
	}
	
	@JsonProperty("originalId")
	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}

	@JsonProperty("recieved")
	public boolean isRecieved() {
		return recieved;
	}

	@JsonProperty("recieved")
	public void setRecieved(boolean recieved) {
		this.recieved = recieved;
	}
}

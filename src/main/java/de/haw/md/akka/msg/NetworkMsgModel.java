package de.haw.md.akka.msg;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.haw.md.helper.StaticValues;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "data"})
public class NetworkMsgModel extends MsgModel{
	
	@JsonProperty("data")
	private byte[] data;
	
	public NetworkMsgModel() {
		this(StaticValues.generatePackageID(), "", "", StaticValues.generatePackageID().getBytes());
		this.data = StaticValues.generatePackageID().getBytes();
	}

	public NetworkMsgModel(String id, String src, String dst, byte[] data) {
		super(id, src, dst);
		this.data = data;
	}
	@JsonProperty("data")
	public byte[] getData() {
		return data;
	}
	@JsonProperty("data")
	public void setData(byte[] data) {
		this.data = data;
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

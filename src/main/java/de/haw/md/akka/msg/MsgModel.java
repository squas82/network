package de.haw.md.akka.msg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "src", "originalSrc", "dst" })
public class MsgModel {

	@JsonProperty("id")
	private String id;

	@JsonProperty("src")
	private String src;

	@JsonProperty("originalSrc")
	private String originalSrc;

	@JsonProperty("dst")
	private String dst;

	public MsgModel(String id, String src, String dst) {
		this.id = id;
		this.src = src;
		this.originalSrc = src;
		this.dst = dst;
	}
	
	public MsgModel(String id, String src, String originalSrc, String dst) {
		this.id = id;
		this.src = src;
		this.originalSrc = originalSrc;
		this.dst = dst;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("src")
	public String getSrc() {
		return src;
	}

	@JsonProperty("src")
	public void setSrc(String src) {
		this.src = src;
	}

	@JsonProperty("dst")
	public String getDst() {
		return dst;
	}

	@JsonProperty("dst")
	public void setDst(String dst) {
		this.dst = dst;
	}

	@JsonProperty("originalSrc")
	public String getOriginalSrc() {
		return originalSrc;
	}

	@JsonProperty("originalSrc")
	public void setOriginalSrc(String originalSrc) {
		this.originalSrc = originalSrc;
	}

}

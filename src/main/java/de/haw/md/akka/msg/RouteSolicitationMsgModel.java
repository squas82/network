package de.haw.md.akka.msg;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.haw.md.akka.main.Route;
import de.haw.md.helper.StaticValues;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "route", "hops" })
public class RouteSolicitationMsgModel extends MsgModel implements Cloneable {

	@JsonProperty("route")
	private Route route;

	@JsonProperty("hops")
	private int hops;

	public RouteSolicitationMsgModel() {
		this(StaticValues.generatePackageID(), "", "", "", new Route("", "", "", new ArrayList<>()), -1);
	}

	public RouteSolicitationMsgModel(String id, String src, String originalSrc, String dst, Route route, int hops) {
		super(id, src, dst);
		this.route = route;
		this.hops = hops;
	}

	@JsonProperty("route")
	public Route getRoute() {
		return route;
	}

	@JsonProperty("route")
	public void setRoute(Route route) {
		this.route = route;
	}

	@JsonProperty("hops")
	public int getHops() {
		return hops;
	}

	@JsonProperty("hops")
	public void setHops(int hops) {
		this.hops = hops;
	}
	
	public RouteSolicitationMsgModel clone() throws CloneNotSupportedException {
        return (RouteSolicitationMsgModel) super.clone();
    }
}

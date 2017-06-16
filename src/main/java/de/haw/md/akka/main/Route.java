package de.haw.md.akka.main;

import java.util.List;

public class Route implements Cloneable{

	private String src;

	private String dst;

	private String nextHop;
	
	private List<String> route;

	public Route(String src, String dst, String nextHop, List<String> route) {
		this.src = src;
		this.dst = dst;
		this.nextHop = nextHop;
		this.route = route;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public String getNextHop() {
		return nextHop;
	}

	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}

	public List<String> getRoute() {
		return route;
	}

	public void setRoute(List<String> route) {
		this.route = route;
	}
	
	public Route clone() throws CloneNotSupportedException {
        return (Route) super.clone();
    }

}

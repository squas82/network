package de.haw.md.akka.main;

public class NetworkRouteContainer {
	
	private static NetworkRouteContainer instance = null;
	
	public static synchronized NetworkRouteContainer getInstance() {
		if (instance == null) {
			instance = new NetworkRouteContainer();
		}
		return instance;
	}
	
}

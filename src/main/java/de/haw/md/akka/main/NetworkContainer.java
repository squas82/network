package de.haw.md.akka.main;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.Props;

public class NetworkContainer {

	private static NetworkContainer instance = null;

	private Map<String, ActorRef> publisher;
	
	private Network network;

	public NetworkContainer() {
		publisher = new HashMap<>();
	}

	public ActorRef getPublisher(String channel) {
		ActorRef publishActor = null;
		if (publisher.containsKey(channel)) {
			publishActor = publisher.get(channel);
		} else {
			publishActor = ActorSystemContainer.getInstance().getSystem().actorOf(Props.create(Network.class, channel), channel);
			publisher.put(channel, publishActor);
		}
		return publishActor;
	}

	public static synchronized NetworkContainer getInstance() {
		if (instance == null) {
			instance = new NetworkContainer();
		}
		return instance;
	}

	public Network getNetwork() {
		return network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}

}

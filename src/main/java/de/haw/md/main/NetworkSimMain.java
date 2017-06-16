package de.haw.md.main;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import de.haw.md.akka.main.ActorSystemContainer;
import de.haw.md.akka.main.Network;
import de.haw.md.akka.main.NetworkContainer;
import de.haw.md.akka.main.NetworkNode;
import de.haw.md.helper.StaticValues;
import scala.concurrent.duration.Duration;

public class NetworkSimMain {

	private static String CHANNEL = "NetworkTest";

	public static void main(String[] args) {
		ActorSystem system = ActorSystemContainer.getInstance().getSystem();
		system.actorOf(Props.create(Network.class, CHANNEL));
		for (int i = 0; i < StaticValues.NODES.length; i++) 
			system.actorOf(Props.create(NetworkNode.class, CHANNEL, StaticValues.NODES[i]));
		
		final ActorRef publisher = NetworkContainer.getInstance().getPublisher(CHANNEL);
		//system.scheduler().scheduleOnce(Duration.Zero(), publisher, "Tick", system.dispatcher(), publisher);
		system.scheduler().schedule(Duration.Zero(), Duration.create(100, TimeUnit.MILLISECONDS), publisher, "Tick", system.dispatcher(), publisher);
	}

}

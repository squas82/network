package de.haw.md.akka.main;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import de.haw.md.akka.msg.NetworkMsgModel;
import de.haw.md.helper.MDHelper;
import de.haw.md.helper.StaticValues;

public class Network extends UntypedActor {

	private ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();

	private String channel;

	public Network(String channel) {
		this.channel = channel;
		NetworkContainer.getInstance().setNetwork(this);
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof String) {
			if (((String) msg).contains("Tick")){
				NetworkMsgModel createPackage = createPackage();
				mediator.tell(new DistributedPubSubMediator.Publish(channel, createPackage), getSelf());
				//System.out.println("PackageID: " + createPackage.getPackageID());
			}
		} else
			unhandled(msg);

	}

	public NetworkMsgModel createPackage() {
		String src;
		String dst;
		int countOfRoutes = StaticValues.ROUTES.length - 1;
		do {
			src = StaticValues.ROUTES[MDHelper.randInt(0, countOfRoutes)].split("-")[MDHelper.randInt(0, 1)];
			dst = StaticValues.ROUTES[MDHelper.randInt(0, countOfRoutes)].split("-")[MDHelper.randInt(0, 1)];
		} while (src.compareToIgnoreCase(dst) == 0);
//		src = "A";
//		dst = "E";
		return new NetworkMsgModel(MDHelper.generatePackageID(), src, dst, MDHelper.generatePackageID().getBytes());
	}

}

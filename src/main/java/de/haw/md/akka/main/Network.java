package de.haw.md.akka.main;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import de.haw.md.akka.msg.MsgModel;
import de.haw.md.akka.msg.NetworkMsgModel;
import de.haw.md.helper.MDHelper;
import de.haw.md.helper.StaticValues;

public class Network extends UntypedActor {

	private ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();

	private String channel;

	private boolean deactivatorSend = false;

	public Network(String channel) {
		this.channel = channel;
		NetworkContainer.getInstance().setNetwork(this);
//		mediator.tell(new DistributedPubSubMediator.Subscribe(channel, getSelf()), getSelf());
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof MsgModel) {
//			MDHelper.getInstance().addMsgToList((MsgModel) msg);
		}
		if (msg instanceof String) {
			if (((String) msg).contains("Tick")) {
//				if (!deactivatorSend) {
//					int countOfRoutes = StaticValues.ROUTES.length - 1;
//					ActorController actorController = new ActorController(
//							StaticValues.ROUTES[MDHelper.randInt(0, countOfRoutes)].split("-")[MDHelper.randInt(0, 1)], false);
//					mediator.tell(new DistributedPubSubMediator.Publish(channel, actorController), getSelf());
//					deactivatorSend = true;
//				}
				NetworkMsgModel createPackage = createPackage();
				mediator.tell(new DistributedPubSubMediator.Publish(channel, createPackage), getSelf());
			}
		} else
			unhandled(msg);

	}

	public NetworkMsgModel createPackage() {
		String src;
		String dst;
		int countOfRoutes = StaticValues.ROUTES.length - 1;
		do {
			src = StaticValues.ROUTES[StaticValues.randInt(0, countOfRoutes)].split("-")[StaticValues.randInt(0, 1)];
			dst = StaticValues.ROUTES[StaticValues.randInt(0, countOfRoutes)].split("-")[StaticValues.randInt(0, 1)];
		} while (src.compareToIgnoreCase(dst) == 0);
		// src = "A";
		// dst = "E";
		return new NetworkMsgModel(StaticValues.generatePackageID(), src, dst, StaticValues.generatePackageID().getBytes());
	}

}

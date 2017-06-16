package de.haw.md.akka.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import de.haw.md.akka.msg.NetworkMsgModel;
import de.haw.md.akka.msg.RouteSolicitationMsgModel;
import de.haw.md.akka.msg.RouteSolicitationResponseMsgModel;
import de.haw.md.helper.MDHelper;
import de.haw.md.helper.StaticValues;

public class NetworkNode extends UntypedActor {

	private String nodeID;

	private String channel;

	private Map<String, Route> routes;

	private List<NetworkMsgModel> queue;

	private List<String> solificationRequests = new ArrayList<>();

	private List<String> recievedPackages = new ArrayList<>();

	private boolean isActive = true;

	public NetworkNode(String channel, String nodeID) {
		this.channel = channel;
		this.nodeID = nodeID;
		this.routes = createRoutes();
		this.queue = new ArrayList<>();
		ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
		mediator.tell(new DistributedPubSubMediator.Subscribe(channel, getSelf()), getSelf());

	}

	private Map<String, Route> createRoutes() {
		Map<String, Route> routes = new HashMap<>();
		for (String dst : StaticValues.NEIGHBOURS.get(nodeID)) {
			if (dst.compareToIgnoreCase(nodeID) != 0) {
				final Route route = new Route(nodeID, dst, dst, Arrays.asList(dst));
				routes.put(dst, route);
			}
		}
		return routes;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (isActive) {
			ObjectMapper om = new ObjectMapper();
			if (msg instanceof NetworkMsgModel) {
				NetworkMsgModel networkMsgModel = (NetworkMsgModel) msg;
				if (nodeID.compareToIgnoreCase(networkMsgModel.getDst()) == 0 && nodeID.compareToIgnoreCase(networkMsgModel.getSrc()) == 0) {
					if (!proofRecieved(networkMsgModel.getId())) {
						recievedPackages.add(networkMsgModel.getId());
						System.err.println("Package " + networkMsgModel.getId() + " recieved! - Src: " + networkMsgModel.getOriginalSrc() + " | Destination: "
								+ networkMsgModel.getDst());
					}
				} else {
					if (nodeID.compareToIgnoreCase(networkMsgModel.getSrc()) == 0) {
						if (routes.containsKey(networkMsgModel.getDst())) {
							final String nextHop = routes.get(networkMsgModel.getDst()).getNextHop();
							networkMsgModel.setSrc(nextHop);
							DistributedPubSub.get(getContext().system()).mediator().tell(new DistributedPubSubMediator.Publish(channel, networkMsgModel),
									getSelf());
						} else {
							for (String neighbours : StaticValues.NEIGHBOURS.get(nodeID)) {
								List<String> routes = new ArrayList<>();
								routes.add(neighbours);
								RouteSolicitationMsgModel routeSolicitationMsgModel = new RouteSolicitationMsgModel(MDHelper.generatePackageID(), neighbours,
										nodeID, networkMsgModel.getDst(), new Route(nodeID, networkMsgModel.getDst(), neighbours, routes), 1);
								solificationRequests.add(routeSolicitationMsgModel.getId());
								DistributedPubSub.get(getContext().system()).mediator()
										.tell(new DistributedPubSubMediator.Publish(channel, routeSolicitationMsgModel), getSelf());
							}
							queue.add(networkMsgModel);
						}
					}
				}
			}
			if (msg instanceof RouteSolicitationMsgModel) {
				RouteSolicitationMsgModel solicitationMsgModel = (RouteSolicitationMsgModel) msg;
				if (solicitationMsgModel.getSrc().compareToIgnoreCase(nodeID) == 0) {
					if (!proofSolificationRequests(solicitationMsgModel.getId())) {
						solificationRequests.add(solicitationMsgModel.getId());
						if (routes.containsKey(solicitationMsgModel.getDst())) {
							if (solicitationMsgModel.getHops() > 1) {
								RouteSolicitationResponseMsgModel routeSolicitationResponseMsgModel = new RouteSolicitationResponseMsgModel(
										solicitationMsgModel.getId(),
										solicitationMsgModel.getRoute().getRoute().get(solicitationMsgModel.getRoute().getRoute().size() - 1), nodeID,
										solicitationMsgModel.getRoute().getSrc(), solicitationMsgModel.getRoute(), solicitationMsgModel.getHops());
								DistributedPubSub.get(getContext().system()).mediator()
										.tell(new DistributedPubSubMediator.Publish(channel, routeSolicitationResponseMsgModel), getSelf());
							} else {
								RouteSolicitationResponseMsgModel routeSolicitationResponseMsgModel = new RouteSolicitationResponseMsgModel(
										solicitationMsgModel.getId(), solicitationMsgModel.getRoute().getSrc(), nodeID,
										solicitationMsgModel.getRoute().getSrc(), solicitationMsgModel.getRoute(), solicitationMsgModel.getHops());
								DistributedPubSub.get(getContext().system()).mediator()
										.tell(new DistributedPubSubMediator.Publish(channel, routeSolicitationResponseMsgModel), getSelf());
							}
						} else {
							RouteSolicitationMsgModel solicitationMsgModelClone = solicitationMsgModel.clone();
							for (String neighbours : StaticValues.NEIGHBOURS.get(nodeID)) {
								boolean visited = false;
								for (String visitedNode : solicitationMsgModelClone.getRoute().getRoute()) {
									if (neighbours.compareToIgnoreCase(visitedNode) == 0) {
										visited = true;
									}
								}
								if (neighbours.compareToIgnoreCase(solicitationMsgModelClone.getRoute().getSrc()) == 0)
									visited = true;
								if (!visited) {
									List<String> routeList = copyList(solicitationMsgModelClone.getRoute().getRoute());
									Route route = solicitationMsgModel.getRoute().clone();
									routeList.add(neighbours);
									route.setRoute(routeList);
									RouteSolicitationMsgModel newSolicitationMsgModel = new RouteSolicitationMsgModel(solicitationMsgModelClone.getId(),
											neighbours, solicitationMsgModelClone.getOriginalSrc(), solicitationMsgModelClone.getDst(), route,
											solicitationMsgModelClone.getHops() + 1);
									DistributedPubSub.get(getContext().system()).mediator()
											.tell(new DistributedPubSubMediator.Publish(channel, newSolicitationMsgModel), getSelf());
								}
							}

						}
					}
				}
			}
			if (msg instanceof RouteSolicitationResponseMsgModel) {
				RouteSolicitationResponseMsgModel routeSolicitationResponseMsgModel = (RouteSolicitationResponseMsgModel) msg;
				if (routeSolicitationResponseMsgModel.getSrc().compareToIgnoreCase(nodeID) == 0) {
					if (routeSolicitationResponseMsgModel.getRoute().getSrc().compareToIgnoreCase(nodeID) != 0) {
						String src;
						int index = routeSolicitationResponseMsgModel.getRoute().getRoute().indexOf(nodeID) - 1;
						if (index < 0) {
							src = routeSolicitationResponseMsgModel.getDst();
						} else {
							src = routeSolicitationResponseMsgModel.getRoute().getRoute().get(index);
						}
						RouteSolicitationResponseMsgModel newRouteSolicitationResponseMsgModel = new RouteSolicitationResponseMsgModel(
								routeSolicitationResponseMsgModel.getId(), src, routeSolicitationResponseMsgModel.getOriginalSrc(),
								routeSolicitationResponseMsgModel.getDst(), routeSolicitationResponseMsgModel.getRoute(),
								routeSolicitationResponseMsgModel.getHops());
//						System.out.println("NodeID: " + nodeID + " | " + om.writeValueAsString(newRouteSolicitationResponseMsgModel));
						DistributedPubSub.get(getContext().system()).mediator()
								.tell(new DistributedPubSubMediator.Publish(channel, newRouteSolicitationResponseMsgModel), getSelf());
					} else {
						Route route = routeSolicitationResponseMsgModel.getRoute();
						if (routes.containsKey(route.getDst())) {
							if (routes.get(route.getDst()).getRoute().size() > route.getRoute().size())
								routes.replace(route.getDst(), route);
						} else
							routes.put(route.getDst(), route);
//						System.out.println("Routes in Node: " + nodeID + " | " + routes.keySet().size());
					}
				}
			}
			proofQueue(om);
		}
	}

	private List<String> copyList(List<String> route) {
		List<String> newList = new ArrayList<>();
		for (String string : route) {
			newList.add(string);
		}
		return newList;
	}

	private boolean proofRecieved(String id) {
		for (String recievedPackage : recievedPackages)
			if (recievedPackage.compareToIgnoreCase(id) == 0)
				return true;
		return false;
	}

	private boolean proofSolificationRequests(String id) {
		for (String solificationRequest : solificationRequests)
			if (solificationRequest.compareToIgnoreCase(id) == 0)
				return true;
		return false;
	}

	private void proofQueue(ObjectMapper om) {
		List<NetworkMsgModel> queueList = new ArrayList<>();
		try {
			for (NetworkMsgModel networkMsgModel : queue)
				if (routes.containsKey(networkMsgModel.getDst())) {
					String nextHop = routes.get(networkMsgModel.getDst()).getNextHop();
					networkMsgModel.setSrc(nextHop);
					DistributedPubSub.get(getContext().system()).mediator().tell(new DistributedPubSubMediator.Publish(channel, networkMsgModel), getSelf());
					queueList.add(networkMsgModel);
				}
			for (NetworkMsgModel networkMsgModel : queueList)
				queue.remove(networkMsgModel);
			queueList.clear();
		} catch (Exception e) {
		}
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}

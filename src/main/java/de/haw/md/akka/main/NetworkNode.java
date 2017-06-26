package de.haw.md.akka.main;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import de.haw.md.akka.msg.ActorController;
import de.haw.md.akka.msg.MsgModel;
import de.haw.md.akka.msg.NetworkMsgModel;
import de.haw.md.akka.msg.NetworkMsgResponseModel;
import de.haw.md.akka.msg.RouteSolicitationMsgModel;
import de.haw.md.akka.msg.RouteSolicitationResponseMsgModel;
import de.haw.md.helper.MDHelper;
import de.haw.md.helper.RecievedPackeges;
import de.haw.md.helper.StaticValues;

public class NetworkNode extends UntypedActor {

	private String nodeID;

	private String channel;

	private Map<String, Route> routes;

	private List<MsgModel> queue;

	private Set<String> solificationRequests = new HashSet<>();

	private List<String> recievedPackages = new ArrayList<>();

	private Map<String, RecievedPackeges> recievedPackagesMap = new HashMap<>();

	private long counter;

	private boolean isActive = true;

	public NetworkNode(String channel, String nodeID) {
		this.channel = channel;
		this.nodeID = nodeID;
		this.routes = createRoutes();
		this.queue = new ArrayList<>();
		this.counter = System.currentTimeMillis();
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

	/**
	 * Erh�lt alle Nachrichten und ordnet sie den Methoden zu damit diese
	 * verarbeitet weden k�nnen.
	 * 
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof ActorController) {
			ActorController actorController = (ActorController) msg;
			if (nodeID.compareToIgnoreCase(actorController.getNodeId()) == 0) {
				this.setActive(actorController.isActive());
				System.err.println("Node: '" + nodeID + "' active = " + isActive);
			}
		}
		if (isActive) {
			if (msg instanceof NetworkMsgModel) {
				NetworkMsgModel networkMsgModel = (NetworkMsgModel) msg;
				handleNetworkMsg(networkMsgModel);
			}
			if (msg instanceof RouteSolicitationMsgModel) {
				RouteSolicitationMsgModel solicitationMsgModel = (RouteSolicitationMsgModel) msg;
				handleRouteSoli(solicitationMsgModel);
			}
			if (msg instanceof RouteSolicitationResponseMsgModel) {
				RouteSolicitationResponseMsgModel routeSolicitationResponseMsgModel = (RouteSolicitationResponseMsgModel) msg;
				handleRouteSoliResponse(routeSolicitationResponseMsgModel);
			}
			if (msg instanceof NetworkMsgResponseModel) {
				NetworkMsgResponseModel networkMsgResponseModel = (NetworkMsgResponseModel) msg;
				handleNetworkMsgResponseModel(networkMsgResponseModel);
			}
			if ((System.currentTimeMillis() - counter) > 500) {
				counter = System.currentTimeMillis();
				proofQueue();
				proofRecievedPackagesMap();
			}
		}
	}

	private void proofRecievedPackagesMap() {
		List<String> packageToRemove = new ArrayList<>();
		for (String packageID : recievedPackagesMap.keySet()) {
			RecievedPackeges recievedPackege = recievedPackagesMap.get(packageID);
			if (!recievedPackege.isRecieved() && recievedPackege.getTimestamp().plusSeconds(1).isBefore(LocalDateTime.now())) {
				if (recievedPackege.getSendingAttempts() < 3) {
					recievedPackege.setSendingAttempts(recievedPackege.getSendingAttempts() + 1);
					handleNetworkMsgHelper(recievedPackege.getMsgModel());
				} else if (recievedPackege.getSendingAttempts() < StaticValues.MAX_ATTEMPTS_TO_TIMEOUT) {
					recievedPackege.setSendingAttempts(recievedPackege.getSendingAttempts() + 1);
					routes.remove(recievedPackege.getMsgModel().getDst());
					handleNetworkMsgHelper(recievedPackege.getMsgModel());
				} else {
//					System.err.println("Package: " + packageID + " transmission failed!");
					routes.remove(recievedPackege.getMsgModel().getDst());
					MDHelper.getInstance().addToFailedNetworkMsgModel();
					packageToRemove.add(packageID);
				}
			}
		}
		for (String string : packageToRemove) {
			recievedPackagesMap.remove(string);
		}

	}

	private void handleNetworkMsgResponseModel(NetworkMsgResponseModel networkMsgResponseModel) {
		if (!proofRecieved(networkMsgResponseModel.getId())) {
			if (nodeID.compareToIgnoreCase(networkMsgResponseModel.getDst()) == 0 && nodeID.compareToIgnoreCase(networkMsgResponseModel.getSrc()) == 0) {
				recievedPackages.add(networkMsgResponseModel.getId());
				MDHelper.getInstance().addNetworkMsgResponseModel();
				if (recievedPackagesMap.containsKey(networkMsgResponseModel.getOriginalId())) {
					RecievedPackeges recievedPackeges = recievedPackagesMap.get(networkMsgResponseModel.getOriginalId());
					recievedPackeges.setRecieved(true);
					System.out.println("Package successful transmittet, PackegeID: " + recievedPackeges.getMsgModel().getId());
				}

			} else {
				if (nodeID.compareToIgnoreCase(networkMsgResponseModel.getSrc()) == 0){
					MDHelper.getInstance().addNetworkMsgResponseModel();
					handleNetworkMsgHelper(networkMsgResponseModel);
				}
			}
		}
	}

	/**
	 * Bearbeitet den Paketversand
	 * 
	 * @param networkMsgModel
	 */
	private void handleNetworkMsg(NetworkMsgModel networkMsgModel) {
		if (nodeID.compareToIgnoreCase(networkMsgModel.getDst()) == 0 && nodeID.compareToIgnoreCase(networkMsgModel.getSrc()) == 0) {
			if (!proofRecieved(networkMsgModel.getId())) {
				MDHelper.getInstance().addNetworkMsgModel();
				recievedPackages.add(networkMsgModel.getId());
				NetworkMsgResponseModel networkMsgResponseModel = new NetworkMsgResponseModel(StaticValues.generatePackageID(), networkMsgModel.getDst(),
						networkMsgModel.getOriginalSrc(), networkMsgModel.getId(), true);
				handleNetworkMsgHelper(networkMsgResponseModel);
//				System.out.println("Package " + networkMsgModel.getId() + " recieved! - Src: " + networkMsgModel.getOriginalSrc() + " | Destination: "
//						+ networkMsgModel.getDst());
			}
		} else {
			if (nodeID.compareToIgnoreCase(networkMsgModel.getSrc()) == 0) {
				MDHelper.getInstance().addNetworkMsgModel();
				if (nodeID.compareToIgnoreCase(networkMsgModel.getSrc()) == 0 && nodeID.compareToIgnoreCase(networkMsgModel.getOriginalSrc()) == 0) {
					recievedPackagesMap.put(networkMsgModel.getId(), new RecievedPackeges(networkMsgModel, false, LocalDateTime.now()));
				}
				handleNetworkMsgHelper(networkMsgModel);
			}
		}
	}

	private void handleNetworkMsgHelper(MsgModel networkMsgModel) {
		if (routes.containsKey(networkMsgModel.getDst())) {
			final String nextHop = routes.get(networkMsgModel.getDst()).getNextHop();
			networkMsgModel.setSrc(nextHop);
			send(networkMsgModel);
		} else {
			for (String neighbours : StaticValues.NEIGHBOURS.get(nodeID)) {
				List<String> routes = new ArrayList<>();
				routes.add(neighbours);
				RouteSolicitationMsgModel routeSolicitationMsgModel = new RouteSolicitationMsgModel(StaticValues.generatePackageID(), neighbours, nodeID,
						networkMsgModel.getDst(), new Route(nodeID, networkMsgModel.getDst(), neighbours, routes), 1);
				solificationRequests.add(routeSolicitationMsgModel.getId());
				send(routeSolicitationMsgModel);
			}
			queue.add(networkMsgModel);
		}
	}

	/**
	 * Bearbeitet Routen-Anfragen
	 * 
	 * @param solicitationMsgModel
	 * @throws CloneNotSupportedException
	 */
	private void handleRouteSoli(RouteSolicitationMsgModel solicitationMsgModel) throws CloneNotSupportedException {
		if (solicitationMsgModel.getSrc().compareToIgnoreCase(nodeID) == 0) {
			if (!proofSolificationRequests(solicitationMsgModel.getId())) {
				MDHelper.getInstance().addSolicitationMsgModel();
				solificationRequests.add(solicitationMsgModel.getId());
				if (routes.containsKey(solicitationMsgModel.getDst())) {
					if (solicitationMsgModel.getHops() > 1) {
						RouteSolicitationResponseMsgModel routeSolicitationResponseMsgModel = new RouteSolicitationResponseMsgModel(
								solicitationMsgModel.getId(),
								solicitationMsgModel.getRoute().getRoute().get(solicitationMsgModel.getRoute().getRoute().size() - 1), nodeID,
								solicitationMsgModel.getRoute().getSrc(), solicitationMsgModel.getRoute(), solicitationMsgModel.getHops());
						send(routeSolicitationResponseMsgModel);
					} else {
						RouteSolicitationResponseMsgModel routeSolicitationResponseMsgModel = new RouteSolicitationResponseMsgModel(
								solicitationMsgModel.getId(), solicitationMsgModel.getRoute().getSrc(), nodeID, solicitationMsgModel.getRoute().getSrc(),
								solicitationMsgModel.getRoute(), solicitationMsgModel.getHops());
						send(routeSolicitationResponseMsgModel);
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
							RouteSolicitationMsgModel newSolicitationMsgModel = new RouteSolicitationMsgModel(solicitationMsgModelClone.getId(), neighbours,
									solicitationMsgModelClone.getOriginalSrc(), solicitationMsgModelClone.getDst(), route,
									solicitationMsgModelClone.getHops() + 1);
							send(newSolicitationMsgModel);
						}
					}

				}
			}
		}
	}

	/**
	 * Bearbeitet die Antworten auf eine Routen-Anfrage
	 * 
	 * @param routeSolicitationResponseMsgModel
	 */
	private void handleRouteSoliResponse(RouteSolicitationResponseMsgModel routeSolicitationResponseMsgModel) {
		if (routeSolicitationResponseMsgModel.getSrc().compareToIgnoreCase(nodeID) == 0) {
			MDHelper.getInstance().addSolicitationResponseMsgModel();
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
						routeSolicitationResponseMsgModel.getDst(), routeSolicitationResponseMsgModel.getRoute(), routeSolicitationResponseMsgModel.getHops());
				// System.out.println("NodeID: " + nodeID + " | " +
				// om.writeValueAsString(newRouteSolicitationResponseMsgModel));
				send(newRouteSolicitationResponseMsgModel);
			} else {
				Route route = routeSolicitationResponseMsgModel.getRoute();
				if (routes.containsKey(route.getDst())) {
					if (routes.get(route.getDst()).getRoute().size() > route.getRoute().size())
						routes.replace(route.getDst(), route);
				} else
					routes.put(route.getDst(), route);
				// System.out.println("Routes in Node: " + nodeID + " | " +
				// routes.keySet().size());
			}
		}
	}

	private void send(Object object) {
		DistributedPubSub.get(getContext().system()).mediator().tell(new DistributedPubSubMediator.Publish(channel, object), getSelf());
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

	/**
	 * �berpr�ft mithilfe der ID ob eine Routenanfrage schon bearbeitet wurde
	 * 
	 * @param id
	 * @return
	 */
	private boolean proofSolificationRequests(String id) {
		for (String solificationRequest : solificationRequests)
			if (solificationRequest.compareToIgnoreCase(id) == 0)
				return true;
		return false;
	}

	/**
	 * Verarbeitet nicht versendete Pakete, welche Aufgrund einer fehlenden
	 * Route nicht versand wurden
	 * 
	 * @param om
	 */
	private void proofQueue() {
		List<MsgModel> queueList = new ArrayList<>();
		try {
			for (MsgModel networkMsgModel : queue)
				if (routes.containsKey(networkMsgModel.getDst())) {
					String nextHop = routes.get(networkMsgModel.getDst()).getNextHop();
					networkMsgModel.setSrc(nextHop);
					send(networkMsgModel);
					queueList.add(networkMsgModel);
				}
			for (MsgModel networkMsgModel : queueList)
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

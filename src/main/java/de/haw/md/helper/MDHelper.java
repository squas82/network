package de.haw.md.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import de.haw.md.akka.msg.MsgModel;
import de.haw.md.akka.msg.NetworkMsgModel;
import de.haw.md.akka.msg.NetworkMsgResponseModel;
import de.haw.md.akka.msg.RouteSolicitationMsgModel;
import de.haw.md.akka.msg.RouteSolicitationResponseMsgModel;

public class MDHelper {

	private static MDHelper instance = null;

	private List<NetworkMsgModel> networkMsgModelsList = new ArrayList<>();
	
	private List<NetworkMsgResponseModel> networkMsgResponseModelsList = new ArrayList<>();

	private List<RouteSolicitationMsgModel> solicitationMsgModelsList = new ArrayList<>();

	private List<RouteSolicitationResponseMsgModel> solicitationResponseMsgModelsList = new ArrayList<>();

	public static synchronized MDHelper getInstance() {
		if (instance == null) {
			instance = new MDHelper();
		}
		return instance;
	}

	public static String generatePackageID() {
		return UUID.randomUUID().toString();
	}

	public static int randInt(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

	public void addMsgToList(MsgModel msg) {
		if (msg instanceof NetworkMsgModel) {
			networkMsgModelsList.add((NetworkMsgModel) msg);
//			System.err.println("Versendete NetworkMsgModel: " + networkMsgModelsList.size());
		}
		if (msg instanceof NetworkMsgResponseModel) {
			networkMsgResponseModelsList.add((NetworkMsgResponseModel) msg);
//			System.err.println("Versendete NetworkMsgResponseModel: " + networkMsgResponseModelsList.size());
		}
		if (msg instanceof RouteSolicitationMsgModel) {
			solicitationMsgModelsList.add((RouteSolicitationMsgModel) msg);
//			System.err.println("Versendete RouteSolicitationMsgModel: " + solicitationMsgModelsList.size());
		}
		if (msg instanceof RouteSolicitationResponseMsgModel) {
			solicitationResponseMsgModelsList.add((RouteSolicitationResponseMsgModel) msg);
//			System.err.println("Versendete RouteSolicitationResponseMsgModel: " + solicitationResponseMsgModelsList.size());
		}
	}

}

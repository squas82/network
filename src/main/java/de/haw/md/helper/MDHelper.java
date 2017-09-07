package de.haw.md.helper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import de.haw.md.akka.msg.MsgModel;

public class MDHelper {

	private static MDHelper instance = null;

	private BigDecimal networkMsgModelsList = BigDecimal.ZERO;

	private BigDecimal failedNetworkMsgModelsList = BigDecimal.ZERO;

	private BigDecimal networkMsgResponseModelsList = BigDecimal.ZERO;

	private BigDecimal solicitationMsgModelsList = BigDecimal.ZERO;

	private BigDecimal solicitationResponseMsgModelsList = BigDecimal.ZERO;

	private Map<String, BigDecimal> msgCounterNode = new HashMap<>();

	public static synchronized MDHelper getInstance() {
		if (instance == null) {
			instance = new MDHelper();
		}
		return instance;
	}

	public void addMsgToList(MsgModel msg) {
		// if (msg instanceof NetworkMsgModel) {
		// networkMsgModelsList++;
		// }
		// if (msg instanceof NetworkMsgResponseModel) {
		// networkMsgResponseModelsList++;
		// }
		// if (msg instanceof RouteSolicitationMsgModel) {
		// solicitationMsgModelsList++;
		// }
		// if (msg instanceof RouteSolicitationResponseMsgModel) {
		// solicitationResponseMsgModelsList++;
		// }
	}

	public BigDecimal getNetworkMsgModelsList() {
		BigDecimal tmp = new BigDecimal(networkMsgModelsList.intValue());
		networkMsgModelsList = BigDecimal.ZERO;
		return tmp;
	}

	public BigDecimal getFailedNetworkMsgModelsList() {
		BigDecimal tmp = new BigDecimal(failedNetworkMsgModelsList.intValue());
		failedNetworkMsgModelsList = BigDecimal.ZERO;
		return tmp;
	}

	public BigDecimal getNetworkMsgResponseModelsList() {
		BigDecimal tmp = new BigDecimal(networkMsgResponseModelsList.intValue());
		networkMsgResponseModelsList = BigDecimal.ZERO;
		return tmp;
	}

	public BigDecimal getSolicitationMsgModelsList() {
		BigDecimal tmp = new BigDecimal(solicitationMsgModelsList.intValue());
		solicitationMsgModelsList = BigDecimal.ZERO;
		return tmp;
	}

	public BigDecimal getSolicitationResponseMsgModelsList() {
		BigDecimal tmp = new BigDecimal(solicitationResponseMsgModelsList.intValue());
		solicitationResponseMsgModelsList = BigDecimal.ZERO;
		return tmp;
	}

	public BigDecimal getMsgCounterNode(String nodeId) {
		if (msgCounterNode.containsKey(nodeId))
			return msgCounterNode.get(nodeId) != null ? msgCounterNode.get(nodeId) : BigDecimal.ZERO;
		else
			return BigDecimal.ZERO;
	}

	public void addMsgToMap(String nodeId) {
		if (msgCounterNode.containsKey(nodeId))
			msgCounterNode.replace(nodeId, msgCounterNode.get(nodeId) != null
					? msgCounterNode.get(nodeId).add(BigDecimal.ONE) : BigDecimal.ONE);
		else
			msgCounterNode.put(nodeId, BigDecimal.ONE);
	}

	public void addToFailedNetworkMsgModel() {
		this.failedNetworkMsgModelsList = this.failedNetworkMsgModelsList.add(BigDecimal.ONE);
	}

	public void addNetworkMsgModel() {
		this.networkMsgModelsList = this.networkMsgModelsList.add(BigDecimal.ONE);
	}

	public void addNetworkMsgResponseModel() {
		this.networkMsgResponseModelsList = this.networkMsgResponseModelsList.add(BigDecimal.ONE);
	}

	public void addSolicitationMsgModel() {
		this.solicitationMsgModelsList = this.solicitationMsgModelsList.add(BigDecimal.ONE);
	}

	public void addSolicitationResponseMsgModel() {
		this.solicitationResponseMsgModelsList = this.solicitationResponseMsgModelsList.add(BigDecimal.ONE);
	}
}

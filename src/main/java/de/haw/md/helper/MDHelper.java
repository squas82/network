package de.haw.md.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

public class MDHelper {

	private static MDHelper instance = null;
	
	public static final Map<String, List<String>> NEIGHBOURS = createNeighboursList();

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

	public static String findNextHop(String src, String dst) {
		return getDirections(src, dst).get(1);
	}

	private static List<String> getDirections(String start, String finish) {
		//System.out.println("Called");
		Map<String, Boolean> vis = new HashMap<String, Boolean>();
		Map<String, String> prev = new HashMap<String, String>();
		List<String> directions = new LinkedList<>();
		Queue<String> q = new LinkedList<>();
		String current = start;
		q.add(current);
		vis.put(current, true);
		while (!q.isEmpty()) {
			current = q.remove();
			if (current.equals(finish)) {
				break;
			} else {
				for (String node : MDHelper.NEIGHBOURS.get(current)) {
					if (!vis.containsKey(node)) {
						q.add(node);
						vis.put(node, true);
						prev.put(node, current);
					}
				}
			}
		}
		if (!current.equals(finish)) {
			System.out.println("can't reach destination");
		}
		for (String node = finish; node != null; node = prev.get(node)) {
			directions.add(node);
		}
		Collections.reverse(directions);
		return directions;
	}
	
	public static Map<String, List<String>> createNeighboursList() {
		Map<String, List<String>> neighbours = new HashMap<>();
		for (int i = 0; i < StaticValues.ROUTES.length; i++) {
			String[] nodesFromRoutes = StaticValues.ROUTES[i].split("-");
			if (neighbours.containsKey(nodesFromRoutes[0])) {
				List<String> nList = neighbours.get(nodesFromRoutes[0]);
				nList.add(nodesFromRoutes[1]);
				neighbours.replace(nodesFromRoutes[0], nList);
			} else {
				List<String> list = new ArrayList<>();
				list.add(nodesFromRoutes[1]);
				neighbours.put(nodesFromRoutes[0], list);
			}
			if (neighbours.containsKey(nodesFromRoutes[1])) {
				List<String> nList = neighbours.get(nodesFromRoutes[1]);
				nList.add(nodesFromRoutes[0]);
				neighbours.replace(nodesFromRoutes[1], nList);
			} else {
				List<String> list = new ArrayList<>();
				list.add(nodesFromRoutes[0]);
				neighbours.put(nodesFromRoutes[1], list);
			}
		}
		return neighbours;
	}
	
}

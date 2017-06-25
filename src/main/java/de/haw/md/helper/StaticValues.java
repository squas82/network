package de.haw.md.helper;

import java.util.HashMap;
import java.util.Map;

public class StaticValues {

	public static String[] ROUTES = { 	"A-B", "A-F", 
										"B-C", "B-D", 
										"C-D", "C-G", "C-F",  
										"D-E", "D-I", 
										"E-J", 
										"F-J", "F-K",
										"G-H", "G-L", "G-M",
										"H-I",
										"I-J",
										"J-K",
										"K-O",
										"L-M", "L-P",
										"M-N", "M-Q",
										"N-O", "N-R",
										"O-S",
										"P-Q", "P-T",
										"Q-R",
										"R-S", "R-T",
										"S-T"
	};

	public static String[] NODES = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T" };
	
	public static int MAX_ATTEMPTS_TO_TIMEOUT = 5;

	public static final Map<String, String[]> NEIGHBOURS = createMap();

	private static Map<String, String[]> createMap() {
		Map<String, String[]> myMap = new HashMap<>();
		myMap.put("A", new String[]{"B", "F"});
		myMap.put("B", new String[]{"A", "C", "D"});
		myMap.put("C", new String[]{"B", "D", "G", "H"});
		myMap.put("D", new String[]{"B", "C", "E", "I"});
		myMap.put("E", new String[]{"A", "D", "J"});
		myMap.put("F", new String[]{"A", "J", "K"});
		myMap.put("G", new String[]{"C", "H", "L", "M"});
		myMap.put("H", new String[]{"C", "G", "I"});
		myMap.put("I", new String[]{"D", "H", "J"});
		myMap.put("J", new String[]{"E", "F", "I", "K"});
		myMap.put("K", new String[]{"F", "J", "O"});
		myMap.put("L", new String[]{"G", "M", "P"});
		myMap.put("M", new String[]{"G", "N", "L", "Q"});
		myMap.put("N", new String[]{"J", "M", "O", "R"});
		myMap.put("O", new String[]{"K", "N", "S"});
		myMap.put("P", new String[]{"L", "Q", "T"});
		myMap.put("Q", new String[]{"M", "P", "R"});
		myMap.put("R", new String[]{"N", "Q", "S", "T"});
		myMap.put("S", new String[]{"O", "R", "T"});
		myMap.put("T", new String[]{"P", "R", "S"});
		return myMap;
	}

}

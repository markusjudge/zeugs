import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Constructor;

public class MultiRunner {

	public static void main(String[] args) throws Exception {
		if (args.length < 7) {
			System.out.println("Usage: java MultiRunner lowerBound upperBound time games "+
					"makerClass1 [makerClass2 ...] : breakerClass1 [breakerClass2 ...]");
			System.exit(0);
		}
		int lower = Integer.parseInt(args[0]);
		int upper = Integer.parseInt(args[1]);
		int time = Integer.parseInt(args[2]);
		int numGames = Integer.parseInt(args[3]);
		
		List<String> makerNames = new ArrayList<String>();
		List<String> breakerNames = new ArrayList<String>();
		boolean isMaker = true;
		for (int i = 4; i < args.length; i++) {
			if (args[i].equals(":")) isMaker = false;
			else if (isMaker) makerNames.add(args[i]);
			else breakerNames.add(args[i]);
		}
		
		new MultiRunner(lower, upper, time, numGames, makerNames, breakerNames);
	}

	Graph graph;
	int numColors;
	int numNodes;
	int[] coloring;

	public MultiRunner(int lower, int upper, int time, int numGames,
			List<String> makerNames, List<String> breakerNames) throws Exception {
		
		Class[] constructorTypes = { Graph.class, int.class, int.class };
		List<Constructor> makerConstructors = new ArrayList<Constructor>();
		List<Constructor> breakerConstructors = new ArrayList<Constructor>();
		boolean isMaker = true;
		for (String name : makerNames)
			makerConstructors.add(Class.forName(name).getConstructor(constructorTypes));
		for (String name : breakerNames)
			breakerConstructors.add(Class.forName(name).getConstructor(constructorTypes));
		
		int[][][] winCounts = new int[makerNames.size()][breakerNames.size()][2]; 
		
		while (numGames --> 0) {
			createGraph(lower, upper);
			
			for (int makerIndex = 0; makerIndex < makerNames.size(); makerIndex++) {
				if (makerIndex == 0) System.out.println("===");
				for (int breakerIndex = 0; breakerIndex < breakerNames.size(); breakerIndex++) {
					Player[] players = {
							(Player) makerConstructors.get(makerIndex).newInstance(graph, numColors, time),
							(Player) breakerConstructors.get(breakerIndex).newInstance(graph, numColors, time)
					};
		
					int playerIndex = 0;
					boolean isTimeout = false;
					boolean hasEnded = false;
					
					coloring = new int[numNodes];
					Turn turn = null;
					
					while (!hasEnded) {
						Player player = players[playerIndex];
						
						long startTime = System.currentTimeMillis();
						turn = player.getNextTurn(turn);
						long spentTime = System.currentTimeMillis() - startTime;
						
						if (spentTime > time) isTimeout = true;
		
						if (!isLegal(turn) || isTimeout) {
							winCounts[makerIndex][breakerIndex][1 - playerIndex]++;
							//if (isTimeout) System.out.println("TIMEOUT");
							//else System.out.println("ILLEGAL MOVE");
							hasEnded = true;
						} else {
							coloring[id(turn.v)] = turn.color;
							playerIndex = 1 - playerIndex;
							
							if (colored() || !allNodesLegal()) {
								if (colored()) winCounts[makerIndex][breakerIndex][0]++;
								else winCounts[makerIndex][breakerIndex][1]++;
								hasEnded = true;
							}
						}
					}
					
					System.out.println(Arrays.toString(winCounts[makerIndex][breakerIndex]) + " " +
							makerNames.get(makerIndex) + " vs " + breakerNames.get(breakerIndex));
				}
			}
		}
	}

	void createGraph(int lower, int upper) {
		graph = new SingleGraph("Random");
		numNodes = getBoundedRandom(lower, upper);
		double prob = Math.random();

		for (int i = 0; i < numNodes; i++)
			graph.addNode("" + i);

		for (int i = 0; i < numNodes; i++) {
			for (int j = i + 1; j < numNodes; j++) {
				double act = Math.random();
				if (act >= prob)
					graph.addEdge("" + i + "" + j, "" + i, "" + j);
			}
		}

		int maxDegree = 0;
		for (Node v : graph)
			maxDegree = Math.max(maxDegree, v.getDegree());

		numColors = getBoundedRandom(3, maxDegree);
	}

	public int getBoundedRandom(int lower, int upper) {
		if (upper < lower) return lower;
		return (int) (Math.random() * (upper - lower) + lower);
	}

	public HashSet<Integer> legalColors(Node v) {
		HashSet<Integer> result = new HashSet<Integer>();
		for (int i = 1; i <= numColors; i++) result.add(i);
		
		Iterator<? extends Node> it = v.getNeighborNodeIterator();
		while (it.hasNext()) {
			Node w = it.next();
			int color = coloring[id(w)];
			if (color > 0)
				result.remove(color);
		}
		return result;
	}

	public boolean isLegal(Turn turn) {
		if (turn != null)
			if (turn.color > 0 && turn.color <= numColors)
				if (coloring[id(turn.v)] == 0)
					if (legalColors(turn.v).contains(turn.color))
						return true;
		return false;
	}

	public boolean allNodesLegal() {
		for (Node v : graph)
			if (coloring[id(v)] == 0)
				if (legalColors(v).isEmpty())
					return false;
		return true;
	}

	public boolean colored() {
		for (Node v : graph)
			if (coloring[id(v)] == 0)
				return false;
		return true;
	}

	public int id(Node v) {
		return Integer.parseInt(v.getId());
	}

}

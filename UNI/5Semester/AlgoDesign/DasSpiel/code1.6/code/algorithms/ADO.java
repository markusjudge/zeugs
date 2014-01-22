/**
 * @author Max Bannach <bannach@informatik.uni-luebeck.de>
 */
package algorithms;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * The ADO (adjacency degree ordering) algorithm works like the Welsh-Powell algorithm,
 * but instead of ordering the vertices ones after degree, they will be
 * ordered at every round after their adjacency degree.
 * 
 * The adjacency degree of a vertex is defined as the number of colors
 * in the vertex neighborhood.
 */
public class ADO extends Algorithm {

	private int n;
	private int[] adjacencyDegree;
	
	public ADO(Graph G, int colors) {
		super(G, colors);
		n = G.getNodeSet().size();
		adjacencyDegree = new int[n];
	}

	@Override
	public Node getNextVertex() {
		sortAdjacencyDegree();
		Node v = V.poll();;
		while (getColorForVertex(v)  == 0 && !V.isEmpty()) {
			v = V.poll();
		}
		return v;
	}

	@Override
	public int getColorForVertex(Node v) {
		return colorList.get(id(v)).isEmpty() ? 0 : colorList.get(id(v)).get(0);
	}
	
	/** Sort vertices after adjacency degree */
	private void sortAdjacencyDegree() {
		for (Node v : G) {
			int numberOfColors = 0;
			for (Iterator<Node> iter = v.getNeighborNodeIterator(); iter.hasNext();) {
				numberOfColors += coloring[id(iter.next())] == 0 ? 1 : 0;
		    }
			adjacencyDegree[id(v)] = numberOfColors;
		}
		
		Collections.sort(V, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				if (adjacencyDegree[id(o1)] < adjacencyDegree[id(o2)]) return 1;
				if (adjacencyDegree[id(o1)] > adjacencyDegree[id(o2)]) return -1;
				return id(o1) < id(o2) ? 1 : -1;
			}
		});
	}	

}

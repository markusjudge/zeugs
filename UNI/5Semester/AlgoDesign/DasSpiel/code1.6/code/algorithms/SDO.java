/**
 * @author Max Bannach <bannach@informatik.uni-luebeck.de>
 */
package algorithms;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * The SDO (saturation degree ordering) algorithm works like the Welsh-Powell algorithm,
 * but instead of ordering the vertices ones after  degree, they will be
 * ordered at every round after their saturation degree.
 * 
 * The saturation degree of a vertex is defined as the number of distinct colors
 * in the vertex neighborhood.
 */
public class SDO extends Algorithm {

	private int n;
	private int[] saturationDegree;
	
	public SDO(Graph G, int colors) {
		super(G, colors);
		n = G.getNodeSet().size();
		saturationDegree = new int[n];
	}

	@Override
	public Node getNextVertex() {
		sortSaturationDegree();
		Node v = V.poll();;
		while (getColorForVertex(v) == 0 && !V.isEmpty()) {
			v = V.poll();
		}
		return v;
	}

	@Override
	public int getColorForVertex(Node v) {
		return colorList.get(id(v)).isEmpty() ? 0 : colorList.get(id(v)).get(0);
	}
	
    /** Sort vertices after saturation degree */
	private void sortSaturationDegree() {
		for (Node v : G) {
			Set<Integer> numberOfColors = new TreeSet<Integer>();
			for (Iterator<Node> iter = v.getNeighborNodeIterator(); iter.hasNext();) {
				numberOfColors.add(coloring[id(iter.next())]);
		    }
			saturationDegree[id(v)] = numberOfColors.size();
		}

		Collections.sort(V, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				if (saturationDegree[id(o1)] < saturationDegree[id(o2)]) return 1;
				if (saturationDegree[id(o1)] > saturationDegree[id(o2)]) return -1;
				return id(o1) < id(o2) ? 1 : -1;
			}
		});
	}	
}
/**
 * @author Max Bannach <bannach@informatik.uni-luebeck.de>
 */
package algorithms;

import java.util.Collections;
import java.util.Comparator;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * The famous Welsh Powell algorithm.
 * Every vertex get a list of possible colors,
 * in the same order each.
 * Vertices are ordered by their degree. 
 * Vertex with highest degree which is not colored will be chosen 
 * and colored with the first color in his color list,
 * this color will be removed from the color list of all his neighbors.
 * 
 * This algorithm has to advantages: because we color
 * always the vertex with highest degree, we hope that
 * the vertices with low degree can be colored easier later with less colors;
 * and because all vertices have the same color edge, we use one color as long 
 * as possible and can hope, that we have colors left for the late game. 
 */
public class WelshPowell extends Algorithm {

	public WelshPowell(Graph G, int colors) {
		super(G, colors);
		
		// we sort the the vertex set after degree, so we will color high degree vertices first
		Collections.sort(V, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				if (o1.getDegree() < o2.getDegree()) return 1;
				if (o1.getDegree() > o2.getDegree()) return -1;
				return id(o1) < id(o2) ? 1 : -1;
			}
		});	
	}

	@Override
	public int getColorForVertex(Node v) {
		return colorList.get(id(v)).isEmpty() ? 0 : colorList.get(id(v)).get(0);
	}

	@Override
	public Node getNextVertex() {
		Node v = V.poll();;
		while (getColorForVertex(v)  == 0 && !V.isEmpty()) {
			v = V.poll();
		}
		return v;
	}

}

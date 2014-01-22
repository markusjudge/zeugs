/**
 * Algorithm for the coloring of a graph in the coloring construction game.
 * 
 * @author Max Bannach <bannach@informatik.uni-luebeck.de>
 * @date 04.11.2013
 */

package algorithms;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graphstream.graph.*;

/**
 * This class provides the abstraction of an algorithm that can color a graph
 * sequentially.
 *
 * Such a algorithm has to remember the coloring and
 * has to provides methods to get a vertex which should be colored
 * next as well as a legal color for such a vertex.
 *
 */
public abstract class Algorithm {

	/** The graph. */
	protected Graph G;
	
	/** Vertex set of the graph. */
    LinkedList<Node> V;
	
	/** The coloring of the graph. */
    protected int[] coloring;
	
	/**
     * The color list holds list of integers for every vertex in the graph.
     * This list than describes all possible colors for this vertex.
     */
    protected HashMap<Integer, List<Integer>> colorList;
	
	/**
	 * Constructor, an algorithm needs a graph and the number of colors he can use.
	 * @param G
	 * @param numberOfColorss
	 */
	public Algorithm(Graph G, int colors) {
		this.G              = G;
		this.V              = new LinkedList<Node>();
		this.coloring       = new int[G.getNodeSet().size()];
		this.colorList      = new HashMap<Integer, List<Integer>>();
		for (Node v : G.getNodeSet()) {
			V.add(v);
			colorList.put(id(v), new ArrayList<Integer>(colors));
			// we add colors in order to the a list of possible colors for every vertex
			for (int i = 1; i <= colors; i++) {
				colorList.get(id(v)).add(i);
			}
		}
	}
	
	/** Returns the next node that should be colored. */
	public abstract Node getNextVertex();
	
	/**
	 * Get the next possible color for the given vertex.
	 * "Next" here means in the color ordering of the algorithm.
	 * @param v - the vertex
	 * @return -1 if the vertex is not colorable and the color as int else
	 */
	public abstract int getColorForVertex(Node v);
	
	/**
	 * Color the given vertex with the given color.
	 * This methods has to do all calls to make sure,
	 * that the neighborhood of v can not be colored with the color anymore.
	 * @param v     - the vertex
	 * @param color - the color
	 */
	public void colorVertex(Node v, int color) {
		coloring[id(v)] = color;
    	for (Iterator<Node> iter = v.getNeighborNodeIterator(); iter.hasNext();) {
	    	strikeColorForVertex(iter.next(), color);
	    }
    	V.remove(v); // we do not this anymore
	}
	
	/** Removes the given color from the list of possible colors for the vertex */
    protected void strikeColorForVertex(Node v, int color) {
    	colorList.get(id(v)).remove((Integer) color);
    }
	
	/** Get a integer for a vertex. */
    public int id(Node v){
        return Integer.parseInt(v.getId());
    }
	
}

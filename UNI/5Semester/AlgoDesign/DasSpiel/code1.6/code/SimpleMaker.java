/**
 * A simple maker which chooses a random legal turn
 *
 * @author Sebastian Berndt <berndt@tcs.uni-luebeck.de>
 */

import org.graphstream.graph.*;
import java.util.HashSet;
import java.util.Iterator;

public class SimpleMaker extends Maker{

    /**
     * The coloring of the nodes
     */
    public int[] coloring;
    
    /**
     * The number of colors
     */
    int colors;
    
    /**
     * The graph to play on
     */
    Graph g;

    /**
     * Instantiate the Maker
     *
     * @param g the graph to play on
     * @param colors the number of colors
     * @param time the time allowed to make a turn
     */
    public SimpleMaker(Graph g, int colors, int time){
        super(g,colors,time);
        this.colors=colors;
        this.g=g;
        int n = 0;
        for (Node v: g){
            n++;
        }
        coloring = new int[n];
    }
    
    /**
     * Simply returns a valid turn
     *
     * @param t the last turn of the opponent
     * @return a valid turn
     */
    public Turn getNextTurn(Turn t){
        // execute the last turn
        if(t != null){
            coloring[id(t.v)] = t.color;
        }

        // search for a remaining legal turn
        for(Node v: g){
            // are there legal turns for node v?
            if (coloring[id(v)] == 0){
                HashSet<Integer> res = legalColors(v);
                if(!res.isEmpty()){
                    int c = res.iterator().next();
                    // execute the turn
                    coloring[id(v)] = c;
                    return new Turn(v,c);
                }
            }
        }
        return null;
    }

        /**
     * Return all legal colors which are allowed for node v
     *
     * @param v the node to check for
     * @return a set of all possible colors to color v
     */
    public HashSet<Integer> legalColors(Node v){
        HashSet<Integer> result = new HashSet<>();
       
        // add all possible colors
        for(int i = 1; i<= colors; i++){
            result.add(i);
        }
        
        Iterator<? extends Node> k = v.getNeighborNodeIterator();

        // test the colors of the neighbours and remove them from the set
        while(k.hasNext()){
            Node w = k.next();
            int cw=coloring[id(w)];
            if(cw > 0){
                result.remove(cw);
            }
        }
        return result;
    }

    /**
     * Returns the id of a node
     * 
     * @param v the node
     * @return the id of node v
     */
    public int id(Node v){
        return Integer.parseInt(v.getId());
    }




}

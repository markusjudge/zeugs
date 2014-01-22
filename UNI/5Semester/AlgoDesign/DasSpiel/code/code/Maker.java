/** 
 * An abstract class representing a maker
 * @author Sebastian Berndt <berndt@tcs.uni-luebeck.de>
 */

import org.graphstream.graph.*;

public abstract class Maker extends Player{
    
    /**
     * Instantiate a maker
     *
     * @param g the graph to play on
     * @param colors the number of colors to color the graph
     * @param time the time allowed to make a turn
     */
    public Maker(Graph g, int colors, int time){
        super(g,colors,time);
    }





}

/**
 * An abstract class representing a breaker   
 * @author Sebastian Berndt <berndt@tcs.uni-luebeck.de>
 */

import org.graphstream.graph.*;

public abstract class Breaker extends Player{
    
    /**
     * Instantiate a breaker
     *
     * @param g the graph to play on
     * @param colors the number of colors to color the graph
     * @param time the time allowed to make a turn
     */
    public Breaker(Graph g, int colors, int time){
        super(g,colors,time);
    }





}

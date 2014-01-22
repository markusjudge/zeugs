/**
 * An abstract class representing a player of the game
 * 
 * @author Sebastian Berndt <berndt@tcs.uni-luebeck.de>
 */


import org.graphstream.graph.*;

public abstract class Player{

    /**
     * The graph to play on
     */
    public Graph g;
    
    /**
     * The number of colors to color the graph
     */
    public int colors;
    
    /**
     * The time allowed to make a turn
     */
    public int time;
    
    /**
     * Instantiate a player
     *
     * @param g the graph to play on
     * @param colors the number of colors to color the graph
     * @param time the time allowed to make a turn
     */
    public Player(Graph g, int colors, int time){
        this.g = g;
        this.colors = colors;
        this.time = time;
    }

    /**
     * Compute the next turn
     * 
     * @param t the last turn taken by the opponent
     * @return the next turn
     */
    public abstract Turn getNextTurn(Turn t);
    

}

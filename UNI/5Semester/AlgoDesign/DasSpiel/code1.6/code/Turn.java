/**
 * A class representing a turn in the game
 *
 * @author Sebastian Berndt <berndt@tcs.uni-luebeck.de>
 */

import org.graphstream.graph.*;

public class Turn{

    /**
     * The node on which to act
     */
    public Node v;
    
    /**
     * The color to color the node
     */
    public int color; 

    /**
     * Instantiate the turn
     * 
     * @param v the node to act on
     * @param color the color to color the node
     */
    public Turn(Node v, int color){
        this.v = v;
        this.color = color;
    }

}

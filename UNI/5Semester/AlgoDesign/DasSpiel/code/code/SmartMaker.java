
/**
 * @author Max Bannach <bannach@uni-luebeck.de>
 */

import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import algorithms.ADO;
import algorithms.Algorithm;
import algorithms.SDO;
import algorithms.WelshPowell;

/**
 * This makers rolls at the beginning of the game the dices an choose one
 * of the given strategies. Then the maker just asks every turn this strategy
 * which vertex he should color.
 * 
 * Currently the following strategies are used:
 *  - Welsh Powell
 *  - SDO
 *  - ADO
 */
public class SmartMaker extends Maker {

	/** The algorithm that will be used to color the graph. */
    private Algorithm strategy;
    
    /** construct */
    public SmartMaker(Graph g, int colors, int time) {
		super(g, colors, time);
		
		// roll the dices and take a strategy
		Random rand = new Random();
		switch (rand.nextInt(3)) {
		case 0:
			System.out.println("WelshPowell");
			strategy = new WelshPowell(g, colors);
			break;
		case 1:
			System.out.println("SDO");
			strategy = new SDO(g, colors);
			break;
		case 2:
			System.out.println("ADO");
			strategy = new ADO(g, colors);
			break;
		}
    }

    /**
     * Execute the last turn and calculate a new one.
     * What this method does depends on the chosen algorithm.
     */
    public Turn getNextTurn(Turn t) {
		if (t != null) {
			strategy.colorVertex(t.v, t.color);
		}
		Node v = strategy.getNextVertex();
		int c  = strategy.getColorForVertex(v);
		strategy.colorVertex(v, c);
		return new Turn(v, c);
    }
}


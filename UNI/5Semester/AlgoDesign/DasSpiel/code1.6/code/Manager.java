/**
 * The simple form of the tournament Manager which plays a game between
 * a  Maker and a Breaker.
 * 
 * The command line arguments are a lower and an upper bound on the
 * number of nodes and the corresponding classes which should play the
 * game.
 *
 *
 * @author Sebastian Berndt <berndt@tcs.uni-luebeck.de>
 */

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swingViewer.*;
import org.graphstream.algorithm.generator.*;
import java.util.HashSet;
import java.util.Iterator;
import java.lang.reflect.Constructor;

public class Manager{
    
    /**
     * Reads the command line arguments and parses them
     */
    public static void main(String args[]) throws Exception{
        if(args.length< 5){
            System.out.println("Usage: java Manager lowerBound upperBound makerClass breakerClass time");
            System.exit(0);
        }
        int lower = Integer.parseInt(args[0]);
        int upper = Integer.parseInt(args[1]);
        Class cmaker = Class.forName(args[2]);
        Class cbreaker = Class.forName(args[3]);
        int time = Integer.parseInt(args[4]);
        Manager m = new Manager(lower,upper,cmaker,cbreaker,time);
    }
    
    /**
     * The graph on which to play
     */
    Graph g;
    
    /**
     * The coloring of the nodes
     */
    int[] coloring;
    
    /**
     * The number of colors
     */
    int colors;
    
    /** 
     * The  Maker Player
     */
    Maker maker;

    /** 
     * The Breaker Player
     */
    Breaker breaker;
    
    /**
     * An array containing the colors to display in the graph
     */
    String[] colormap;
    
    /**
     * the time in milliseconds allowed for a turn
     */
    int time;

    /**
     * Creates a manager instance
     *
     * @param lower a lower bound on the number of nodes
     * @param upper an upper bound on the number of nodes
     * @param cmaker the class of the Maker
     * @param cbreaker the class of the Breaker
     * @param time the time allowed for a turn
     *
     */
    public Manager(int lower, int upper, Class<?> cmaker, Class<?> cbreaker, int time) throws Exception{
        
        
        this.time = time;
        
        // create random Graph 
        g = new SingleGraph("Random");
        int n =  getBoundedRandom(lower,upper);
        double prob = Math.random();
        System.out.println("P: "+prob);
        

        
        // add the nodes
        for(int i = 0; i < n; i++){
            g.addNode(""+i);
        }
        
        // randomly add the edges with probability prob
        for(int i = 0; i < n; i++){
            for(int j = i+1; j < n; j++){
                double act = Math.random();
                if (act >= prob){
                    g.addEdge(""+i+""+j,""+i,""+j);
                }
            }
        }

        // setup the display of the graph
        Viewer viewer = g.display();
        ViewerPipe fromViewer = viewer.newViewerPipe();

        // compute the maximal degree in the graph
        int maxdegree = 0;
        for(Node v: g){
            if (v.getDegree() > maxdegree){
                maxdegree = v.getDegree();
            }
        }

        // randomly compute the number of allowed colors
        // TODO: Hiermit rumspielen
        colors = getBoundedRandom(3,maxdegree);

        // create the empty coloring
        coloring = new int [n];

        System.out.println("Number of colors: "+colors);
        

        
        // create the colors to be used in the display of the graph
        colormap = new String[colors+6];
        int val = 255;
        for(int i = 0; i < colors; i=i+6){
            colormap[i]="(0,0,"+val+")";
            colormap[i+1]="(0,"+val+",0)";
            colormap[i+2]="("+val+",0,0)";
            colormap[i+3]="(0,"+val+","+val+")";
            colormap[i+4]="("+val+",0,"+val+")";
            colormap[i+5]="("+val+","+val+",0)";
            val = val / 2;
        }
        
        Class<?> [] params = { Graph.class,int.class,int.class};

        // instantiate the Maker via dark Reflection magic
        Constructor<?> constructorMaker = cmaker.getConstructor(params);
        maker = (Maker) constructorMaker.newInstance(g,colors,time);
        
        // instantiate the Breaker via dark Reflection magic
        Constructor<?> constructorBreaker = cbreaker.getConstructor(params);
        breaker = (Breaker) constructorBreaker.newInstance(g,colors,time);
        

        int player = 1;
        Turn curr = null;
        
        // if a player took too long
        boolean tooLong = false;

        while(!colored() && allNodesLegal()){
            // the game goes on 

            Player p = getPlayer(player);
            long startTime = System.currentTimeMillis();
            curr=p.getNextTurn(curr);

            long spentTime = System.currentTimeMillis() - startTime;
            

            if(spentTime > time){
                tooLong = true;
            }


            if(!isLegal(curr) || tooLong){
                // a player violated the rules
                if(player==1){
                    System.out.println("Breaker wins!");
                    try{
                        Thread.sleep(10000000);
                    }
                    catch(Exception e){
                        System.out.println(e);
                    }
                }
                else{
                    System.out.println("Maker wins!");
                    try{
                        Thread.sleep(10000000);
                    }
                    catch(Exception e){
                        System.out.println(e);
                    }

                }
            }

            // take the turn and update the view
            coloring[id(curr.v)] = curr.color;
            curr.v.addAttribute("ui.style", "fill-color: rgb"+colormap[curr.color]+";");
            fromViewer.pump();

            try{
                Thread.sleep(1000);   
            }

            catch(Exception e){
                System.out.println(e);
            }

            player = -player;

        }

        if(colored()){
            System.out.println("Maker wins!");
        }
        if(!allNodesLegal()){
            System.out.println("Breaker wins!");
        }
    }
    

    /**
     * Return the player corresponding to a number
     *
     * @param i the number of the player, where 1 ~= maker and  -1 ~= breaker
     * @return the player corresponding to the number
     */
    public Player getPlayer(int i){
        if(i==1){
            return maker;
        }
        return breaker;
    }

    /**
     * Return a random integer uniformly distributed between two numbers
     *
     * @param lower the lower bound
     * @param upper the upper bound
     * @return a random number in the interval [lower,upper]
     */
    public int getBoundedRandom(int lower, int upper){
        if (upper < lower){
            return lower;
        }
        return (int)(Math.random() * (upper - lower) + lower);
    }
    
    /**
     * Return all colors which are still possible to color a node
     * 
     * @param v the node to color
     * @return a set containing all possible colors
     */
    public HashSet<Integer> legalColors(Node v){
        HashSet<Integer> result = new HashSet<>();
       
        // add all possible colors
        for(int i = 1; i<= colors; i++){
            result.add(i);
        }
        
        // remove all colors which are used by the neighbors
        Iterator<? extends Node> k = v.getNeighborNodeIterator();

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
     * Test whether a turn is legal
     *
     * @param turn the turn to test
     * @return true iff the turn is legal
     */
    public boolean isLegal(Turn turn){
        if(turn != null){
            if (turn.color <= colors){
                if (coloring[id(turn.v)] == 0){
                    if (legalColors(turn.v).contains(turn.color)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Test whether all uncolored node can be colored legally
     * 
     * @return true iff all uncolored nodes can be colored
     */
    public boolean allNodesLegal(){
        for(Node v: g){
            if(coloring[id(v)] == 0){
                if(legalColors(v).isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Test whether all nodes are colored
     * 
     * @return true iff all nodes are colored
     */
    public boolean colored(){
        for(Node v: g){
            if(coloring[id(v)] == 0){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns the id of the node
     * 
     * @param v the node 
     * @return the id of the node
     */
    public int id(Node v){
        return Integer.parseInt(v.getId());
    }

}

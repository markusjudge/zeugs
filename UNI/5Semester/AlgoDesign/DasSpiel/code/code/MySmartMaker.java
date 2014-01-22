import java.util.Collection;
import java.util.LinkedList;

/**
 * This class implements a computer player that plays board games by using the
 * minimax algorithm to 'minimize the maximum possible loss' of the player. This
 * implementation of the minimax algorithm is based on the following references:
 * 
 * "Data structures and algorithms - Game trees, alpha-beta search"
 * http://www.cs.mcgill.ca/~cs251/OldCourses/1997/topic11/z
 * 
 * "AI Horizon: MinimaxPlayer game tree programming"
 * http://www.aihorizon.com/essays/basiccs/trees/minimax2.htm
 * 
 * Pseudocode for alpha-beta pruning in conjunction with minimax
 * http://en.wikipedia.org/wiki/Alpha-beta_pruning#Pseudocode
 */
public class MinimaxAI extends AI {

	protected static final int MIN = Integer.MIN_VALUE;
	protected static final int AVG = 0;
	protected static final int MAX = Integer.MAX_VALUE;

	/**
	 * Create a player with the given nickname.
	 * 
	 * @param name The nickname for the new player.
	 */
	public MinimaxAI(String name) {
		super(name);
	}

	/**
	 * Choose the best next move using the minimax algorithm.
	 */
	@Override public Move getMove(GameModel board) {
		int maxValue = MIN;
		Node root = new Node(board);
		Node bestNode = null;
		Collection<Node> nodes = root.getChildren();
		int index = 1;
		for (Node child : nodes) {
			// System.out.println("Evaluating branch " + (index++) + " of " + nodes.size());
			int value = minimax(child, true, MIN, MAX, 5);
			if (value >= maxValue) {
				maxValue = value;
				bestNode = child;
			}
		}
		return (bestNode != null) ? bestNode.move : null;
	}

	/**
	 * An implementation of the mimimax algorithm that uses alpha-beta pruning
	 * to keep the game tree small enough.
	 * 
	 * @param node The node in the game tree from which to start the search.
	 * @param min True to minimize the maximum possible loss of the current
	 *            player, false to maximize the minimum possible loss of the
	 *            other player. This value changes on each recursion.
	 * @param alpha
	 * @param beta
	 * @return
	 */
	private int minimax(Node node, boolean min, int alpha, int beta, int depth) {
		if (node.isLeaf() || depth == 0) return node.getValue(getName());
		for (Node child : node.getChildren()) {
			int value = minimax(child, !min, alpha, beta, depth - 1);
			if (min) beta = Math.min(beta, value);
			else alpha = Math.max(alpha, value);
			if (alpha > beta) break; // beta cut-off
		}
		return min ? beta : alpha;
	}

	@Override public String toString() {
		return name + " (Computer)";
	}

	protected class Node {

		public final GameModel board;
		public final Move move;

		/**
		 * Create the root node.
		 */
		public Node(GameModel board) {
			this.board = (GameModel) board.clone();
			this.move = null;
		}

		/**
		 * Create a node that branches from the given board by applying the
		 * given move to a copy of the board.
		 * 
		 * @param board
		 * @param move
		 */
		public Node(GameModel board, Move move) {
			this.board = (GameModel) board.clone();
			this.board.setMove(this.move = move);
		}

		/**
		 * Get the nodes that are a direct branch from this one.
		 * 
		 * @return A collection of zero or more nodes.
		 */
		public Collection<Node> getChildren() {
			Collection<Node> nodes = new LinkedList<Node>();
			for (Move move : board.getMoves()) {
				// System.out.println("MinimaxAI: Got valid from from model: " + move);
				nodes.add(new Node(board, move));
			}
			return nodes;
		}

		/**
		 * Check if this node is a leaf on the game tree.
		 */
		public boolean isLeaf() {
			return board.getWinner() != null || board.isDraw();
		}

		/**
		 * Get the value of this node to the given player.
		 */
		public int getValue(String player) {
			String winner = board.getWinner();
			// if the player won this node has positive value
			if (player.equals(winner)) return MAX;
			// if the player lost this node has negative value
			if (winner != null) return MIN;
			// if the game ended in a draw this node has no value
			return 0;
		}

	}

}

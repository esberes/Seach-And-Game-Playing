import java.util.ArrayList;
import java.util.List;

public class AlphaBetaPruning {
    private int bestMove;
    private double bestOverallValue;
    private int numNodesVisited;
    private int numNodesEvaluated;
    private int maxDepthReached;
    private double branchingFactor;
    private int maxDepth;
    private double numInternalNodes;
    private double bestCurrentValue;
    ArrayList<Double> valueList;
    ArrayList<Integer> moveList;

    /**
     * Constructor for AlphaBetaPruning class
     *
     */
    public AlphaBetaPruning() {
        bestMove = 10000000;
        bestOverallValue = 0.0;
        numNodesVisited = 0;
        numNodesEvaluated = 0;
        maxDepthReached = 0;
        branchingFactor = 0.0;
        maxDepth = 0;
        numInternalNodes = 0;
        bestCurrentValue = 0.0;
        valueList = new ArrayList();
        moveList = new ArrayList();
    }

    /**
     * This function will print out the information to the terminal, as specified in
     * the homework description.
     */
    public void printStats() {
        System.out.println("Move: " + this.bestMove);
        System.out.println("Value: " + this.bestOverallValue);
        System.out.println("Number of Nodes Visited: " + this.numNodesVisited);
        System.out.println("Number of Nodes Evaluated: " + this.numNodesEvaluated);
        System.out.println("Max Depth Reached: " + (this.maxDepthReached - 1));
        // calc for average branching factor
        this.numInternalNodes = this.numNodesVisited - this.numNodesEvaluated;
        this.branchingFactor = (this.numNodesVisited - 1) / this.numInternalNodes;
        System.out.printf("Avg Effective Branching Factor: %.1f",  this.branchingFactor);
    }

    /**
     * This function will start the alpha-beta search
     * 
     * @param state This is the current game state
     * @param depth This is the specified search depth
     */
    public void run(GameState state, int depth) {

        // stop search at this depth
        this.maxDepth = depth;

        // determine current player
        boolean isMaxTurn = state.isMaxTurn();
        alphabeta(state, this.maxDepthReached, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, isMaxTurn);
    }

    /**
     * This method is used to implement alpha-beta pruning for both 2 players
     * 
     * @param state     This is the current game state
     * @param depth     Current depth of search
     * @param alpha     Current Alpha value
     * @param beta      Current Beta value
     * @param maxPlayer True if player is Max Player; Otherwise, false
     * @return double This is the number indicating score of the best next move
     */
    private double alphabeta(GameState state, int depth, double alpha, double beta, boolean maxPlayer) {

        double v; // value returned

        // call to get value based on type of player
        if (maxPlayer) {
            v = getMax(state, depth, alpha, beta);
        } else {
            v = getMin(state, depth, alpha, beta);
        }
        this.bestOverallValue = v;

        ArrayList<Double> newValueList = new ArrayList();
        ArrayList<Integer> newMoveList = new ArrayList();

        for (int i = 0; i < valueList.size(); ++i) {
            if (valueList.get(i) == this.bestOverallValue) {
                newValueList.add(valueList.get(i));
                newMoveList.add(moveList.get(i));
            }
        }
        for (int i = 0; i < newValueList.size(); ++i) {
            if (newMoveList.get(i) < this.bestMove) {
                this.bestMove = newMoveList.get(i);
            }
        }

        return v;
    }

    /**
     * This method is used to find the Max value
     * 
     * @param state This is the current game state
     * @param depth Current depth of search
     * @param alpha Current Alpha value
     * @param beta  Current Beta value
     * @return double This is the number indicating score of the best next move
     */
    private double getMax(GameState state, int currentDepth, double alpha, double beta) {
        double value; // value to be returned
        this.numNodesVisited++; // update number of nodes visited

        // if max depth is reached or terminal state, return value
        if (currentDepth++ == this.maxDepth || state.getSuccessors().isEmpty()) {
            if (currentDepth > this.maxDepthReached) {
                this.maxDepthReached = currentDepth;
            }

            this.numNodesEvaluated++;
            return state.evaluate();
        }
        value = Double.NEGATIVE_INFINITY;

        List<GameState> successors = new ArrayList<GameState>();
        successors = state.getSuccessors();

        // iterate through successors to find best
        for (int i = 0; i < successors.size(); ++i) {
            double newVal = getMin(successors.get(i), currentDepth, alpha, beta);
            value = Math.max(value, newVal);
            if (currentDepth == 1) {
                valueList.add(value);
                moveList.add(successors.get(i).getLastMove());
            }
            if (value >= beta) {
                return value; // prune remaining children -- do not keep searching
            }
            alpha = Math.max(alpha, value);
        }
        return value; // return best value of child
    }

    /**
     * This method is used to find the Min value
     * 
     * @param state This is the current game state
     * @param depth Current depth of search
     * @param alpha Current Alpha value
     * @param beta  Current Beta value
     * @return double This is the number indicating score of the best next move
     */
    private double getMin(GameState state, int currentDepth, double alpha, double beta) {
        double value; // value to be returned
        this.numNodesVisited++; // update number of nodes visited

        // if max depth is reached or terminal state, return value
        if (currentDepth++ == this.maxDepth || state.getSuccessors().isEmpty()) {
            if (currentDepth > this.maxDepthReached) {
                this.maxDepthReached = currentDepth;
            }

            this.numNodesEvaluated++;
            return state.evaluate();
        }

        value = Double.POSITIVE_INFINITY;

        List<GameState> successors = new ArrayList<GameState>();
        successors = state.getSuccessors();

        // iterate through successors to find best
        for (int i = 0; i < successors.size(); ++i) {
            double newVal = getMax(successors.get(i), currentDepth, alpha, beta);
            value = Math.min(value, newVal);
            if (currentDepth == 1) {
                valueList.add(value);
                moveList.add(successors.get(i).getLastMove());
            }
            if (value <= alpha) {
                return value; // prune remaining children -- do not keep searching
            }
            beta = Math.min(beta, value);
        }
        return value; // return best value of child
    }
}

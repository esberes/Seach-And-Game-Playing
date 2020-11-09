import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {
    private int size; // The number of stones
    public boolean[] stones; // Game state: true for available stones, false for taken ones
    private int lastMove; // The last move

    /**
     * Class constructor specifying the number of stones.
     */
    public GameState(int size) {

        this.size = size;

        // For convenience, we use 1-based index, and set 0 to be unavailable
        this.stones = new boolean[this.size + 1];
        this.stones[0] = false;

        // Set default state of stones to available
        for (int i = 1; i <= this.size; ++i) {
            this.stones[i] = true;
        }

        // Set the last move be -1
        this.lastMove = -1;
    }

    /**
     * Copy constructor
     */
    public GameState(GameState other) {
        this.size = other.size;
        this.stones = Arrays.copyOf(other.stones, other.stones.length);
        this.lastMove = other.lastMove;
    }

    /**
     * This method is used to compute a list of legal moves
     *
     * @return This is the list of state's moves
     */
    public List<Integer> getMoves() {

        // list of possible moves for a state
        ArrayList<Integer> possibleMoves = new ArrayList<>();

        // determine if first move has been made
        boolean firstMove = true;
        for (int i = 1; i < this.stones.length; ++i) {
            if (this.stones[i] == false) {
                firstMove = false;
                break;
            }
        }

        // iterate through stones to determine if possible move
        for (int i = 1; i < this.stones.length; ++i) {

            // first move has not been made
            if (firstMove) {
                // must be odd number less than half the number of stones
                if ((i % 2) == 1 && i < (this.size / 2.0)) {
                    possibleMoves.add(i);
                }
                // first move has been made
            } else {
                // must be factor or multiple and not already taken out
                if ((i % this.lastMove == 0 || this.lastMove % i == 0) && this.stones[i] == true) {
                    possibleMoves.add(i);
                }
            }
        }

        return possibleMoves;
    }

    /**
     * This method is used to generate a list of successors using the getMoves()
     * method
     *
     * @return This is the list of state's successors
     */
    public List<GameState> getSuccessors() {
        return this.getMoves().stream().map(move -> {
            var state = new GameState(this);
            state.removeStone(move);
            return state;
        }).collect(Collectors.toList());
    }

    /**
     * This method is used to evaluate a game state based on the given heuristic
     * function
     *
     * @return int This is the static score of given state
     */
    public double evaluate() {

        // MIN WIN
        if (isMaxTurn() && getMoves().isEmpty()) {
            return -1.0;
        }
        // MAX WIN
        else if (isMaxTurn() == false && getMoves().isEmpty()) {
            return 1.0;
        }

        // NO WIN -- STILL PLAYING
        else {
            // MAX TURN -- still playing
            if (isMaxTurn()) {
                // stone 1 has not been taken
                if (this.stones[1] == true) {
                    return 0;
                }
                // last move was a 1
                if (this.lastMove == 1) {
                    if (getMoves().size() % 2 == 1) {
                        return 0.5;
                    } else {
                        return -0.5;
                    }
                }
                // last move was prime
                if (Helper.isPrime(this.lastMove)) {
                    int numSuccessors = 0;
                    List<Integer> possSuccessors = getMoves();
                    for (int i = 0; i < possSuccessors.size(); ++i) {
                        if (possSuccessors.get(i) % this.lastMove == 0) {
                            numSuccessors++;
                        }
                    }
                    if (numSuccessors % 2 == 1) {
                        return 0.7;
                    } else {
                        return -0.7;
                    }
                }
                // last move was composite
                if (Helper.isPrime(this.lastMove) == false) {
                    int largestPrime = Helper.getLargestPrimeFactor(this.lastMove);
                    int numSucessors = 0;
                    List<Integer> possSuccessors = getMoves();
                    for (int i = 0; i < possSuccessors.size(); ++i) {
                        if (possSuccessors.get(i) % largestPrime == 0) {
                            numSucessors++;
                        }
                    }
                    if (numSucessors % 2 == 1) {
                        return 0.6;
                    } else {
                        return -0.6;
                    }
                }
                // MIN TURN -- still playing
            } else {
                // stone 1 has not been taken
                if (this.stones[1] == true) {
                    return 0;
                }
                // last move was a 1
                if (this.lastMove == 1) {
                    if (getMoves().size() % 2 == 1) {
                        return -0.5;
                    } else {
                        return 0.5;
                    }
                }
                // last move was prime
                if (Helper.isPrime(this.lastMove)) {
                    int numSuccessors = 0;
                    List<Integer> possSuccessors = getMoves();
                    for (int i = 0; i < possSuccessors.size(); ++i) {
                        if (possSuccessors.get(i) % this.lastMove == 0) {
                            numSuccessors++;
                        }
                    }
                    if (numSuccessors % 2 == 1) {
                        return -0.7;
                    } else {
                        return 0.7;
                    }
                }
                // last move was composite
                if (Helper.isPrime(this.lastMove) == false) {
                    int largestPrime = Helper.getLargestPrimeFactor(this.lastMove);
                    int numSucessors = 0;
                    List<Integer> possSuccessors = getMoves();
                    for (int i = 0; i < possSuccessors.size(); ++i) {
                        if (possSuccessors.get(i) % largestPrime == 0) {
                            numSucessors++;
                        }
                    }
                    if (numSucessors % 2 == 1) {
                        return -0.6;
                    } else {
                        return 0.6;
                    }
                }
            }
        }
        return 0.0;
    }

    /**
     * This method is used to take a stone out
     *
     * @param idx Index of the taken stone
     */
    public void removeStone(int idx) {
        this.stones[idx] = false;
        this.lastMove = idx;
    }

    /**
     * These are get/set methods for a stone
     *
     * @param idx Index of the taken stone
     */
    public void setStone(int idx) {
        this.stones[idx] = true;
    }

    public boolean getStone(int idx) {
        return this.stones[idx];
    }

    /**
     * These are get/set methods for lastMove variable
     *
     * @param move Index of the taken stone
     */
    public void setLastMove(int move) {
        this.lastMove = move;
    }

    public int getLastMove() {
        return this.lastMove;
    }

    /**
     * This is get method for game size
     *
     * @return int the number of stones
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Determine if it is MAX or MIN turn
     *
     * @return true if Max, false if Min
     */
    public boolean isMaxTurn() {

        int numStonesTaken = 0;
        for (int i = 1; i < this.stones.length; ++i) {
            if (this.stones[i] == false) {
                numStonesTaken++;
            }
        }
        if (numStonesTaken % 2 == 0 || numStonesTaken == 0) {
            return true;
        }
        return false;
    }

}

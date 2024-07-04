import java.util.*;

public class Node {
    private Node parent;
    private List<Node> children;
    private Game gameState;
    private int visits;
    private double reward;
    private int wins;
    private boolean player;

    public Node(Game gameState, Node parent, boolean player) {
        this.gameState = gameState;
        this.parent = parent;
        this.player = player;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.reward = 0;
        this.wins = 0;
    }

    public Node select() {
        return children.stream()
                .max(Comparator.comparing(this::uctValue))
                .orElseThrow();
    }

    private double uctValue(Node node) {
        if (node.visits == 0) {
            return Double.MAX_VALUE;
        }
        return node.reward / node.visits +
                1.8 * Math.sqrt(2 * Math.log(this.visits) / node.visits);
    }

    public void expand() {
        for (int col = 0; col < 7; col++) {
            if (gameState.load_free_spaces()[col] >= 0) {
                Game newGameState = new Game();
                copyGameState(newGameState, gameState);
                newGameState.makeMove(col);
                children.add(new Node(newGameState, this, -player));
            }
        }
    }

    public double simulate() {
        Game simGameState = new Game();
        copyGameState(simGameState, gameState);
        Random random = new Random();
        int currentPlayer = player;

        while (simGameState.isGameRunning()) {
            int[] freeCols = simGameState.load_free_spaces();
            ArrayList<Integer> availableMoves = new ArrayList<>();
            for (int col = 0; col < 7; col++) {
                if (freeCols[col] >= 0) {
                    availableMoves.add(col);
                }
            }
            int move = availableMoves.get(random.nextInt(availableMoves.size()));
            simGameState.makeMove(move);
            currentPlayer = -currentPlayer; // Switch player after move
        }
        if (simGameState.hasPlayerWon(simGameState.load_p1_grid())) {
            return this.player == 1 ? 1 : -1;
        } else if (simGameState.hasPlayerWon(simGameState.load_p2_grid())) {
            return this.player == -1 ? 1 : -1;
        } else {
            return 0;
        }
    }

    public void backpropagate(double result) {
        Node node = this;
        while (node != null) {
            node.visits++;
            node.reward += result;
            if ((result == 1 && node.player == this.player) || (result == -1 && node.player != this.player)) {
                node.wins++;
            }
            result = -result;
            node = node.parent;
        }
    }

    public Game getGameState() {
        return gameState;
    }

    public int getVisits() {
        return visits;
    }

    public int getWins() {
        return wins;
    }

    private void copyGameState(Game target, Game source) {
        boolean[][] p1Grid = new boolean[6][7];
        boolean[][] p2Grid = new boolean[6][7];
        int[] freeSpaceCols = new int[7];
        String gameKey = source.load_gameKey();
        boolean isP1Human = source.isP1Human;
        boolean isP2Human = source.isP2Human;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                p1Grid[i][j] = source.load_p1_grid()[i][j];
                p2Grid[i][j] = source.load_p2_grid()[i][j];
            }
        }

        for (int i = 0; i < 7; i++) {
            freeSpaceCols[i] = source.load_free_spaces()[i];
        }

        target.store_p1_grid(p1Grid, freeSpaceCols);
        target.store_p2_grid(p2Grid, freeSpaceCols);
        target.setPlayerModes(isP1Human ? "Human" : "MCTS", isP2Human ? "Human" : "MCTS");
        target.setGameKey(gameKey);
    }

    public List<Node> getChildren() {
        return children;
    }

    public double getReward() {
        return reward;
    }
}

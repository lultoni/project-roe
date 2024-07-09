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
        Player p = gameState.getPlayer(player);
        for (Turn turn: gameState.generatePossibleTurns(p)) {
            Game newGameState = new Game(gameState.getPlayer(false).getIsHuman(), gameState.getPlayer(true).getIsHuman());
            copyGameState(newGameState, gameState);
            p = newGameState.getPlayer(player);
            newGameState.executeTurn(turn, p, null);
            children.add(new Node(newGameState, this, !player));
        }
    }

    public double simulate() {
        Game simGameState = new Game(gameState.getPlayer(false).getIsHuman(), gameState.getPlayer(true).getIsHuman());
        copyGameState(simGameState, gameState);
        Random random = new Random();
        boolean currentPlayer = player;

        while (simGameState.isGameOver() == 2) {
            Player p = simGameState.getPlayer(currentPlayer);
            List<Turn> possibleTurns = simGameState.generatePossibleTurns(p);

            if (!possibleTurns.isEmpty()) {
                Turn turn = possibleTurns.get(random.nextInt(possibleTurns.size()));
                simGameState.executeTurn(turn, p, null);
            }

            currentPlayer = !currentPlayer; // Switch player after move
        }

        if (simGameState.isGameOver() == 0) {
            return player ? 1 : -1;
        } else if (simGameState.isGameOver() == 1) {
            return player ? -1 : 1;
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
        Game copiedState = source.copyGameState();
        target.loadGameState(copiedState);
    }

    public List<Node> getChildren() {
        return children;
    }

    public double getReward() {
        return reward;
    }
}

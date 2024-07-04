import java.util.Comparator;

public class MCTS {

    int TIME_LIMIT = 5000; // Time limit in milliseconds
    public int games_simulated = 0;

    public Turn findBestMove(Game game, boolean player) {
        Node root = new Node(game, null, player);
        long endTime = System.currentTimeMillis() + TIME_LIMIT;

        while (System.currentTimeMillis() < endTime) {
            Node selectedNode = select(root);
            if (selectedNode.getGameState().isGameOver() == 2) {
                selectedNode.expand();
            }
            Node nodeToSimulate = selectedNode.getChildren().isEmpty() ? selectedNode : selectedNode.select();
            double simulationResult = nodeToSimulate.simulate();
            nodeToSimulate.backpropagate(simulationResult);
            games_simulated++;
        }

        Node bestChild = root.getChildren().stream()
                .max(Comparator.comparing(Node::getVisits))
                .orElseThrow();

        return bestChild.getGameState().getLastTurn();
    }

    private Node select(Node node) {
        while (!node.getChildren().isEmpty()) {
            node = node.select();
        }
        return node;
    }
}

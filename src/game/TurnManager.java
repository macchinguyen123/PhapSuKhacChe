package game;

public class TurnManager {
    private String currentTurn = "Player";
    private boolean skipNextEnemyTurn = false;
    private boolean skipNextPlayerTurn = false;

    public String getCurrentTurn() { return currentTurn; }

    public void nextTurn() {
        if (currentTurn.equals("Player")) currentTurn = "Enemy";
        else currentTurn = "Player";
    }

    public void skipTurn(String target) {
        if (target.equals("Player")) skipNextPlayerTurn = true;
        else skipNextEnemyTurn = true;
    }

    public boolean isTurnSkipped(String target) {
        if (target.equals("Player")) return skipNextPlayerTurn;
        else return skipNextEnemyTurn;
    }

    public void resetSkip(String target) {
        if (target.equals("Player")) skipNextPlayerTurn = false;
        else skipNextEnemyTurn = false;
    }
}

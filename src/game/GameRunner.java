package game;

/*
//Imports for testing action system
import actions.Action;
import actions.PlayerAction;
import actions.PolicyAction;
import enums.Policy;
import players.Player;

import java.util.LinkedList;
*/

/**
 * Runs a game of Secret Hitler
 */
public class GameRunner {

    public static void main(String[] args) {

        int numLibWins = 0;
        int count = 0;
        int numRounds = 0;

        for(int i = 0; i < 4096 * 4096; i++) {
            Game game = new Game();
            boolean libsWon = game.round();

            if(libsWon) {
                numLibWins++;
            }
            numRounds+= game.numRounds;
            count++;
        }

        System.out.printf("Liberals won %.2f%% of the time\n", ((double)numLibWins / (double)count) * 100);
        System.out.printf("Average number of rounds: %.2f\n", (double)numRounds / (double)count);

        /*
        //For testing the action system
        Game game = new Game();
        boolean libsWon = game.round();
        for(Action action : game.actions) {
            StringBuilder addition = new StringBuilder();
            if(action.getClass() == PlayerAction.class) {
                LinkedList<Player> players = ((PlayerAction) action).getVictims();
                for(Player player : players) {
                    addition.append(player.getRole().toString()).append(" ")
                            .append(player.getPlayerIndex()).append(", ");
                }
            }
            else if(action.getClass() == PolicyAction.class) {
                LinkedList<Policy> policies = ((PolicyAction) action).getPolicies();
                for(Policy policy : policies) {
                    addition.append(policy.toString()).append(" policy , ");
                }
            }
            addition = new StringBuilder(addition.substring(0, addition.length() - 2));
            System.out.printf("%s %d performed action %s on %s\n", action.getPlayer().getRole().toString(),
                    action.getPlayer().getPlayerIndex(), action.getType().toString(), addition.toString());
        }
        System.out.printf("%s won in %d rounds\n", libsWon ? "Liberals" : "Fascists", game.numRounds);
        */
    }
}

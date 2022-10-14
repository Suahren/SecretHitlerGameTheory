package game;

import actions.Action;
import actions.PlayerAction;
import actions.PolicyAction;
import enums.Policy;
import players.Player;

import java.time.Instant;
import java.util.LinkedList;

/**
 * Runs a game of Secret Hitler
 */
public class GameRunner {

    public static void main(String[] args) {

        Instant start = Instant.now();

        int numGames = (int)Math.pow(2, 20);
        int numLibWins = 0;
        int count = 0;
        int numRounds = 0;

        for(int i = 0; i < numGames; i++) {
            Game game = new Game();
            boolean libsWon = game.round();

            if(libsWon) {
                numLibWins++;
            }
            numRounds+= game.numRounds;
            count++;
        }

        Instant end = Instant.now();
        System.out.println("This version produced the following results:");
        System.out.println(numGames + " games executed in " +
                (end.getEpochSecond() - start.getEpochSecond()) + " seconds");
        System.out.printf("Liberals won %.2f%% of the time\n",
                ((double)numLibWins / (double)count) * 100);
        System.out.printf("Average number of rounds: %.2f\n", (double)numRounds / (double)count);
    }
}

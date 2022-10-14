package game;

import actions.Action;
import actions.PlayerAction;
import actions.PolicyAction;
import enums.Policy;
import players.Player;

import java.time.Instant;
import java.util.LinkedList;

/**
 * Runs games of Secret Hitler
 */
public class GameRunner {

    /**
     * Runs 2^args[0] games, 2^16 if no arg provided
     *
     * @param args integer argument which will result in 2^args[0] games run
     */
    public static void main(String[] args) {
        int numGames = (int)Math.pow(2, 16);

        if(args.length > 0 && args[0].matches("\\d+")) {
            numGames = (int)Math.pow(2, Integer.parseInt(args[0]));
        }

        Instant start = Instant.now();

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

package players;

import enums.Party;
import enums.Role;
import game.Game;

/**
 * Implements strategy for a Hitler Player
 */
public class Hitler extends Fascist {

    /**
     * Constructor
     *
     * @param game game the player is taking part in
     */
    public Hitler(Game game) {
        super(game);
        role = Role.HITLER;
        party = Party.FASCIST;
    }
}

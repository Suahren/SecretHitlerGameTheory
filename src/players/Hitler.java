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
     * @param id unique identifier for the player
     * @param game game the player is taking part in
     */
    public Hitler(int id, Game game) {
        super(id, game);
        role = Role.HITLER;
        party = Party.FASCIST;
    }
}

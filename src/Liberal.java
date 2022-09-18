/**
 * Implements strategy for a Liberal Player
 */
public class Liberal extends Player {

    /**
     * Constructor
     *
     * @param game game the player is taking part in
     */
    public Liberal(Game game) {
        super(game);
        role = Role.LIBERAL;
        party = Party.LIBERAL;
    }
}

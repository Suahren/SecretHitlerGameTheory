/**
 * Implements strategy for a Fascist Player
 */
public class Fascist extends Player {

    /**
     * Constructor
     *
     * @param game game the player is taking part in
     */
    public Fascist(Game game) {
        super(game);
        role = Role.FASCIST;
        party = Party.FASCIST;
    }
}

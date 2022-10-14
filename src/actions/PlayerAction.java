package actions;

import enums.ActionType;
import players.Player;

import java.util.LinkedList;

/**
 * Handles actions made on players
 */
public class PlayerAction extends Action {

    private final LinkedList<Player> victims;    //Players the action was performed on

    /**
     * Constructor
     *
     * @precondition type is SELECT, ACCUSE, INVESTIGATE, SHOOT, VOTE_YES, or VOTE_NO
     * @precondition if present, victims size is 2 if type is VOTE_YES or VOTE_NO, size 1 otherwise
     * @param player the player who performed the action
     * @param type the type of action
     * @param victims the players the action was performed on
     */
    public PlayerAction(Player player, ActionType type, LinkedList<Player> victims) {
        super(player, type);
        assert(type == ActionType.SELECT ||
                type == ActionType.ACCUSE ||
                type == ActionType.INVESTIGATE ||
                type == ActionType.SHOOT ||
                type == ActionType.VOTE_YES ||
                type == ActionType.VOTE_NO);
        assert(victims == null || ((victims.size() == 2 &&
                    (type == ActionType.VOTE_YES || type == ActionType.VOTE_NO)) ||
                (victims.size() == 1 &&
                    (type != ActionType.VOTE_YES && type != ActionType.VOTE_NO))));
        this.victims = victims;
    }

    /**
     * Gets the players the action was performed on
     *
     * @return the players the action was performed on
     */
    public LinkedList<Player> getVictims() {
        return victims;
    }

    /**
     * Gets the player the action was performed on in cases where there is only one victim
     *
     * @precondition vicitms.size() == 1
     * @return the player
     */
    public Player getVictim() {
        assert(victims.size() == 1);
        return victims.get(0);
    }

    /**
     * Produces a String representation of the PlayerAction
     *
     * @return a String representation of the PlayerAction
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(" ");
        if(victims == null ) {
            out.append("UNKNOWN ");
        }
        else {
            for (Player victim : victims) {
                out.append(victim.getRole()).append(" ").append(victim.getId()).append(" ");
            }
        }
        return super.toString() + out.substring(0, out.length() - 1);
    }
}

package actions;

import enums.ActionType;
import players.Player;

/**
 * Action taken during the game
 */
public abstract class Action {

    protected Player player;    //Player who performed the action
    protected ActionType type;  //Type of action

    /**
     * Constructor
     *
     * @param player the player who performed the action
     * @param type the type of action
     */
    public Action(Player player, ActionType type) {
        this.player = player;
        this.type = type;
    }

    /**
     * Gets the player
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the action type
     *
     * @return the action type
     */
    public ActionType getType() {
        return type;
    }

    /**
     * Produces a String representation of the Action
     *
     * @return a String representation of the Action
     */
    @Override
    public String toString() {
        return player.getRole() + " " + player.getId() + " " + type;
    }
}

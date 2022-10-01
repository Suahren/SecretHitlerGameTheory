package actions;

import enums.ActionType;
import enums.Policy;
import players.Player;

import java.util.LinkedList;

/**
 * Handles actions made on policies
 */
public class PolicyAction extends Action {

    private final LinkedList<Policy> policies; //The policies that were played or declared

    /**
     * Constructor
     *
     * @precondition type is DISCARD, PLAY, DECLARE_PASSED, or DECLARE_DISCARD
     * @precondition policies is size 2 if type is DECLARE_PASSED or VETO, size 1 otherwise
     * @param player the player who performed the action
     * @param type the type of action
     * @param policies the policies that were played or declared
     */
    public PolicyAction(Player player, ActionType type, LinkedList<Policy> policies) {
        super(player, type);
        assert(type == ActionType.DISCARD ||
                type == ActionType.PLAY ||
                type == ActionType.DECLARE_PASSED ||
                type == ActionType.DECLARE_DISCARD ||
                type == ActionType.VETO);

        this.policies = policies;
    }

    /**
     * Gets the policies that were played or declared
     *
     * @return the policies that were played or declared
     */
    public LinkedList<Policy> getPolicies() {
        return policies;
    }
}

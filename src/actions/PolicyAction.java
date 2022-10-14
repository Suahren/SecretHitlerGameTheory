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
     * @precondition if present, policies is size 2 if type is PASS, DECLARE_PASSED or VETO, size 1
     *               otherwise
     * @param player the player who performed the action
     * @param type the type of action
     * @param policies the policies that were played or declared
     */
    public PolicyAction(Player player, ActionType type, LinkedList<Policy> policies) {
        super(player, type);
        assert(type == ActionType.PLAY ||
                type == ActionType.DISCARD ||
                type == ActionType.PASS ||
                type == ActionType.DECLARE_DISCARDED ||
                type == ActionType.DECLARE_PASSED ||
                type == ActionType.VETO);
        assert((policies == null || (policies.size() == 2 &&
                    (type == ActionType.DECLARE_PASSED || type == ActionType.VETO)) ||
                (policies.size() == 1 &&
                    (type != ActionType.DECLARE_PASSED && type != ActionType.VETO))));

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

    /**
     * Gets the policy in cases where there is only one policy
     *
     * @precondition policies.size() == 1
     * @return the policy
     */
    public Policy getPolicy() {
        assert(policies.size() == 1);
        return policies.get(0);
    }

    /**
     * Produces a String representation of the PolicyAction
     *
     * @return a String representation of the PolicyAction
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(" ");
        if(policies == null ) {
            out.append("UNKNOWN ");
        }
        else {
            for (Policy policy : policies) {
                out.append(policy).append(" ");
            }
        }
        return super.toString() + out.substring(0, out.length() - 1);
    }
}

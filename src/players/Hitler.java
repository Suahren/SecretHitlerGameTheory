package players;

import actions.PolicyAction;
import enums.ActionType;
import enums.Party;
import enums.Policy;
import enums.Role;
import game.Game;

import java.util.LinkedList;

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
        playerParties = new Party[7];
        for(int i = 0; i < playerParties.length; i++) {
            playerParties[i] = i == this.id ? this.getParty() : null;
        }
    }

    /**
     * Chooses the next chancellor
     * Strategy is to pick the most suspicious player
     *
     * @precondition the current player is the president
     * @return a random player index other than the player's or the previous chancellor's
     */
    public int chooseChancellor() {
        assert(this == game.president);
        return getMostSuspicousPlayerExclChancellor();
    }

    /**
     * Determines if the player will vote for a president/chancellor combo
     * Strategy is to vote yes unless five liberal policies have been played and the chancellor is
     *   not in the fascist party
     *
     * @param president the current president
     * @param chancellor the current chancellor
     * @return true if the player decides to vote for the combo, false otherwise
     */
    public boolean vote(Player president, Player chancellor){
        return !(game.liberalPolicies.size() == 5 && playerParties[chancellor.getId()].isLiberal);
    }

    /**
     * Chooses the next president
     * Strategy is to pick the most suspicious player
     *
     * @precondition the player is the president
     * @precondition the third fascist policy was just played by the current chancellor
     * @return the index of a random player to be selected as the chancellor
     */
    public int choosePresident() {
        assert(this == game.president);
        assert(game.fascistPolicies.size() == 3 &&
                game.actions.getLast().getType() == ActionType.PLAY &&
                ((PolicyAction)game.actions.getLast()).getPolicy() == Policy.FASCIST);
        return getMostSuspiciousPlayer();
    }

    /**
     * Shoots a player
     * Strategy is to shoot the most suspicious player
     *
     * @precondition the player is the president
     * @precondition the fourth or fifth fascist policy was just played by the current chancellor
     * @return the killed player
     */
    public Player shoot() {
        assert(this == game.president);
        assert(game.fascistPolicies.size() == 4 || game.fascistPolicies.size() == 5 &&
                game.actions.getLast().getType() == ActionType.PLAY &&
                ((PolicyAction)game.actions.getLast()).getPolicy() == Policy.FASCIST);
        return game.kill(getMostSuspiciousPlayer());
    }

    /**
     * Determines if a player will agree to veto the two policies passed to the chancellor
     * Strategy is to veto if both are liberals
     *
     * @precondition veto power is enabled
     * @precondition the player is the current president or current chancellor
     * @param policies the policies the current president gave to the current chancellor
     * @return true if the player will veto, false otherwise
     */
    public boolean veto(LinkedList<Policy> policies) {
        assert(game.vetoPower);
        assert(this == game.president || this == game.chancellor);
        return policies.get(0).isLiberal && policies.get(1).isLiberal;
    }
}

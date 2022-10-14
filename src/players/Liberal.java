package players;

import actions.PolicyAction;
import enums.ActionType;
import enums.Party;
import enums.Policy;
import enums.Role;
import game.Game;

import java.util.LinkedList;

/**
 * Implements strategy for a Liberal Player
 */
public class Liberal extends Player {

    /**
     * Constructor
     *
     * @param id unique identifier for the player
     * @param game game the player is taking part in
     */
    public Liberal(int id, Game game) {
        super(id, game);
        role = Role.LIBERAL;
        party = Party.LIBERAL;
        playerParties = new Party[7];
        for(int i = 0; i < playerParties.length; i++) {
            playerParties[i] = i == this.id ? this.getParty() : null;
        }
    }

    /**
     * Chooses the next chancellor
     * Strategy is to choose the least suspicious player available
     *
     * @precondition the current player is the president
     * @return the least suspicious player available
     */
    public int chooseChancellor() {
        assert(this == game.president);
        return getLeastSuspiciousPlayerExclChancellor();
    }

    /**
     * Handles drawing three cards from the deck and discarding one
     * The remaining two policies should be passed to the chancellor
     * Will discard a fascist policy if one is present
     *
     * @precondition the player is the president
     * @return two policies for the chancellor to pick from
     */
    public LinkedList<Policy> draw() {
        assert(this == game.president);
        //Shuffle in discard if not enough cards in the deck
        if(game.deck.size() < 3) {
            game.shuffleInDiscard();
        }
        LinkedList<Policy> hand = new LinkedList<Policy>();
        //Draw three cards from the deck
        for(int i = 0; i < 3; i++) {
            hand.add(game.deck.pop());
        }
        //Discard a fascist policy if it exists in the hand
        for(Policy policy : hand) {
            if(!policy.isLiberal) {
                game.discard.push(policy);
                hand.remove(policy);
                break;
            }
        }
        //If there isn't a fascist policy, discard the first liberal policy
        if(hand.size() == 3) {
            game.discard.add(hand.remove(0));
        }
        //Returns the remaining two cards
        return hand;
    }

    /**
     * Plays one of two policies, adding the unplayed policy to the discard
     * Will play a liberal policy if one is present
     *
     * @precondition the player is the chancellor
     * @precondition policies.size() == 2
     * @param policies policies to be played
     * @return the played policy
     */
    public Policy play(LinkedList<Policy> policies) {
        assert(this == game.chancellor);
        assert(policies.size() == 2);
        //If the first policy is liberal, return it and discard the second policy
        if(policies.get(0).isLiberal) {
            game.discard.push(policies.get(1));
            return policies.get(0);
        }
        //Otherwise, play the second policy
        game.discard.push(policies.get(0));
        return policies.get(1);
    }

    /**
     * Determines if the player will vote for a president/chancellor combo
     * Will vote yes if fewer than three fascist policies are played, if two governments have
     *   failed, or if the president and chancellor have < 50 suspicion
     * Will always vote yes if self is chancellor
     *
     * @param president the current president
     * @param chancellor the current chancellor
     * @return true if the player decides to vote for the combo, false otherwise
     */
    public boolean vote(Player president, Player chancellor) {
        return this == chancellor ||
                game.fascistPolicies.size() < 3 ||
                game.numFailed > 1 ||
                (suspicions[president.getId()] < 50 && suspicions[chancellor.getId()] < 50);
    }

    /**
     * Chooses the next president
     * Strategy is to choose the least suspicious player available
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
        return getLeastSuspiciousPlayer();
    }

    /**
     * Shoots a player
     * Strategy is to target most suspicious player
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
     * Strategy is to veto if both policies are fascist
     *
     * @precondition veto power is enabled
     * @precondition the player is the current president or current chancellor
     * @param policies the policies the current president gave to the current chancellor
     * @return true if the player will veto, false otherwise
     */
    public boolean veto(LinkedList<Policy> policies) {
        assert(game.vetoPower);
        assert(this == game.president || this == game.chancellor);
        return !(policies.get(0).isLiberal || policies.get(1).isLiberal);
    }

}

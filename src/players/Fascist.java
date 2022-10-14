package players;

import enums.Party;
import enums.Policy;
import enums.Role;
import game.Game;

import java.util.LinkedList;

/**
 * Implements strategy for a Fascist Player
 */
public class Fascist extends Player {


    private Role[] playerRoles; //Fascist player role knowledge

    /**
     * Constructor
     *
     * @param id unique identifier for the player
     * @param game game the player is taking part in
     */
    public Fascist(int id, Game game) {
        super(id, game);
        role = Role.FASCIST;
        party = Party.FASCIST;
        playerRoles = new Role[7];
        for(int i = 0; i < game.players.size(); i++) {
            playerParties[i] = game.players.get(i).getParty();
            playerRoles[i] = game.players.get(i).getRole();
        }
    }

    /**
     * Handles drawing three cards from the deck and discarding one
     * The remaining two policies should be passed to the chancellor
     * Will discard a liberal policy if one is present
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
        //Discard a liberal policy if it exists in the hand
        for(Policy policy : hand) {
            if(policy.isLiberal) {
                game.discard.push(policy);
                hand.remove(policy);
                break;
            }
        }
        //If there isn't a fascist policy, discard the first fascist policy
        if(hand.size() == 3) {
            game.discard.add(hand.remove(0));
        }
        //Returns the remaining two cards
        return hand;
    }

    /**
     * Plays one of two policies, adding the unplayed policy to the discard
     * Will play a fascist policy if one is present and three liberal cards have been played
     *
     * @precondition the player is the chancellor
     * @precondition policies.size() == 2
     * @param policies policies to be played
     * @return the played policy
     */
    public Policy play(LinkedList<Policy> policies) {
        assert(this == game.chancellor);
        assert(policies.size() == 2);
        //If both policies are the same, discard the first and return the other
        if(policies.get(0).isLiberal == policies.get(0).isLiberal) {
            game.discard.push(policies.get(0));
            return policies.get(1);
        }
        //If fewer than three liberal policies have been played, play a liberal policy
        if(game.liberalPolicies.size() < 3) {
            if(policies.get(0).isLiberal) {
                game.discard.push(policies.get(1));
                return policies.get(0);
            }
            else {
                game.discard.push(policies.get(0));
                return policies.get(1);
            }
        }
        //Otherwise, play a fascist policy
        else {
            if(!policies.get(0).isLiberal) {
                game.discard.push(policies.get(1));
                return policies.get(0);
            }
            else {
                game.discard.push(policies.get(0));
                return policies.get(1);
            }
        }
    }
}

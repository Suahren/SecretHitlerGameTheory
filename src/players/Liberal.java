package players;

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
}

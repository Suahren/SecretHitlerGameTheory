package players;

import actions.PolicyAction;
import enums.ActionType;
import enums.Party;
import enums.Policy;
import enums.Role;
import game.Game;

import java.util.LinkedList;

/**
 * Implements strategy for a Fascist Player
 */
public class Fascist extends Player {


    private final Role[] playerRoles; //Fascist player role knowledge

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
        playerParties = new Party[7];
        for(int i = 0; i < playerParties.length; i++) {
            playerParties[i] = i == this.id ? this.getParty() : null;
        }
    }

    /**
     * Shows player roles to the fascist
     */
    public void viewRoles() {
        for(int i = 0; i < playerRoles.length; i++) {
            playerParties[i] = game.players.get(i).getParty();
            playerRoles[i] = game.players.get(i).getRole();
        }
    }

    /**
     * Chooses the next chancellor
     * Strategy is to pick the other fascist unless three fascist policies have been played, in
     *   which case they will pick Hitler
     *
     * @precondition the current player is the president
     * @return a random player index other than the player's or the previous chancellor's
     */
    public int chooseChancellor() {
        assert(this == game.president);
        if(game.fascistPolicies.size() < 3 && getOtherFascist() != -1) {
            return getOtherFascist();
        }
        else {
            return getHitler();
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
     * Strategy is to pick the other fascist if alive, otherwise Hitler
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
        return getOtherFascist() != -1 ? getOtherFascist() : getHitler();
    }

    /**
     * Shoots a player
     * Strategy is to shoot a random liberal
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
        return game.kill(getRandomLiberal());
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

    /**
     * Gets the other fascist player
     *
     * @return the index of the other fascist player, -1 if dead
     */
    private int getOtherFascist() {
        for(int i = 0; i < playerRoles.length; i++) {
            if(playerRoles[i] == Role.FASCIST && i != this.id) {
                return game.findPlayerIndexById(i);
            }
        }
        return -1;
    }

    /**
     * Gets Hitler
     *
     * @return the index of Hitler
     */
    private int getHitler() {
        for(int i = 0; i < playerRoles.length; i++) {
            if(playerRoles[i].isHitler) {
                return game.findPlayerIndexById(i);
            }
        }
        return -1;
    }

    /**
     * Gets a random liberal's index
     *
     * @return a random liberal's index
     */
    private int getRandomLiberal() {
        LinkedList<Player> liberals = new LinkedList<>();
        for(Player player : game.players) {
            if(player.getRole() == Role.LIBERAL) {
                liberals.add(player);
            }
        }
        return liberals.get((int)(Math.random() * liberals.size())).getPlayerIndex();
    }
}

package players;

import enums.Party;
import enums.Policy;
import enums.Role;
import game.Game;

import java.util.LinkedList;
import java.util.Random;

/**
 * Represents a player
 * Default strategy is random
 */
public abstract class Player
{
    protected final Game game;    //Game the player is taking part in
    protected Role role;        //Player's role
    protected Party party;      //Player's party

    /**
     * Constructor
     *
     * @param game game the player is taking part in
     */
    public Player(Game game) {
        this.game = game;
    }

    public Role getRole() {
        return role;
    }

    public Party getParty() {
        return party;
    }

    /**
     * Choses the next chancellor
     * Default strategy is random, avoids chosing previous chancellor
     *
     * @precondition the current player is the president
     * @return a random player index other than the player's or the previous chancellor's
     */
    public int choseChancellor() {
        int playerIndex = getPlayerIndex();
        int lastChancellor = game.getChancellorIndex();

        //Determines if the player was the last chancellor, increases the number of choices by one if true
        int numChoices = playerIndex == lastChancellor ? game.players.size() - 1 : game.players.size() - 2;

        Random rand = new Random();

        int index = rand.nextInt(numChoices);
        int mod = 0;

        //If the player's index is below or at the chosen index, increase the index by one to account
        if(index >= playerIndex) {
            mod++;
        }
        //If the previous chancellor's index is below or at the chosen index, increase the index by one to account
        if(playerIndex != lastChancellor && index >= lastChancellor) {
            mod++;
        }
        //Add the increase
        index += mod;
        return index;
    }

    /**
     * Handles drawing three cards from the deck and discarding one
     * The remaining two policies should be passed to the chancellor
     * Default strategy is random
     *
     * @precondition the player is the president
     * @return two policies for the chancellor to pick from
     */
    public LinkedList<Policy> draw() {
        //Shuffle in discard if not enough cards in the deck
        if(game.deck.size() < 3) {
            game.shuffleInDiscard();
        }

        LinkedList<Policy> hand = new LinkedList<Policy>();
        //Draw three cards from the deck
        for(int i = 0; i < 3; i++) {
            hand.add(game.deck.pop());
        }
        //Discards a random card from the hand
        Random rand = new Random();
        int discard = rand.nextInt(3);
        game.discard.push(hand.remove(discard));

        //Returns the remaining two cards
        return hand;
    }

    /**
     * Plays one of two policies, adding the unplayed policy to the discard
     * Default behavior is random
     *
     * @precondition the player is the chancellor
     * @precondition policies.size() == 2
     * @param policies policies to be played
     * @return the played policy
     */
    public Policy play(LinkedList<Policy> policies) {
        Random rand = new Random();
        game.discard.push(policies.remove(rand.nextInt(2)));
        return policies.get(0);
    }

    /**
     * Handles investigating another player's party enum
     * Default behavior picks a random player and does nothing
     *
     * @return the investigated player
     */
    public Player investigate() {
        return game.players.get(pickRandomPlayer());
    }

    /**
     * Determines if the player will vote for a president/chancellor combo
     * Default behavior is to always vote yes before three fascist policies have been played, otherwise random
     *
     * @param president the current president
     * @param chancellor the current chancellor
     * @return true if the player decides to vote for the combo, false otherwise
     */
    public boolean vote(Player president, Player chancellor) {
        //If the game cannot be ended by electing Hitler as chancellor, vote yes on the combo
        if(game.fascistPolicies.size() < 3) {
            return true;
        }
        else {
            Random rand = new Random();
            return rand.nextBoolean();
        }
    }

    /**
     * Chooses the next president
     * Default behavior is to pick a random player
     *
     * @precondition the player is the president
     * @precondition the third fascist policy was just played by the current chancellor
     * @return the index of a random player to be selected as the chancellor
     */
    public int chosePresident() {
        return pickRandomPlayer();
    }

    /**
     * Shoots a player
     * Default behavior is random
     *
     * @precondition the fourth or fifth fascist policies were just played by the current chancellor
     * @return the killed player
     */
    public Player shoot() {
        return game.kill(pickRandomPlayer());
    }

    /**
     * Determines if a player will agree to veto the two policies passed to the chancellor
     *
     * @precondition veto power is enabled
     * @precondition the player is the current president or current chancellor
     * @param policies the policies the current president gave to the current chancellor
     * @return true if the player will veto, false otherwise
     */
    public boolean veto(LinkedList<Policy> policies) {
        Random rand = new Random();
        return rand.nextBoolean();
    }

    /**
     * Gets the index of this player in the game
     *
     * @return the index of this player, -1 if the player is dead
     */
    public int getPlayerIndex() {
        for(int i = 0; i < game.players.size(); i++) {
            if (this == game.players.get(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Picks a random player other than the current player
     *
     * @return index of a random player
     */
    private int pickRandomPlayer() {
        int playerIndex = getPlayerIndex();

        Random rand = new Random();
        int index = rand.nextInt(game.players.size() - 1);
        if(index >= playerIndex) {
            index++;
        }
        return index;
    }
}

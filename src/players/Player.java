package players;

import actions.Action;
import actions.PlayerAction;
import actions.PolicyAction;
import enums.ActionType;
import enums.Party;
import enums.Policy;
import enums.Role;
import game.Game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * Represents a player
 * Default strategy is random
 */
public abstract class Player implements Comparable<Player> {
    protected int id;                           //Unique identifier for the player
    protected final Game game;                  //Game the player is taking part in
    protected Role role;                        //Player's role
    protected Party party;                      //Player's party
    protected LinkedList<Action>[] knowledge;   //Actions player is aware of
    protected int[] suspicions;                 //How suspect each player appears
    protected Party[] playerParties;            //Known player parties, null if unknown

    /**
     * Constructor
     *
     * @param id unique identifier for the player
     * @param game game the player is taking part in
     */
    @SuppressWarnings("unchecked")
    public Player(int id, Game game) {
        this.id = id;
        this.game = game;
        knowledge = new LinkedList[7];
        for(int i = 0; i < knowledge.length; i++) {
            knowledge[i] = new LinkedList<Action>();
        }
        suspicions = new int[7];
        Arrays.fill(suspicions, 0);
    }

    /**
     * Gets player id
     *
     * @return player id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets player role
     *
     * @return player role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Gets player party
     *
     * @return player party
     */
    public Party getParty() {
        return party;
    }

    /**
     * Chooses the next chancellor
     * Default strategy is random, avoids choosing previous chancellor
     *
     * @precondition the current player is the president
     * @return a random player index other than the player's or the previous chancellor's
     */
    public int chooseChancellor() {
        assert(this == game.president);
        int playerIndex = getPlayerIndex();
        int lastChancellor = game.getChancellorIndex();

        //Determines if the player was the last chancellor
        //Increases the number of choices by one if true
        int numChoices = playerIndex == lastChancellor ?
                game.players.size() - 1 : game.players.size() - 2;

        Random rand = new Random();

        int index = rand.nextInt(numChoices);
        int mod = 0;

        //If the player's index is below or at the chosen index
        //then increase the index by one to account
        if(index >= playerIndex) {
            mod++;
        }
        //If the previous chancellor's index is below or at the chosen index
        //then increase the index by one to account
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
        //Discards a random card from the hand
        Random rand = new Random();
        int discard = rand.nextInt(3);
        game.discard.push(hand.remove(discard));

        //Returns the remaining two cards
        return hand;
    }

    /**
     * Plays one of two policies, adding the unplayed policy to the discard
     * Default strategy is random
     *
     * @precondition the player is the chancellor
     * @precondition policies.size() == 2
     * @param policies policies to be played
     * @return the played policy
     */
    public Policy play(LinkedList<Policy> policies) {
        assert(this == game.chancellor);
        assert(policies.size() == 2);
        Random rand = new Random();
        game.discard.push(policies.remove(rand.nextInt(2)));
        return policies.get(0);
    }

    /**
     * Handles investigating another player's party enum
     * Default strategy picks a random player and updates player's party list
     *
     * @return the investigated player
     */
    public Player investigate() {
        Player investigated = game.players.get(pickRandomPlayer());
        playerParties[investigated.getId()] = investigated.getParty();
        return investigated;
    }

    /**
     * Determines if the player will vote for a president/chancellor combo
     * Default strategy is to always vote yes before three fascist policies are played,
     *   otherwise random
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
     * Default strategy is to pick a random player
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
        return pickRandomPlayer();
    }

    /**
     * Shoots a player
     * Default strategy is random
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
        return game.kill(pickRandomPlayer());
    }

    /**
     * Determines if a player will agree to veto the two policies passed to the chancellor
     * Default strategy is random
     *
     * @precondition veto power is enabled
     * @precondition the player is the current president or current chancellor
     * @param policies the policies the current president gave to the current chancellor
     * @return true if the player will veto, false otherwise
     */
    public boolean veto(LinkedList<Policy> policies) {
        assert(game.vetoPower);
        assert(this == game.president || this == game.chancellor);
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
     * Adds an action to the knowledge of the player and updates suspicions
     *
     * @param action an action
     */
    public void addAction(Action action) {
        updateSuspicions(action);
        knowledge[action.getPlayer().getId()].add(action);
    }

    /**
     * Compare method for sorting player array
     *
     * @param other the player to be compared
     * @return this id minus other id
     */
    @Override
    public int compareTo(Player other) {
        return this.getId() - other.getId();
    }

    /**
     * Picks a random player other than the current player
     *
     * @return index of a random player
     */
    protected int pickRandomPlayer() {
        int playerIndex = getPlayerIndex();

        Random rand = new Random();
        int index = rand.nextInt(game.players.size() - 1);
        if(index >= playerIndex) {
            index++;
        }
        return index;
    }

    /**
     * Gets the least suspicious player excluding self
     *
     * @return the least suspicious player's index
     */
    protected int getLeastSuspiciousPlayer() {
        int leastIndex = 0;
        int leastValue = Integer.MAX_VALUE;
        for(int i = 0; i < suspicions.length; i++) {
            if(suspicions[i] < leastValue && game.isAlive(i) && this.id != i) {
                leastIndex = i;
                leastValue = suspicions[i];
            }
        }
        return game.findPlayerIndexById(leastIndex);
    }

    /**
     * Gets the least suspicious player excluding the chancellor and president
     *
     * @return the least suspicious player's index excluding the chancellor and president
     */
    protected int getLeastSuspiciousPlayerExclChancellor() {
        int leastIndex = 0;
        int leastValue = Integer.MAX_VALUE;
        for(int i = 0; i < suspicions.length; i++) {
            if(suspicions[i] < leastValue && game.isAlive(i) &&
                    (game.chancellor == null || game.chancellor.getId() != i) && this.id != i) {
                leastIndex = i;
                leastValue = suspicions[i];
            }
        }
        return game.findPlayerIndexById(leastIndex);
    }

    /**
     * Gets the most suspicious player excluding self
     *
     * @return the most suspicious player's index
     */
    protected int getMostSuspiciousPlayer() {
        int mostIndex = 0;
        int mostValue = Integer.MIN_VALUE;
        for(int i = 0; i < suspicions.length; i++) {
            if(suspicions[i] > mostValue && game.isAlive(i) && this.id != i) {
                mostIndex = i;
                mostValue = suspicions[i];
            }
        }
        return game.findPlayerIndexById(mostIndex);
    }

    /**
     * Gets the most suspicious player excluding self and the chancellor
     *
     * @return the most suspicious player's index excluding self and the chancellor
     */
    protected int getMostSuspicousPlayerExclChancellor() {
        int mostIndex = 0;
        int mostValue = Integer.MIN_VALUE;
        for(int i = 0; i < suspicions.length; i++) {
            if(suspicions[i] > mostValue && game.isAlive(i) &&
                    (game.chancellor == null || game.chancellor.getId() != i)  && this.id != i) {
                mostIndex = i;
                mostValue = suspicions[i];
            }
        }
        return game.findPlayerIndexById(mostIndex);
    }

    private void updateSuspicions(Action action) {
        suspicions[action.getPlayer().getId()] += calcSuspicion(action);
    }

    /**
     * Assigns a suspicion value to an isolated action
     *
     * @param action an Action
     * @return the suspicion associated with the action
     */
    private int calcSuspicion(Action action) {

        if(playerParties[action.getPlayer().getId()] == Party.FASCIST) {
            return Integer.MAX_VALUE;
        }
        else if(playerParties[action.getPlayer().getId()] == Party.LIBERAL) {
            return Integer.MIN_VALUE;
        }
            if(action.getClass() == PolicyAction.class){
                PolicyAction policyAction = (PolicyAction)action;
                switch(policyAction.getType()) {
                    case PLAY:
                        if(policyAction.getPolicy() == Policy.FASCIST) {
                            return 10;
                        }
                        else {
                            return 10;
                        }
                    case DISCARD:
                        if(policyAction.getPolicy() == Policy.FASCIST) {
                            return 5;
                        }
                        else  if(policyAction.getPolicy() == Policy.LIBERAL) {
                            return 25;
                        }
                        break;
                    case PASS:
                        break;
                    case DECLARE_PASSED:
                        break;
                    case VETO:
                        break;
                }
            } else {
                PlayerAction playerAction = (PlayerAction)action;
                switch(playerAction.getType()) {
                    case SELECT:
                        return (int)(suspicions[playerAction.getVictim().getId()] * .5);
                    case ACCUSE:
                        if(this == playerAction.getVictim()) {
                            return 50;
                        }
                        break;
                    case INVESTIGATE:
                        break;
                    case SHOOT:
                        break;
                    case VOTE_YES:
                        break;
                    case VOTE_NO:
                        break;
                }
            }
        return 0;
    }
}

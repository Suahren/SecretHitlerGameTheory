package game;

import actions.Action;
import actions.PolicyAction;
import actions.PlayerAction;
import enums.ActionType;
import enums.Policy;
import enums.Role;
import players.Fascist;
import players.Hitler;
import players.Liberal;
import players.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Handles a game of Secret Hitler
 */
public class Game {

    public LinkedList<Player> players;      //List of alive players
    public LinkedList<Player> deadPlayers;  //List of dead players
    public LinkedList<Action> actions;      //Master list of all game actions
    public Stack<Policy> deck;              //Deck of policies
    public Stack<Policy> discard;           //Discard for policies
    public Stack<Policy> liberalPolicies;   //Played liberal policies
    public Stack<Policy> fascistPolicies;   //Played fascist policies

    public Player chancellor;               //Current chancellor
    public Player president;                //Current president

    public boolean vetoPower;               //If veto power has been enabled
    public int numRounds;                   //Number of rounds passed
    public int numFailed;                   //Number of rounds without a played policy
    private boolean presidentPicks;         //If the president selects the next president

    /**
     * Constructor
     */
    public Game()
    {
        players = new LinkedList<Player>();
        deadPlayers = new LinkedList<Player>();
        actions = new LinkedList<Action>();
        deck = new Stack<Policy>();
        discard = new Stack<Policy>();
        liberalPolicies = new Stack<Policy>();
        fascistPolicies = new Stack<Policy>();
        numRounds = 0;
        numFailed = 0;
        presidentPicks = false;

        LinkedList<Integer> ids = new LinkedList<>();
        for(int i = 0; i < 7; i++) {
            ids.add(i);
        }

        Collections.shuffle(ids);

        for(int i = 0; i < 4; i++) {
            players.add(new Liberal(ids.remove(), this));
        }

        for(int i = 0; i < 2; i++) {
            players.add(new Fascist(ids.remove(), this));
        }

        players.add(new Hitler(ids.remove(),this));

        for(int i = 0; i < 6; i++) {
            deck.add(Policy.LIBERAL);
        }
        for(int i = 0; i < 11; i++) {
            deck.add(Policy.FASCIST);
        }

        //Matches player IDs to initial player position
        Collections.sort(players);
        Collections.shuffle(deck);

        //Reveals player roles to the fascist players
        for(Player player : players) {
            if(player.getRole() == Role.FASCIST) {
                ((Fascist)player).viewRoles();
            }
        }

        //Will set president to be the first player once the round starts
        president = players.get(6);

        vetoPower = false;
    }

    /**
     * Handles a round, runs recursively until a win condition is met
     *
     * @return true if the liberals win, false if the fascists win
     */
    public boolean round() {
        numRounds++;

        //Rotate the president unless the previous president already picked the new president
        if(!presidentPicks) {
            president = players.get((president.getPlayerIndex() + 1) % players.size());
        }
        else {
            presidentPicks = false;
        }

        chancellor = players.get(president.chooseChancellor());
        addAction(president, ActionType.SELECT, chancellor);

        //Each player votes
        int numYes = 0;
        for(Player player : players) {
            if(player.vote(president, chancellor)) {
                numYes++;
                addAction(player, ActionType.VOTE_YES, president, chancellor);
            }
            else {
                addAction(player, ActionType.VOTE_NO, president, chancellor);
            }
        }

        //If the president/chancellor combo was voted up
        if((double) numYes / (double) players.size() > .5) {

            //The fascists win if Hitler is elected chancellor after 3 fascist policies are played
            if(fascistPolicies.size() > 3 && chancellor.getRole().isHitler) {
                return false;
            }

            LinkedList<Policy> policies = president.draw();
            addAction(president, ActionType.DISCARD, discard.peek());
            addAction(president, ActionType.PASS, policies.get(0), policies.get(1));

            //If the chancellor and president agree to veto the policies
            if(chancellor.veto(policies) && president.veto(policies)) {
                addAction(president, ActionType.VETO, policies.get(0), policies.get(1));

                for(int i = 0; i < 2; i++) {
                    discard.push(policies.remove(0));
                }
                numFailed++;
            }
            else {
                Policy played = chancellor.play(policies);
                addAction(chancellor, ActionType.DISCARD, discard.peek());
                addAction(chancellor, ActionType.PLAY, played);
                numFailed = 0;

                //Play the card and handle fascist policy powers
                if (played.isLiberal) {
                    liberalPolicies.push(played);
                } else {
                    fascistPolicies.push(played);

                    if (fascistPolicies.size() == 2) {
                        addAction(president, ActionType.INVESTIGATE, president.investigate());
                    } else if (fascistPolicies.size() == 3) {
                        Player oldPresident = president;
                        president = players.get(president.choosePresident());
                        presidentPicks = true;
                        addAction(oldPresident, ActionType.SELECT, president);
                    } else if (fascistPolicies.size() == 4) {
                        addAction(president, ActionType.SHOOT, president.shoot());
                    } else if (fascistPolicies.size() == 5) {
                        addAction(president, ActionType.SHOOT, president.shoot());
                        vetoPower = true;
                    }
                }
            }
        }
        else {
            //If the president/chancellor combo was voted down, increase the election tracker
            numFailed++;
        }

        //If three governments have failed in a row, flip a policy from the deck
        if(numFailed == 3) {
            if(deck.size() < 1) {
                shuffleInDiscard();
            }
            Policy flip = deck.pop();

            if (flip.isLiberal) {
                liberalPolicies.push(flip);
            } else {
                fascistPolicies.push(flip);
            }
            //Reset the election tracker
            numFailed = 0;
            //Anyone can be the next chancellor
            chancellor = null;
        }

        //Handle win conditions
        if (fascistPolicies.size() == 6) {
            return false;
        }
        else if(liberalPolicies.size() == 5 || hitlerIsDead()) {
            return true;
        }

        return round();
    }

    /**
     * Gets the index of the chancellor
     *
     * @return index of the chancellor, -1 if no chancellor
     */
    public int getChancellorIndex() {
        for(int i = 0; i < players.size(); i++) {
            if(chancellor == players.get(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Shuffles the discard back into the deck
     */
    public void shuffleInDiscard() {
        deck.addAll(discard);
        discard = new Stack<Policy>();
        Collections.shuffle(deck);
    }

    /**
     * Removes the player at the specified index from the player list and adds them to the dead
     *   players list
     *
     * @param playerIndex index of the player to be killed
     * @return the killed player
     */
    public Player kill(int playerIndex) {
        Player killed = players.remove(playerIndex);
        deadPlayers.add(killed);
        return killed;
    }

    /**
     * Gets a player by player ID
     * @param id player ID
     * @return a player
     */
    public Player findPlayerById(int id) {
        for(Player player : players) {
            if(player.getId() == id) {
                return player;
            }
        }
        return null;
    }

    /**
     * Gets the player index by player ID
     *
     * @param id player ID
     * @return the player index for the player matching id, -1 if dead
     */
    public int findPlayerIndexById(int id) {
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determines if a player with the specified ID is alive
     *
     * @param id player ID
     * @return
     */
    public boolean isAlive(int id) {
        for(Player player : players) {
            if(player.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a player with the HITLER role is alive
     *
     * @return true if a player with the HITLER role remains in the players list, false otherwise
     */
    private boolean hitlerIsDead() {
        for (Player player : players) {
            if (player.getRole().isHitler) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method to add a PlayerAction to the actions list
     *
     * @precondition type is SELECT, ACCUSE, INVESTIGATE, or SHOOT
     * @param player the player who performed the action
     * @param type the type of action
     * @param victim the player the action was performed on
     */
    private void addAction(Player player, ActionType type, Player victim) {
        assert(type == ActionType.SELECT || type == ActionType.ACCUSE ||
                type == ActionType.INVESTIGATE || type == ActionType.SHOOT);
        actions.add(new PlayerAction(player, type, makeList(victim)));
        updatePlayerKnowledge();
    }

    /**
     * Helper method to add a PlayerAction to the actions list
     *
     * @precondition type is VOTE_YES, or VOTE_NO
     * @param player the player who performed the action
     * @param type the type of action
     * @param first the first player the action was performed on
     * @param second the second player the action was performed on
     */
    private void addAction(Player player, ActionType type, Player first, Player second) {
        assert(type == ActionType.VOTE_YES || type == ActionType.VOTE_NO);
        actions.add(new PlayerAction(player, type, makeList(first, second)));
        updatePlayerKnowledge();
    }

    /**
     * Helper method to add a PolicyAction to the actions list
     *
     * @precondition type is DISCARD, PLAY, or DECLARE_DISCARD
     * @param player the player who performed the action
     * @param type the type of action
     * @param policy the policy that was played or declared
     */
    private void addAction(Player player, ActionType type, Policy policy) {
        assert(type == ActionType.DISCARD || type == ActionType.PLAY ||
                type == ActionType.DECLARE_DISCARDED);
        actions.add(new PolicyAction(player, type, makeList(policy)));
        updatePlayerKnowledge();
    }

    /**
     * Helper method to add a PolicyAction to the actions list
     *
     * @precondition type is PASS, DECLARE_PASSED or VETO
     * @param player the player who performed the action
     * @param type the type of action
     * @param first the first policy that was played or declared
     * @param second the second policy that was played or declared
     */
    private void addAction(Player player, ActionType type, Policy first, Policy second) {
        assert(type == ActionType.PASS || type == ActionType.DECLARE_PASSED
                || type == ActionType.VETO);
        actions.add(new PolicyAction(player, type, makeList(first, second)));
        updatePlayerKnowledge();
    }

    /**
     * Updates player knowledge with the last processed action
     */
    private void updatePlayerKnowledge() {
        if(actions.getLast().getType() == ActionType.DISCARD) {
            for (Player player : players) {
                //No one but the player and the president knows what card was discarded
                if (player != actions.getLast().getPlayer() && player != president) {
                    player.addAction(new PolicyAction(actions.getLast().getPlayer(),
                            actions.getLast().getType(), dummyPolicies(1)));
                }
            }
            //Adds the action to the president and, if visible, to the chancellor
            president.addAction(actions.getLast());
            if(actions.getLast().getPlayer() != president) {
                actions.getLast().getPlayer().addAction(actions.getLast());
            }
        }
        else if(actions.getLast().getType() == ActionType.PASS) {
            for (Player player : players) {
                //No one but the president and chancellor knows what cards were passed
                if (player != president  && player != chancellor) {
                    player.addAction(new PolicyAction(actions.getLast().getPlayer(),
                            actions.getLast().getType(), dummyPolicies(2)));
                }
            }
            //Adds the action to the president and chancellor
            president.addAction(actions.getLast());
            chancellor.addAction(actions.getLast());
        }
        else {
            //If actions is visible to all players, add the action to all players
            for (Player player : players) {
                player.addAction(actions.getLast());
            }
        }
    }

    /**
     * Helper method to wrap an object in a LinkedList
     *
     * @param single object to be wrapped
     * @return a LinkedList containing single
     */
    private <T> LinkedList<T> makeList(T single) {
        LinkedList<T> list = new LinkedList<T>();
        list.add(single);
        return list;
    }

    /**
     * Helper method to wrap two objects in a LinkedList
     *
     * @param first first object to be wrapped
     * @param second second object to be wrapped
     * @return a LinkedList containing first and second
     */
    private <T> LinkedList<T> makeList(T first, T second) {
        LinkedList<T> list = new LinkedList<T>();
        list.add(first);
        list.add(second);
        return list;
    }

    /**
     * Returns a policy list with null values
     *
     * @precondition size == 1 || size == 2
     * @param size size of list
     * @return policy list with null values
     */
    private LinkedList<Policy> dummyPolicies(int size) {
        assert(size == 1 || size == 2);
        LinkedList<Policy> dummy = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            dummy.add(null);
        }
        return dummy;
    }
}

import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Handles a game of Secret Hitler
 */
public class Game {

    public LinkedList<Player> players;      //List of alive players
    public Stack<Policy> deck;              //Deck of policies
    public Stack<Policy> discard;           //Discard for policies
    public Stack<Policy> liberalPolicies;   //Played liberal policies
    public Stack<Policy> fascistPolicies;   //Played fascist policies

    public Player chancellor;               //Current chancellor
    public Player president;                //Current president

    public boolean vetoPower;               //If veto power has been enabled
    public int numRounds;                   //Number of rounds passed
    private int numFailed;                  //Number of rounds without a played policy
    private boolean presidentPicks;         //If the president selects the next president

    /**
     * Constructor
     */
    public Game()
    {
        players = new LinkedList<Player>();
        deck = new Stack<Policy>();
        discard = new Stack<Policy>();
        liberalPolicies = new Stack<Policy>();
        fascistPolicies = new Stack<Policy>();
        numRounds = 0;
        numFailed = 0;
        presidentPicks = false;

        for(int i = 0; i < 4; i++) {
            players.add(new Liberal(this));
        }

        for(int i = 0; i < 2; i++) {
            players.add(new Fascist(this));
        }

        players.add(new Hitler(this));

        for(int i = 0; i < 6; i++) {
            deck.add(Policy.LIBERAL);
        }
        for(int i = 0; i < 11; i++) {
            deck.add(Policy.FASCIST);
        }

        Collections.shuffle(players);
        Collections.shuffle(deck);

        president = players.get(0);

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
            int newIndex = (president.getPlayerIndex() + 1) % players.size();
            president = players.get(newIndex);
        }
        else {
            presidentPicks = false;
        }

        chancellor = players.get(president.choseChancellor());

        //Each player votes
        int numYes = 0;
        for(Player player : players) {
            if(player.vote(president, chancellor)) {
                numYes++;
            }
        }

        //If the president/chancellor combo was voted up
        if((double) numYes / (double) players.size() > .5) {

            //If Hitler is elected chancellor after 3 fascist policies have been played, the fascists win
            if(fascistPolicies.size() > 3 && chancellor.role.isHitler) {
                return false;
            }

            LinkedList<Policy> policies = president.draw();

            //If the chancellor and president agree to veto the policies
            if(chancellor.veto(policies) && president.veto(policies)) {
                for(int i = 0; i < 2; i++) {
                    discard.push(policies.remove(0));
                }
                numFailed++;
            }
            else {
                Policy played = chancellor.play(policies);
                numFailed = 0;

                //Play the card and handle fascist policy powers
                if (played.isLiberal) {
                    liberalPolicies.push(played);
                } else {
                    fascistPolicies.push(played);

                    if (fascistPolicies.size() == 2) {
                        president.investigate();
                    } else if (fascistPolicies.size() == 3) {
                        president = players.get(president.chosePresident());
                        presidentPicks = true;
                    } else if (fascistPolicies.size() == 4) {
                        president.shoot();
                    } else if (fascistPolicies.size() == 5) {
                        president.shoot();
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
     * Determines if a player with the HITLER role is alive
     *
     * @return true if a player with the HITLER role remains in the players list, false otherwise
     */
    private boolean hitlerIsDead() {
        for (Player player : players) {
            if (player.role.isHitler) {
                return false;
            }
        }
        return true;
    }
}

import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

public class Game {

    public LinkedList<Player> players;
    public Stack<Policy> deck;
    public Stack<Policy> discard;
    public Stack<Policy> liberalPolicies;
    public Stack<Policy> fascistPolicies;

    public Player chancellor;
    public Player president;

    public boolean vetoPower;
    public int numRounds;

    public Game()
    {
        players = new LinkedList<Player>();
        deck = new Stack<Policy>();
        discard = new Stack<Policy>();
        liberalPolicies = new Stack<Policy>();
        fascistPolicies = new Stack<Policy>();
        numRounds = 0;

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


    //TODO add in voting for pres/chancellor combo
    public boolean round() {
        numRounds++;

        if(fascistPolicies.size() != 3) {
            int newIndex = (president.getPlayerIndex() + 1) % players.size();
            president = players.get(newIndex);
        }

        chancellor = players.get(president.choseChancellor());

        if(fascistPolicies.size() > 3 && chancellor.role.val) {
            return false;
        }
        LinkedList<Policy> policies = president.draw();
        Policy played = chancellor.play(policies);

        if(!vetoPower || president.veto(played)) {
            if (played.val) {
                liberalPolicies.push(played);
            } else {
                fascistPolicies.push(played);

                if (fascistPolicies.size() == 2) {
                    president.investigate();
                } else if (fascistPolicies.size() == 3) {
                    president = players.get(president.chosePresident());
                } else if (fascistPolicies.size() == 4) {
                    president.shoot();
                } else if (fascistPolicies.size() == 5) {
                    president.shoot();
                    vetoPower = true;
                } else if (fascistPolicies.size() == 6) {
                    return false;
                }
            }
        }
        else {
            discard.push(played);
        }

        if(liberalPolicies.size() == 5 || hitlerIsDead()) {
            return true;
        }

        return round();
    }

    /**
     * Gets the index of the chancellor
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

    public void shuffleInDiscard() {
        deck.addAll(discard);
        discard = new Stack<Policy>();
        Collections.shuffle(deck);
    }

    public boolean hitlerIsDead() {
        for (Player player : players) {
            if (player.role.val) {
                return false;
            }
        }
        return true;
    }
}

import java.util.LinkedList;
import java.util.Random;

public abstract class Player
{

    private final Game game;
    protected Role role;
    protected Party party;

    public Player(Game game){
        this.game = game;
    }

    public int choseChancellor() {
        int playerIndex = getPlayerIndex();
        int lastChancellor = game.getChancellorIndex();

        int numChoices = playerIndex == lastChancellor ? game.players.size() - 1 : game.players.size() - 2;

        Random rand = new Random();

        int index = rand.nextInt(numChoices);
        int mod = 0;

        if(index >= playerIndex) {
            mod++;
        }

        if(playerIndex != lastChancellor && index >= lastChancellor){
            mod++;
        }
        index += mod;
        return index;
    }

    public LinkedList<Policy> draw() {
        if(game.deck.size() < 3) {
            game.shuffleInDiscard();
        }

        LinkedList<Policy> hand = new LinkedList<Policy>();

        for(int i = 0; i < 3; i++){
            hand.add(game.deck.pop());
        }

        Random rand = new Random();
        int discard = rand.nextInt(3);
        game.discard.push(hand.remove(discard));

        return hand;
    }

    public Policy play(LinkedList<Policy> policies) {
        Random rand = new Random();
        game.discard.add(policies.get(rand.nextInt(2)));
        return policies.get(0);
    }

    public void investigate() {

    }

    public int chosePresident() {
        return pickRandomPlayer();
    }

    public void shoot() {
        game.players.remove(pickRandomPlayer());
    }

    public boolean veto(Policy policy) {
        Random rand = new Random();
        return rand.nextBoolean();
    }

    /**
     * Gets the index of this player in the game
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
     * @return index of a random player
     */
    private int pickRandomPlayer(){
        int playerIndex = getPlayerIndex();

        Random rand = new Random();
        int index = rand.nextInt(game.players.size() - 1);
        if(index >= playerIndex) {
            index++;
        }
        return index;
    }

}

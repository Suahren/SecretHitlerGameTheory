public class GameRunner {

    public static void main(String[] args){

        int numLibWins = 0;
        int count = 0;

        for(int i = 0; i < 4096 * 4096; i++) {
            Game game = new Game();
            boolean libsWon = game.round();

            if(libsWon){
                numLibWins++;
            }
            count++;
        }

        System.out.printf("Liberals won %.2f%% of the time", ((double)numLibWins / (double)count) * 100);
    }
}

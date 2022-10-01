package enums;

/**
 * Player party identification enum
 */
public enum Party {
    LIBERAL(true),
    FASCIST(false);

    public final boolean isLiberal; //If the player is liberal

    /**
     * Constructor
     *
     * @param isLiberal true if the party is LIBERAL, false if the party is FASCIST
     */
    private Party(boolean isLiberal) {
        this.isLiberal = isLiberal;
    }
}

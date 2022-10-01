package enums;

/**
 * Models a policy card
 */
public enum Policy {
    LIBERAL(true),
    FASCIST(false);

    public final boolean isLiberal; //If the policy is LIBERAL

    /**
     * Constructor
     *
     * @param isLiberal true if the policy is LIBERAL, false if the policy is FASCIST
     */
    private Policy(boolean isLiberal) {
        this.isLiberal = isLiberal;
    }
}

package enums;

/**
 * Player role enum
 */
public enum Role {
    LIBERAL(false),
    FASCIST(false),
    HITLER(true);

    public final boolean isHitler; //If the role is HITLER

    /**
     * Constructor
     *
     * @param isHitler true if the role is HITLER, false if the role is LIBERAL or FASCIST
     */
    private Role(boolean isHitler) {
        this.isHitler = isHitler;
    }
}

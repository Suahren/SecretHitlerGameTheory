public enum Policy {
    LIBERAL(true),
    FASCIST(false);

    public final boolean val;

    private Policy(boolean val) {
        this.val = val;
    }
}

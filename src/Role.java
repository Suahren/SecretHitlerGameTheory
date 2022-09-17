public enum Role {
    LIBERAL(false),
    FASCIST(false),
    HITLER(true);

    public final boolean val;

    private Role(boolean val) {
        this.val = val;
    }
}

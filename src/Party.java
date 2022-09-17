public enum Party {
    LIBERAL(true),
    FASCIST(false);

    public final boolean val;

    private Party(boolean val) {
        this.val = val;
    }
}

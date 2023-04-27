package hwr.oop.poker;

public class Play {
    private final Player player;
    private final ChipValue chipCount;
    private final Type type;

    public Play(Player player, ChipValue chipValue, Type type) {
        this.player = player;
        this.chipCount = chipValue;
        this.type = type;
    }

    public Player player() {
        return player;
    }

    public ChipValue chipValue() {
        return chipCount;
    }

    public Type type() {
        return type;
    }

    public enum Type {BET, CALL}
}

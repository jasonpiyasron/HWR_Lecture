package hwr.oop.poker;

public class Play {
    private final Player player;
    private final ChipValue chipsTotal;
    private final ChipValue chipsAdded;
    private final Type type;

    public Play(Player player, ChipValue chipsTotal, ChipValue chipsAdded, Type type) {
        this.player = player;
        this.chipsTotal = chipsTotal;
        this.chipsAdded = chipsAdded;
        this.type = type;
    }

    public static Play fold(Player player) {
        return new Play(player, ChipValue.of(0), ChipValue.zero(), Type.FOLD);
    }

    public static Play check(Player player) {
        return new Play(player, ChipValue.zero(), ChipValue.zero(), Play.Type.CHECK);
    }

    public static Play raiseBy(Player player, ChipValue amount, ChipValue chipsAdded) {
        return new Play(player, amount, chipsAdded, Play.Type.RAISE);
    }

    public boolean playedBy(Player player) {
        return player.equals(this.player);
    }

    public Player player() {
        return player;
    }

    public ChipValue chipValue() {
        return chipsAdded;
    }

    public Type type() {
        return type;
    }

    public boolean increasedChips() {
        return type.hasIncreasedChipsInPod();
    }

    public ChipValue totalChipValue() {
        return chipsTotal;
    }

    public enum Type {
        BET(true), FOLD(false), RAISE(true), CHECK(false), CALL(true);

        private final boolean increasedChips;

        Type(boolean increasedChips) {
            this.increasedChips = increasedChips;
        }

        public boolean hasIncreasedChipsInPod() {
            return increasedChips;
        }
    }
}

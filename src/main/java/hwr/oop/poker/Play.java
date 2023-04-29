package hwr.oop.poker;

public class Play {
    private final Player player;
    private final ChipValue chipsTotal;
    private final ChipValue chipsAdded;
    private final Type type;

    private Play(Player player, ChipValue chipsTotal, ChipValue chipsAdded, Type type) {
        this.player = player;
        this.chipsTotal = chipsTotal;
        this.chipsAdded = chipsAdded;
        this.type = type;
    }

    public static Play fold(Player player) {
        return new Play(player, ChipValue.zero(), ChipValue.zero(), Type.FOLD);
    }

    public static Play check(Player player) {
        return new Play(player, ChipValue.zero(), ChipValue.zero(), Play.Type.CHECK);
    }

    public static Play bet(Player player, ChipValue amount) {
        return new Play(player, amount, amount, Play.Type.BET);
    }

    public static Play call(Player player, ChipValue target, ChipValue amount) {
        return new Play(player, target, amount, Play.Type.CALL);
    }

    public static Play raiseBy(Player player, ChipValue target, ChipValue amount) {
        return new Play(player, target, amount, Play.Type.RAISE);
    }

    public boolean playedBy(Player player) {
        return this.player.equals(player);
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

    public boolean isCheck() {
        return type == Type.CHECK;
    }

    public boolean isFold() {
        return type == Type.FOLD;
    }

    public ChipValue totalChipValue() {
        return chipsTotal;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Play{")
                .append(player).append(" ")
                .append(type);
        if (increasedChips()) {
            builder
                    .append(" added ").append(chipsAdded.value())
                    .append(" to ").append(chipsTotal.value());
        }
        builder.append("}");
        return builder.toString();
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

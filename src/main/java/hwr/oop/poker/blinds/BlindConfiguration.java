package hwr.oop.poker.blinds;

public class BlindConfiguration {

    private final SmallBlind smallBlind;
    private final BigBlind bigBlind;

    public BlindConfiguration(SmallBlind smallBlind) {
        this.smallBlind = smallBlind;
        this.bigBlind = this.smallBlind.bigBlind();
    }

    public BigBlind bigBlind() {
        return bigBlind;
    }

    public SmallBlind smallBlind() {
        return smallBlind;
    }
}

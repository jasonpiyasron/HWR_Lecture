package hwr.oop.poker.blinds;

import hwr.oop.poker.ChipValue;

import java.util.Objects;

public class SmallBlind implements ChipValue {

    private final long value;
    private final BigBlind bigBlind;

    public static SmallBlind of(long value) {
        return new SmallBlind(value);
    }

    private SmallBlind(long value) {
        this.value = value;
        this.bigBlind = new BigBlind(value * 2);
    }

    @Override
    public long value() {
        return value;
    }

    public BigBlind bigBlind() {
        return bigBlind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChipValue)) {
            return false;
        }
        ChipValue chipCount = (ChipValue) o;
        return value == chipCount.value();
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

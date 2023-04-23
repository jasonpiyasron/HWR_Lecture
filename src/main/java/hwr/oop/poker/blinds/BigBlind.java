package hwr.oop.poker.blinds;

import hwr.oop.poker.ChipValue;

import java.util.Objects;

public class BigBlind implements ChipValue {

    private final long value;

    public BigBlind(long value) {
        this.value = value;
    }

    @Override
    public long value() {
        return value;
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

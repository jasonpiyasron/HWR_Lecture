package hwr.oop.poker;

public interface ChipValue extends Comparable<ChipValue> {
    long value();

    @Override
    default int compareTo(ChipValue o) {
        return Long.compare(value(), o.value());
    }
}

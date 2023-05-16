package hwr.oop.poker;

import java.util.Objects;

public interface ChipValue extends Comparable<ChipValue> {
    static ChipValue of(long value) {
        return new SimpleChipValue(value);
    }

    static ChipValue zero() {
        return ChipValue.of(0);
    }

    static ChipValue minRaise(ChipValue bet) {
        return ChipValue.of(bet.value() * 2);
    }

    long value();

    default ChipValue minus(ChipValue other) {
        return ChipValue.of(value() - other.value());
    }

    default ChipValue plus(ChipValue other) {
        return ChipValue.of(value() + other.value());
    }

    @Override
    default int compareTo(ChipValue o) {
        return Long.compare(value(), o.value());
    }

    default boolean isLessThan(ChipValue minRaise) {
        return compareTo(minRaise) < 0;
    }

    class SimpleChipValue implements ChipValue {
        private final long value;

        public SimpleChipValue(long value) {
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
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SimpleChipValue that = (SimpleChipValue) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "ChipValue{" + value + '}';
        }
    }
}

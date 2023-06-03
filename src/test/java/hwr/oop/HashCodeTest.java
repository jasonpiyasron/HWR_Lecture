package hwr.oop;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HashCodeTest {

    @Test
    void name() {
        final List<Integer> first = List.of(1, 2, 3, 4);
        final List<Integer> second = List.of(1, 2, 3, 4);
        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);
    }
}

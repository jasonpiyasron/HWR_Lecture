package hwr.oop.poker.community.cards;

import hwr.oop.poker.Card;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CommunityCards {
    static CommunityCards empty() {
        return new SimpleCommunityCards();
    }

    static CommunityCardBuilder flop(Card... cards) {
        return new CommunityCardBuilder(Flop.of(Arrays.asList(cards)));
    }

    static CommunityCardBuilder flop(Flop flop) {
        return new CommunityCardBuilder(flop);
    }

    Collection<Card> cardsDealt();

    Optional<Flop> flop();

    Optional<Turn> turn();

    Optional<River> river();


    class CommunityCardBuilder {

        private final Flop flop;
        private Turn turn;

        public CommunityCardBuilder(Flop flop) {
            this.flop = flop;
        }

        public CommunityCardBuilder turn(Card card) {
            this.turn = Turn.of(card);
            return this;
        }

        public CommunityCards noTurnNoRiver() {
            return new SimpleCommunityCards(flop);
        }

        public CommunityCards noRiver() {
            return new SimpleCommunityCards(flop, turn);
        }
    }

    class SimpleCommunityCards implements CommunityCards {

        private final Flop flop;
        private final Turn turn;
        private final River river;

        public SimpleCommunityCards(Flop flop) {
            this(flop, null, null);
        }

        public SimpleCommunityCards(Flop flop, Turn turn, River river) {
            this.flop = flop;
            this.turn = turn;
            this.river = river;
        }

        public SimpleCommunityCards() {
            this(null, null, null);
        }

        public SimpleCommunityCards(Flop flop, Turn turn) {
            this(flop, turn, null);
        }

        @Override
        public Collection<Card> cardsDealt() {
            if (flop != null) {
                if (turn != null) {
                    return Stream.of(flop.cards(), List.of(turn.card()))
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
                } else {
                    return flop.cards();
                }
            } else {
                return List.of();
            }
        }

        @Override
        public Optional<Flop> flop() {
            return Optional.ofNullable(flop);
        }

        @Override
        public Optional<Turn> turn() {
            return Optional.ofNullable(turn);
        }

        @Override
        public Optional<River> river() {
            return Optional.ofNullable(river);
        }
    }
}

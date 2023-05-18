package hwr.oop.poker.community.cards;

import hwr.oop.poker.Card;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommunityCards {
    private final Flop flop;
    private final Turn turn;
    private final River river;

    public static CommunityCards empty() {
        return new CommunityCards();
    }

    public static CommunityCardBuilder flop(Card... cards) {
        return flop(Flop.of(Arrays.asList(cards)));
    }

    public static CommunityCardBuilder flop(Flop flop) {
        return new CommunityCardBuilder(flop);
    }

    private CommunityCards() {
        this(null, null, null);
    }

    private CommunityCards(Flop flop) {
        this(flop, null, null);
    }

    private CommunityCards(Flop flop, Turn turn) {
        this(flop, turn, null);
    }

    private CommunityCards(Flop flop, Turn turn, River river) {
        this.flop = flop;
        this.turn = turn;
        this.river = river;
    }

    public Collection<Card> cardsDealt() {
        return Stream.of(flop, turn, river)
                .filter(Objects::nonNull)
                .flatMap(CommunityCardProvider::cards)
                .collect(Collectors.toList());
    }

    public Optional<Flop> flop() {
        return Optional.ofNullable(flop);
    }

    public Optional<Turn> turn() {
        return Optional.ofNullable(turn);
    }

    public Optional<River> river() {
        return Optional.ofNullable(river);
    }


    public static class CommunityCardBuilder {

        private final Flop flop;
        private Turn turn;

        private CommunityCardBuilder(Flop flop) {
            assertFlopIsValid(flop);
            this.flop = flop;
        }

        private static void assertFlopIsValid(Flop flop) {
            if (flop == null) {
                throw new CannotCreateCommunityCardsException(
                        "Cannot build community cards with the flop being null." +
                                " If you want to create an empty Board of community cards, use CommunityCards.empty()"
                );
            }
        }

        public CommunityCardBuilder turn(Card card) {
            this.turn = Turn.of(card);
            return this;
        }

        public CommunityCardBuilder turn(Turn turn) {
            this.turn = turn;
            return this;
        }

        public CommunityCards noTurnNoRiver() {
            return new CommunityCards(flop);
        }

        public CommunityCards noRiver() {
            return new CommunityCards(flop, turn);
        }

        public CommunityCards river(Card card) {
            final River river = River.of(card);
            return new CommunityCards(flop, turn, river);
        }
    }

    private static class CannotCreateCommunityCardsException extends RuntimeException {
        public CannotCreateCommunityCardsException(String message) {
            super(message);
        }
    }

}

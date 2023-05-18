package hwr.oop.poker.community.cards;

import hwr.oop.poker.Card;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommunityCards {
    public static CommunityCards empty() {
        return new CommunityCards();
    }

    public static CommunityCardBuilder flop(Card... cards) {
        return flop(Flop.of(Arrays.asList(cards)));
    }

    public static CommunityCardBuilder flop(Flop flop) {
        return new CommunityCardBuilder(flop);
    }

    private final Flop flop;
    private final Turn turn;
    private final River river;

    public CommunityCards(Flop flop) {
        this(flop, null, null);
    }

    public CommunityCards(Flop flop, Turn turn, River river) {
        this.flop = flop;
        this.turn = turn;
        this.river = river;
    }

    public CommunityCards() {
        this(null, null, null);
    }

    public CommunityCards(Flop flop, Turn turn) {
        this(flop, turn, null);
    }

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

        public CommunityCardBuilder(Flop flop) {
            this.flop = flop;
        }

        public CommunityCardBuilder turn(Card card) {
            this.turn = Turn.of(card);
            return this;
        }

        public CommunityCards noTurnNoRiver() {
            return new CommunityCards(flop);
        }

        public CommunityCards noRiver() {
            return new CommunityCards(flop, turn);
        }
    }

}

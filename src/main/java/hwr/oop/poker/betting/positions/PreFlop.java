package hwr.oop.poker.betting.positions;

import hwr.oop.poker.Deck;
import hwr.oop.poker.community.cards.CommunityCards;

import java.util.Objects;
import java.util.Optional;

class PreFlop implements RoundPosition {

    @Override
    public int position() {
        return 0;
    }

    @Override
    public boolean shouldCauseBurn() {
        return false;
    }

    @Override
    public CommunityCards buildCardsFor(Deck deck, CommunityCards currentCards) {
        return CommunityCards.empty();
    }

    @Override
    public Optional<RoundPosition> nextPosition() {
        return Optional.of(RoundPosition.FLOP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoundPosition roundPosition = (RoundPosition) o;
        return position() == roundPosition.position();
    }

    @Override
    public int hashCode() {
        return Objects.hash(position());
    }
}

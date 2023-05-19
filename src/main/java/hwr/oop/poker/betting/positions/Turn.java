package hwr.oop.poker.betting.positions;

import hwr.oop.poker.Deck;
import hwr.oop.poker.community.cards.CommunityCards;
import hwr.oop.poker.community.cards.CommunityCardsProvider;

import java.util.Objects;
import java.util.Optional;

class Turn implements RoundPosition {

    @Override
    public int position() {
        return 2;
    }

    @Override
    public boolean shouldCauseBurn() {
        return true;
    }

    @Override
    public CommunityCardsProvider buildCardsFor(Deck deck, CommunityCardsProvider currentCards) {
        return CommunityCards
                .flop(currentCards.flop().orElseThrow())
                .turn(deck.draw())
                .noRiver();
    }

    @Override
    public Optional<RoundPosition> nextPosition() {
        return Optional.of(RoundPosition.RIVER);
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

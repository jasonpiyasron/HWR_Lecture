package hwr.oop.poker.betting.positions;


import hwr.oop.poker.Deck;
import hwr.oop.poker.community.cards.CommunityCards;

import java.util.Optional;

public interface RoundPosition extends Comparable<RoundPosition> {

    RoundPosition PRE_FLOP = new PreFlop();
    RoundPosition FLOP = new Flop();
    RoundPosition TURN = new Turn();
    RoundPosition RIVER = new River();

    int position();

    boolean shouldCauseBurn();

    CommunityCards buildCardsFor(Deck deck, CommunityCards currentCards);

    Optional<RoundPosition> nextPosition();

    default void ifRequiresBurn(Runnable runnable) {
        if (shouldCauseBurn()) {
            runnable.run();
        }
    }

    default RoundPosition latest(RoundPosition other) {
        if (position() > other.position()) {
            return this;
        } else {
            return other;
        }
    }

    @Override
    default int compareTo(RoundPosition o) {
        return Integer.compare(position(), o.position());
    }
}

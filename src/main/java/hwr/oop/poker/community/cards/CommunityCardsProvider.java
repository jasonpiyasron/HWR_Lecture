package hwr.oop.poker.community.cards;

import hwr.oop.poker.Card;

import java.util.Collection;
import java.util.Optional;

public interface CommunityCardsProvider {

    Collection<Card> cardsDealt();

    Optional<Flop> flop();

    Optional<Turn> turn();

    Optional<River> river();
}

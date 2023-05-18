package hwr.oop.poker.community.cards;

import hwr.oop.poker.Deck;
import hwr.oop.poker.Hand;
import hwr.oop.poker.betting.positions.RoundPosition;

public class CommunityCardFactory {

    private final CommunityCards cards;
    private final Hand hand;

    public static CommunityCardFactory basedOn(CommunityCards cards, Hand hand) {
        return new CommunityCardFactory(cards, hand);
    }

    private CommunityCardFactory(CommunityCards cards, Hand hand) {
        this.cards = cards;
        this.hand = hand;
    }

    public CommunityCards drawCardsFrom(Deck deck) {
        RoundPosition position = hand.currentPosition();
        position.ifRequiresBurn(deck::burn);
        return position.buildCardsFor(deck, cards);
    }

}

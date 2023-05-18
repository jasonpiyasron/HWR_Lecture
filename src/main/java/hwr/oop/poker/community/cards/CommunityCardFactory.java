package hwr.oop.poker.community.cards;

import hwr.oop.poker.Deck;
import hwr.oop.poker.Hand;

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
        if (hand.preFlopRoundPlayed()) {
            deck.burn();
            if (hand.flopRoundPlayed()) {
                if (hand.turnRoundPlayed()) {
                    return CommunityCards
                            .flop(cards.flop().orElseThrow())
                            .turn(cards.turn().orElseThrow())
                            .river(deck.draw());
                } else {
                    return CommunityCards
                            .flop(cards.flop().orElseThrow())
                            .turn(deck.draw())
                            .noRiver();
                }
            } else {
                return CommunityCards
                        .flop(deck.draw(), deck.draw(), deck.draw())
                        .noTurnNoRiver();
            }
        } else {
            return cards;
        }
    }
}

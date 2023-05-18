package hwr.oop.poker.community.cards;

import hwr.oop.poker.Card;

import java.util.stream.Stream;

public interface CommunityCardProvider {
    Stream<Card> cards();
}

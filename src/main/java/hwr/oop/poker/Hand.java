package hwr.oop.poker;

import hwr.oop.poker.betting.BettingRound;
import hwr.oop.poker.betting.positions.RoundPosition;
import hwr.oop.poker.blinds.BlindConfiguration;
import hwr.oop.poker.blinds.SmallBlind;
import hwr.oop.poker.community.cards.CommunityCards;
import hwr.oop.poker.community.cards.Flop;
import hwr.oop.poker.community.cards.River;
import hwr.oop.poker.community.cards.Turn;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class Hand {
    private final Deck deck;
    private final List<Player> players;
    private final BlindConfiguration blindConfiguration;
    private final Map<Player, List<Card>> holeCards;
    private final Map<RoundPosition, BettingRound> rounds;
    private final CommunityCards communityCards;

    public static Builder newBuilder() {
        return new Builder();
    }

    private Hand(Deck deck, List<Player> players, SmallBlind smallBlind) {
        this.deck = deck;
        this.players = players;
        this.blindConfiguration = new BlindConfiguration(smallBlind);
        this.holeCards = drawHoleCardsFromDeck();
        this.communityCards = CommunityCards.empty();
        this.rounds = Map.of(RoundPosition.PRE_FLOP, new BettingRound(players));
    }

    private Map<Player, List<Card>> drawHoleCardsFromDeck() {
        final Map<Player, List<Card>> mutableMap = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            for (Player player : this.players) {
                final List<Card> cards = mutableMap
                        .computeIfAbsent(player, p -> new ArrayList<>());
                final Card drawnCard = this.deck.draw();
                cards.add(drawnCard);
            }
        }
        return Collections.unmodifiableMap(mutableMap);
    }

    private Hand(Deck deck,
                 List<Player> players,
                 SmallBlind smallBlind,
                 Map<Player, List<Card>> holeCards,
                 Map<RoundPosition, BettingRound> rounds,
                 CommunityCards communityCards) {
        this.deck = deck;
        this.players = players;
        this.blindConfiguration = new BlindConfiguration(smallBlind);
        this.holeCards = holeCards;
        this.rounds = createMapBasedOn(rounds);
        this.communityCards = buildCommunityCards(deck, communityCards);
    }

    private CommunityCards buildCommunityCards(Deck deck, CommunityCards oldCommunityCards) {
        RoundPosition position = currentPosition();
        position.ifRequiresBurn(deck::burn);
        return position.buildCardsFor(deck, oldCommunityCards);
    }

    public List<Card> holeCards(Player player) {
        return holeCards.get(player);
    }

    public Player smallBlind() {
        return players.get(0);
    }

    public Player bigBlind() {
        return players.get(1);
    }

    public Player button() {
        return players.get(1);
    }

    public Player underTheGun() {
        return players.get(0);
    }

    public BlindConfiguration blindConfiguration() {
        return blindConfiguration;
    }

    public ChipValue potSize() {
        return () -> blindConfiguration.bigBlind().value() + blindConfiguration.smallBlind().value();
    }

    public Optional<Flop> flop() {
        return communityCards.flop();
    }

    public Optional<Turn> turn() {
        return communityCards.turn();
    }

    public Optional<River> river() {
        return communityCards.river();
    }

    public CommunityCards communityCards() {
        return communityCards;
    }

    public boolean preFlopRoundPlayed() {
        return isRoundPlayed(RoundPosition.PRE_FLOP);
    }

    public boolean flopRoundPlayed() {
        return isRoundPlayed(RoundPosition.FLOP);
    }

    public boolean turnRoundPlayed() {
        return isRoundPlayed(RoundPosition.TURN);
    }

    public boolean riverRoundPlayed() {
        return isRoundPlayed(RoundPosition.RIVER);
    }

    public Hand onCurrentRound(UnaryOperator<BettingRound> function) {
        final BettingRound round = roundOn(currentPosition()).orElseThrow();
        return accept(function.apply(round));
    }

    private Hand accept(BettingRound round) {
        final Builder copy = copy();
        final RoundPosition currentPosition = currentPosition();
        final var unmodifiableMap = createNewMapWith(round, currentPosition);
        copy.rounds(unmodifiableMap);
        return copy.build();
    }

    private Map<RoundPosition, BettingRound> createNewMapWith(BettingRound round, RoundPosition currentPosition) {
        final Map<RoundPosition, BettingRound> mutableMap = new HashMap<>(rounds);
        mutableMap.put(currentPosition, round);
        return Collections.unmodifiableMap(mutableMap);
    }

    private Builder copy() {
        return newBuilder()
                .deck(deck)
                .players(players)
                .smallBlind(blindConfiguration.smallBlind())
                .holeCards(holeCards)
                .rounds(rounds)
                .communityCards(communityCards);
    }

    private boolean isRoundPlayed(RoundPosition preFlop) {
        final Optional<BettingRound> optional = roundOn(preFlop);
        return optional.isPresent() && optional.orElseThrow().isFinished();
    }

    private Optional<BettingRound> roundOn(RoundPosition position) {
        final boolean containsKey = rounds.containsKey(position);
        if (!containsKey) {
            return Optional.empty();
        } else {
            final BettingRound round = rounds.get(position);
            return Optional.of(round);
        }
    }

    private Map<RoundPosition, BettingRound> createMapBasedOn(Map<RoundPosition, BettingRound> rounds) {
        Map<RoundPosition, BettingRound> modifiableMap = new HashMap<>(rounds);
        modifiableMap.putIfAbsent(currentPosition(rounds), new BettingRound(players));
        return Collections.unmodifiableMap(modifiableMap);
    }

    public RoundPosition currentPosition(Map<RoundPosition, BettingRound> someRounds) {

        final RoundPosition candidatePosition = someRounds.keySet().stream()
                .reduce(RoundPosition::latest).orElseThrow();
        final BettingRound candidateRound = someRounds.get(candidatePosition);
        if (candidateRound.isFinished()) {
            return candidatePosition.nextPosition().orElseThrow();
        } else {
            return candidatePosition;
        }
    }

    public RoundPosition currentPosition() {
        return currentPosition(rounds);
    }

    static class Builder {
        private Deck deck;
        private List<Player> players;
        private SmallBlind smallBlind;
        private Map<Player, List<Card>> holeCards;
        private Map<RoundPosition, BettingRound> rounds;
        private CommunityCards communityCards;

        private Builder() {
            this.deck = null;
            this.players = null;
            this.smallBlind = null;
            this.holeCards = null;
            this.communityCards = null;
            this.rounds = null;
        }

        Hand build() {
            final boolean hasIncompleteInfo = Stream.of(holeCards, rounds, communityCards).anyMatch(Objects::isNull);
            if (hasIncompleteInfo) {
                return new Hand(deck, players, smallBlind);
            } else {
                return new Hand(deck, players, smallBlind, holeCards, rounds, communityCards);
            }
        }

        public Builder deck(Deck deck) {
            this.deck = deck;
            return this;
        }

        public Builder players(List<Player> players) {
            this.players = players;
            return this;
        }

        public Builder smallBlind(SmallBlind smallBlind) {
            this.smallBlind = smallBlind;
            return this;
        }

        private Builder holeCards(Map<Player, List<Card>> holeCards) {
            this.holeCards = holeCards;
            return this;
        }

        private Builder rounds(Map<RoundPosition, BettingRound> rounds) {
            this.rounds = rounds;
            return this;
        }

        private Builder communityCards(CommunityCards communityCards) {
            this.communityCards = communityCards;
            return this;
        }
    }
}

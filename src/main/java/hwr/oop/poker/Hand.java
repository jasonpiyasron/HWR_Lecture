package hwr.oop.poker;

import hwr.oop.poker.betting.BettingRound;
import hwr.oop.poker.betting.positions.RoundPosition;
import hwr.oop.poker.blinds.BlindConfiguration;
import hwr.oop.poker.blinds.SmallBlind;
import hwr.oop.poker.community.cards.*;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class Hand implements CommunityCardsProvider {
    private final Deck deck;
    private final List<Player> players;
    private final BlindConfiguration blindConfiguration;
    private final HoleCards holeCards;
    private final Map<RoundPosition, BettingRound> rounds;
    private final CommunityCardsProvider communityCards;

    public static Builder newBuilder() {
        return new Builder();
    }

    private Hand(Deck deck, List<Player> players, SmallBlind smallBlind) {
        this.deck = deck;
        this.players = players;
        this.blindConfiguration = new BlindConfiguration(smallBlind);
        this.holeCards = HoleCards.create(deck, players);
        this.communityCards = CommunityCards.empty();
        this.rounds = Map.of(RoundPosition.PRE_FLOP, BettingRound.create(players));
    }

    private Hand(Deck deck,
                 List<Player> players,
                 SmallBlind smallBlind,
                 HoleCards holeCards,
                 Map<RoundPosition, BettingRound> rounds,
                 CommunityCardsProvider communityCards) {
        this.deck = deck;
        this.players = players;
        this.blindConfiguration = new BlindConfiguration(smallBlind);
        this.holeCards = holeCards;
        this.rounds = createMapBasedOn(rounds);
        this.communityCards = buildCommunityCards(deck, communityCards);
    }

    private CommunityCardsProvider buildCommunityCards(Deck deck, CommunityCardsProvider oldCommunityCards) {
        Optional<RoundPosition> optional = currentPosition();
        if (optional.isPresent()) {
            final RoundPosition position = optional.get();
            position.ifRequiresBurn(deck::burn);
            return position.buildCardsFor(deck, oldCommunityCards);
        } else {
            return oldCommunityCards;
        }
    }

    public List<Card> holeCards(Player player) {
        return holeCards.of(player);
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

    @Override
    public Optional<Flop> flop() {
        return communityCards.flop();
    }

    @Override
    public Optional<Turn> turn() {
        return communityCards.turn();
    }

    @Override
    public Optional<River> river() {
        return communityCards.river();
    }

    @Override
    public Collection<Card> cardsDealt() {
        return communityCards.cardsDealt();
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
        final Optional<RoundPosition> optional = currentPosition();
        if (optional.isPresent()) {
            final var position = optional.get();
            final var round = roundOn(position).orElseThrow();
            return accept(function.apply(round));
        } else {
            throw new PlayOnOnFinishedHandException();
        }
    }

    private Hand accept(BettingRound round) {
        final Builder copy = copy();
        final Optional<RoundPosition> optional = currentPosition();
        if (optional.isPresent()) {
            final RoundPosition position = optional.get();
            final var unmodifiableMap = createNewMapWith(round, position);
            copy.rounds(unmodifiableMap);
            return copy.build();
        } else {
            throw new PlayOnOnFinishedHandException();
        }
    }

    private Map<RoundPosition, BettingRound> createNewMapWith(BettingRound round, RoundPosition currentPosition) {
        final Map<RoundPosition, BettingRound> mutableMap =
                new HashMap<>(rounds);
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
        final boolean riverPresentAndFinished = rounds.containsKey(RoundPosition.RIVER) && rounds.get(RoundPosition.RIVER).isFinished();
        if (!riverPresentAndFinished) {
            final var roundPosition = currentPosition(rounds).orElseThrow();
            final var freshBettingRound = BettingRound.create(players);
            modifiableMap.putIfAbsent(roundPosition, freshBettingRound);
        }
        return Collections.unmodifiableMap(modifiableMap);
    }

    public Optional<RoundPosition> currentPosition(Map<RoundPosition, BettingRound> someRounds) {

        final RoundPosition candidatePosition = someRounds.keySet().stream()
                .reduce(RoundPosition::latest).orElseThrow();
        final BettingRound candidateRound = someRounds.get(candidatePosition);
        if (candidateRound.isFinished()) {
            return candidatePosition.nextPosition();
        } else {
            return Optional.of(candidatePosition);
        }
    }

    public Optional<RoundPosition> currentPosition() {
        return currentPosition(rounds);
    }

    public boolean isFinished() {
        if (currentPosition().isEmpty()) {
            return true;
        } else {
            final RoundPosition position = currentPosition().orElseThrow();
            final BettingRound currentRound = rounds.get(position);
            return currentRound.isFinished();
        }
    }

    public static class Builder {
        private Deck deck;
        private List<Player> players;
        private SmallBlind smallBlind;
        private HoleCards holeCards;
        private Map<RoundPosition, BettingRound> rounds;
        private CommunityCardsProvider communityCards;

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

        private Builder holeCards(HoleCards holeCards) {
            this.holeCards = holeCards;
            return this;
        }

        private Builder rounds(Map<RoundPosition, BettingRound> rounds) {
            this.rounds = rounds;
            return this;
        }

        private Builder communityCards(CommunityCardsProvider communityCards) {
            this.communityCards = communityCards;
            return this;
        }
    }

    private class PlayOnOnFinishedHandException extends RuntimeException {
    }
}

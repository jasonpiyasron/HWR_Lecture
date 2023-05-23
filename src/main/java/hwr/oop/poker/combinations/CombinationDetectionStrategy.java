package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;
import hwr.oop.poker.Combination;

import java.util.List;

public interface CombinationDetectionStrategy {

    Result match(List<Card> cards);

    class Result {
        private final List<List<Card>> candidates;
        private final Combination.Label label;

        static Result success(Combination.Label label, List<List<Card>> alternatives) {
            return new Result(alternatives, label);
        }

        static Result failure(Combination.Label label) {
            return new Result(null, label);
        }


        private Result(List<List<Card>> candidates, Combination.Label label) {
            this.candidates = candidates;
            this.label = label;
        }

        public Combination.Label label() {
            return label;
        }

        public List<List<Card>> alternatives() {
            return candidates;
        }

        public List<Card> winner() {
            if (successful()) {
                return candidates.get(0);
            } else {
                throw new IllegalStateException("Cannot retrieve Cards from unsuccessful show down matching");
            }
        }

        public boolean successful() {
            return candidates != null;
        }

    }

}

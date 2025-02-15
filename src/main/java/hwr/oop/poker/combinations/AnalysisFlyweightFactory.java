package hwr.oop.poker.combinations;

import hwr.oop.poker.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AnalysisFlyweightFactory {

    private final Map<Integer, AnalysisFlyweight> map;

    public AnalysisFlyweightFactory() {
        this.map = new HashMap<>();
    }

    public AnalysisFlyweight get(List<Card> cards) {
        final int hashCode = cards.hashCode();
        if (map.containsKey(hashCode)) {
            return map.get(hashCode);
        } else {
            final AnalysisFlyweight analysisFlyweight = AnalysisFlyweight.create(cards);
            map.put(hashCode, analysisFlyweight);
            return analysisFlyweight;
        }
    }
}

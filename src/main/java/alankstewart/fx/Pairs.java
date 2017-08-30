package alankstewart.fx;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public final class Pairs {

    private final Map<String, BigDecimal> pairsRates = new HashMap<>();

    public Pairs(final Map<String, BigDecimal> pairsRates) {
        Objects.requireNonNull(pairsRates, "Currency pairs and rates must not be null");
        this.pairsRates.putAll(pairsRates);
    }

    public BigDecimal getRate(final String base, final String terms) {
        final BigDecimal rate = getRateInternal(base, terms);
        return rate.compareTo(BigDecimal.ZERO) == 0 ? getCrossCurrencyRate(base, terms) : rate;
    }

    private BigDecimal getRateInternal(String base, String terms) {
        if (Objects.equals(base, terms)) { // 1:1
            return BigDecimal.ONE.setScale(4, RoundingMode.HALF_UP);
        } else if (pairsRates.containsKey(base + terms)) { // Direct feed
            return pairsRates.get(base + terms);
        } else if (pairsRates.containsKey(terms + base)) { // Inverted
            return BigDecimal.ONE.divide(pairsRates.get(terms + base), 4, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getCrossCurrencyRate(String base, String terms) {
        // Finding all possible combinations would probably be something like Set<List<String>> where each
        // element in the Set is a List of conversions needed. So your example for AUDCZK would be
        // a Set with one element, List<String> and the List<String> would have three
        // elements: AUDUSD, EURUSD, EURCZK. At the end of getRateCross() it'd just find the Set element
        // with the smallest List size and use that, knowing it can call getRateInternal() and then multiply
        // each of the resulting BigDecimals to get a final conversion factor.
        Set<List<String>> conversions = new HashSet<>();
        for (Map.Entry<String, BigDecimal> entry : pairsRates.entrySet()) {
            if (entry.getKey().startsWith(base)) {

            }
        }
        Map<String, BigDecimal> ccy = pairsRates.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(base))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return BigDecimal.ZERO;
    }
}

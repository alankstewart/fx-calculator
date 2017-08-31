package alankstewart.fx;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.math.RoundingMode.HALF_UP;

public final class CurrencyPairs {

    private final Map<String, BigDecimal> pairRates = new HashMap<>();

    public CurrencyPairs(final Map<String, BigDecimal> pairRates) {
        Objects.requireNonNull(pairRates, "Currency pairs and rates must not be null");
        this.pairRates.putAll(pairRates);
    }

    public BigDecimal getRate(final String base, final String terms) {
        try {
            final BigDecimal rate = getRateInternal(base, terms);
            return rate.compareTo(BigDecimal.ZERO) == 0 ? getCrossCurrencyRate(base, terms) : rate;
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Unable to find rate for %s/%s", base, terms));
        }
    }

    private BigDecimal getRateInternal(String base, String terms) {
        if (Objects.equals(base, terms)) { // 1:1
            return BigDecimal.ONE.setScale(4, HALF_UP);
        } else if (pairRates.containsKey(base + terms)) { // Direct feed
            return pairRates.get(base + terms);
        } else if (pairRates.containsKey(terms + base)) { // Inverted
            return BigDecimal.ONE.divide(pairRates.get(terms + base), 4, HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getCrossCurrencyRate(String base, String terms) {
        final String side1 = findOppositeSide(base);
        final String side2 = findOppositeSide(terms);

        if (side1.equals(side2)) {
            // Single cross (eg AUDCAD = AUDUSD then CADUSD)
            final BigDecimal a = getRateInternal(base, side1);
            final BigDecimal b = getRateInternal(side2, terms);
            return a.multiply(b).setScale(4, HALF_UP);
        }

        // Multi-cross (eg AUDNOK = AUDUSD then EURUSD then EURNOK).
        // The sample data currencies are always satisfied by at most two crosses
        final BigDecimal a = getRateInternal(base, side1);
        final BigDecimal b = getRateInternal(side2, terms);
        final BigDecimal c = getRateInternal(side1, side2);
        return a.multiply(b).multiply(c).setScale(4, HALF_UP);
    }

    private String findOppositeSide(final String currency) {
        for (final String pair : pairRates.keySet()) {
            if (pair.startsWith(currency)) {
                return pair.substring(3);
            }
            if (pair.endsWith(currency)) {
                return pair.substring(0, 3);
            }
        }
        throw new IllegalArgumentException("Unknown currency");
    }
}

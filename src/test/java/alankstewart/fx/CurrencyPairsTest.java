package alankstewart.fx;

import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

public class CurrencyPairsTest {

    private static CurrencyPairs currencyPairs;

    @BeforeClass
    public static void onlyOnce() throws Exception {
        currencyPairs = new CurrencyPairs(new FxCalculator().readCurrencyPairRateData());
    }

    @Test
    public void shouldGetOneToOneRate() throws Exception {
        BigDecimal rate = currencyPairs.getRate("AUD", "AUD");
        assertTrue(rate.compareTo(new BigDecimal("1.0000")) == 0);
    }

    @Test
    public void shouldGetDirectRate() throws Exception {
        BigDecimal rate = currencyPairs.getRate("AUD", "USD");
        assertTrue(rate.compareTo(new BigDecimal("0.8371")) == 0);
    }

    @Test
    public void shouldGetInvertedRate() throws Exception {
        BigDecimal rate = currencyPairs.getRate("USD", "AUD");
        assertTrue(rate.compareTo(new BigDecimal("1.1946")) == 0);
    }

    @Test
    public void shouldGetCrossCurrencyRate() throws Exception {
        BigDecimal rate = currencyPairs.getRate("AUD", "JPY");
        assertTrue(rate.compareTo(new BigDecimal("100.4101")) == 0);
    }

    @Test
    public void shouldGetCrossCurrencyRate2() throws Exception {
        BigDecimal rate = currencyPairs.getRate("AUD", "CZK");
        assertTrue(rate.compareTo(new BigDecimal("18.7623")) == 0);
    }

    @Test
    public void shouldGetInvertedCrossCurrencyRate() throws Exception {
        BigDecimal rate = currencyPairs.getRate("JPY", "AUD");
        assertTrue(rate.compareTo(new BigDecimal("0.0099")) == 0);
    }

    @Test
    public void shouldGetInvertedCrossCurrencyRate2() throws Exception {
        BigDecimal rate = currencyPairs.getRate("NOK", "CAD");
        assertTrue(rate.compareTo(new BigDecimal("0.1631")) == 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToFindRateForUnknownCurrencies() {
        currencyPairs.getRate("KRW", "FJD");
    }
}

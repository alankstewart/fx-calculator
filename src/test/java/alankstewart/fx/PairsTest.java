package alankstewart.fx;

import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

public class PairsTest {

    private static Pairs pairs;

    @BeforeClass
    public static void onlyOnce() throws Exception {
        pairs = new Pairs(new FxCalculator().readPairsData());
    }

    @Test
    public void shouldGetOneToOneRate() throws Exception {
        BigDecimal rate = pairs.getRate("AUD", "AUD");
        assertTrue(rate.compareTo(new BigDecimal("1.0000")) == 0);
    }

    @Test
    public void shouldGetDirectRate() throws Exception {
        BigDecimal rate = pairs.getRate("AUD", "USD");
        assertTrue(rate.compareTo(new BigDecimal("0.8371")) == 0);
    }

    @Test
    public void shouldGetInvertedRate() throws Exception {
        BigDecimal rate = pairs.getRate("USD", "AUD");
        assertTrue(rate.compareTo(new BigDecimal("1.1946")) == 0);
    }

    @Test
    public void shouldGetCrossCurrencyRate() throws Exception {
      //  BigDecimal rate = pairs.getRate("EUR", "JPY");
        BigDecimal rate = pairs.getRate("AUD", "JPY");
        assertTrue(rate.compareTo(new BigDecimal("100.41")) == 0);
    }

    @Test
    public void shouldGetInvertedCrossCurrencyRate() throws Exception {
        BigDecimal rate = pairs.getRate("JPY", "AUD");
        assertTrue(rate.compareTo(new BigDecimal("0.0010")) == 0);
    }
}

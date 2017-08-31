package alankstewart.fx;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toMap;

public class FxCalculator {

    public static void main(String args[]) {
        System.out.println("FX Calculator\n");
        FxCalculator calc = new FxCalculator();
        calc.convertCurrencyAmount();
    }

    private void convertCurrencyAmount() {
        printUsage();

        final CurrencyPairs currencyPairs = new CurrencyPairs(readCurrencyPairRateData());
        final Map<String, Integer> currencyData = readCurrencyData();
        final Pattern p = Pattern.compile("(^[A-Za-z]{3}) ([0-9]+)(\\.[0-9]+)? in ([A-Za-z]{3}$)");
        final Scanner scanner = new Scanner(System.in);
        while (true) {
            final String line = scanner.nextLine();
            if ("q".equals(line) || "exit".equals(line)) {
                break;
            }

            final Matcher m = p.matcher(line);
            if (m.matches()) {
                final String base = m.group(1).toUpperCase();
                final String terms = m.group(4).toUpperCase();
                try {
                    final BigDecimal amount = new BigDecimal(m.group(2) +
                            Optional.ofNullable(m.group(3)).orElse(""));
                    BigDecimal rate = currencyPairs.getRate(base, terms);
                    int precision = currencyData.get(terms);
                    final BigDecimal convertedAmount = rate.multiply(amount).setScale(precision, RoundingMode.HALF_UP);

                    System.out.printf("%s %s = %s %s\n", base, amount, terms, convertedAmount);
                } catch (Exception e) {
                    System.err.printf("Unable to find rate for %s/%s\n", base, terms);
                }
            } else {
                printUsage();
            }
        }
    }

    private void printUsage() {
        System.out.print("Usage: <ccy1> <amount> in <ccy2> (e.g. AUD 100.00 in USD) ");
        System.out.println("or enter 'q' or 'exit' to quit");
    }

    Map<String, BigDecimal> readCurrencyPairRateData() {
        return readFile("pairs_rates.dat")
                .stream()
                .map(line -> line.split("="))
                .collect(toMap(
                        p -> p[0].toUpperCase(),
                        p -> new BigDecimal(p[1])
                ));
    }

    private Map<String, Integer> readCurrencyData() {
        return readFile("currency.dat")
                .stream()
                .map(line -> line.split("="))
                .collect(toMap(
                        c -> c[0].toUpperCase(),
                        c -> Integer.parseInt(c[1])
                ));
    }

    private List<String> readFile(final String fileName) {
        try {
            final URL resource = getClass().getClassLoader().getResource(fileName);
            Objects.requireNonNull(resource, "Failed to load resource " + fileName);
            return Files.readAllLines(Paths.get(resource.toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

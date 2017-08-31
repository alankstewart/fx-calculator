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
        usage();
        try {
            final Pairs pairs = new Pairs(readPairsData());
            final Map<String, Integer> currencyData = readCurrencyData();
            final Pattern p = Pattern.compile("(^[A-Za-z]{3}) ([0-9]+)(\\.[0-9]+)? in ([A-Za-z]{3}$)");
            final Scanner scanner = new Scanner(System.in);
            while (true) {
                final String line = scanner.nextLine();
                if ("q".equals(line) || "quit".equals(line)) {
                    break;
                }

                final Matcher m = p.matcher(line);
                if (m.matches()) {
                    final String base = m.group(1).toUpperCase();
                    final BigDecimal amount = new BigDecimal(m.group(2) +
                            Optional.ofNullable(m.group(3)).orElse(""));
                    final String terms = m.group(4).toUpperCase();
                    final BigDecimal convertedAmount = pairs.getRate(base, terms)
                            .multiply(amount)
                            .setScale(currencyData.get(terms), RoundingMode.HALF_UP);

                    System.out.printf("%s %s = %s %s\n", base, amount, convertedAmount, terms);
                } else {
                    usage();
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void usage() {
        System.out.println("Usage: <ccy1> <amount> in <ccy2> (e.g. AUD 100.0 in USD)");
        System.out.println("       'q' or 'quit' to exit");
    }

    Map<String, BigDecimal> readPairsData() {
        return readFile("pairs_rates.dat")
                .stream()
                .map(line -> line.split("="))
                .collect(toMap(p -> p[0].toUpperCase(), p -> new BigDecimal(p[1])));
    }

    private Map<String, Integer> readCurrencyData() {
        return readFile("currency.dat")
                .stream()
                .map(line -> line.split("="))
                .collect(toMap(c -> c[0].toUpperCase(), c -> Integer.parseInt(c[1])));
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

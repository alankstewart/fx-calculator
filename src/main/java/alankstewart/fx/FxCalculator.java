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
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class FxCalculator {

    public static void main(String args[]) {
        System.out.println("FX Calculator\n");
        FxCalculator calc = new FxCalculator();
        calc.convertCurrency();
    }

    private void convertCurrency() {
        usage();
        try {
            Pairs pairs = readPairsData();
            Map<String, Integer> currencyData = readCurrencyData();
            final Pattern p = Pattern.compile("(^[A-Z]{3}) ([0-9]+)(\\.[0-9]+)? in ([A-Z]{3}$)");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if ("q".equals(line) || "quit".equals(line)) {
                    break;
                }

                Matcher m = p.matcher(line);
                if (m.matches()) {
                    String base = m.group(1);
                    BigDecimal amount = new BigDecimal(m.group(2) + Optional.ofNullable(m.group(3)).orElse(""));
                    String terms = m.group(4);
                    BigDecimal convertedAmount = pairs.getRate(base, terms)
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

    Pairs readPairsData() throws URISyntaxException, IOException {
        Map<String, BigDecimal> pairsRates = readFile("pairs_rates.dat")
                .stream()
                .map(l -> l.split("="))
                .collect(toMap(p -> p[0], p -> new BigDecimal(p[1])));
        return new Pairs(pairsRates);
    }

    private Map<String, Integer> readCurrencyData() throws URISyntaxException, IOException {
        return readFile("currency.dat")
                .stream()
                .map(l -> l.split("="))
                .collect(toMap(c -> c[0], c -> Integer.parseInt(c[1])));
    }

    private List<String> readFile(String fileName) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        Objects.requireNonNull(resource, "Failed to load resource " + fileName);
        try (final Stream<String> lines = Files.lines(Paths.get(resource.toURI()))) {
            return lines.collect(toList());
        }
    }
}

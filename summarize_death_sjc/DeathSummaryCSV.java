
//usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 16
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;

public class DeathSummaryCSV {

    private static final String SUMMARY_CSV = "summary.csv";

    private static final Path OUT_PATH = Paths.get(SUMMARY_CSV);

    private static final String[] BASE_HEADER = {"MONTH", "FUNERAL"};

    private static final int DATE_IDX = 2;
    private static final int FUNERAL_IDX = 4;

    private static final int FUNERAL_OUT_IDX = 1;
    private static final int MONTH_OUT_IDX = 0;

    record DeathRegister(String month, String year, String funeral) {

    }

    public static void main(String... args) throws Exception {
        var baseDir = args.length > 0 ? args[0] : ".";
        var requestedYears = args.length > 1 ? asList(args[1].split(",")) : emptyList();

        var registers = Files.walk(Paths.get(baseDir))
                             .filter(Files::isRegularFile)
                             .filter(p -> !SUMMARY_CSV.equals(p.toFile().getName()))
                             .filter(p -> requestedYears.isEmpty() ||
                                          requestedYears.contains(p.getParent().getParent().toFile().getName()))
                             .filter(p -> p.toString().endsWith("csv"))
                             .parallel()
                             .flatMap(DeathSummaryCSV::lines)
                             .map(DeathSummaryCSV::lineToRegister)
                             .collect(Collectors.toList());

        var header = Stream.concat(stream(BASE_HEADER),
                                   registers.stream().map(DeathRegister::year).distinct())
                           .collect(Collectors.toList());
        
        registers.sort((r1, r2) -> r1.month.compareTo(r2.month));
        
        var body = registers.stream().map(r -> {
            var lineParts = new String[header.size()];
            Arrays.fill(lineParts, "0");
            lineParts[MONTH_OUT_IDX] = r.month();
            lineParts[FUNERAL_OUT_IDX] = r.funeral();
            // fill year index with 1
            lineParts[header.indexOf(r.year())] = "1";
            return String.join(",", lineParts);
        }).collect(Collectors.joining("\n"));

        Files.writeString(OUT_PATH, String.join(",", header.toArray(String[]::new)) + "\n" + body);
    }

    private static DeathRegister lineToRegister(String line) {
        var parts = line.replaceAll("\"", "").split(",");
        var dateParts = parts[DATE_IDX].split("/");
        var funeral = "DIRETO".equalsIgnoreCase(parts[FUNERAL_IDX]) ? "DIRETO" : "-";
        return new DeathRegister(dateParts[1], dateParts[2], funeral);
    }

    private static Stream<String> lines(Path path) {
        try {
            return Files.lines(path).skip(1);
        } catch (IOException e) {
            System.out.println("Not able to read " + path);
            return Stream.empty();
        }
    }

}
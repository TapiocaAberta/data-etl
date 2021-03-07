
//usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS commons-io:commons-io:2.6
//JAVA 16
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

public class SummarizeFuelPrice {

    private static final String LIST = "url_list";
    private static final String OUT_DIR = "downloaded";
    private static final String IN_DIR = "input";
    private static final Path OUT_PATH = Paths.get(OUT_DIR);
    private static final Path IN_PATH = Paths.get(IN_DIR);

    private static final Path PROCESSED_OUT_PATH = Paths.get("processed.csv");

    private static final int IDX_DATE = 6;
    private static final int IDX_STATE = 1;
    private static final int IDX_TYPE = 5;
    private static final int IDX_VALUE = 7;

    private static final String CSV_TEXT_QUOTE = "\"";
    private static final String CSV_DELIMITER = ",";
    private static final String CSV_HEADER = "DAY,MONTH,STATE,TYPE,MIN,AVG,MAX";

    enum Options {
        DOWNLOAD,
        PROCESS;
    }

    record FuelPriceGroup(String year,
                          String month,
                          String state,
                          String type) {
    }

    record FuelPriceSummary(FuelPriceGroup group, double min, double average, double max) {
    }

    public static void main(String args[]) throws Exception {
        if (args.length == 0) {
            exit("Please provide a valid option: " + Arrays.toString(Options.values()));
        }

        var op = loadOption(args).orElseThrow(() -> new RuntimeException("Unknown option"));

        switch (op) {
            case DOWNLOAD:
                downloadFiles();
                break;
            case PROCESS:
                processInputFiles();
                break;
            default:
                exit("Something weird happened");
                break;
        }

    }

    private static void exit(String message) {
        System.out.println(message);
        System.exit(0);
    }

    private static void downloadFiles() throws IOException {
        var list = Paths.get(LIST);
        if (!list.toFile().exists()) {
            exit("File " + LIST + " not found. Create it with line separated urls to be downloaded.");
        }
        Files.lines(list).forEach(SummarizeFuelPrice::download);
    }

    private static void processInputFiles() throws Exception {

        if (!IN_PATH.toFile().exists() || Files.walk(IN_PATH).findFirst().isEmpty()) {
            exit("No files to process in dir " + IN_PATH);
        }

        // Read all the record in parallel way and group the fuel data by month - year - state - type 
        var groupedRecord = new HashMap<FuelPriceGroup, List<Double>>();
        var t = System.currentTimeMillis();
        System.out.println("Start processing... ");
        Files.walk(IN_PATH)
             .filter(f -> f.toString().toLowerCase().endsWith("csv"))
             .parallel()
             .flatMap(SummarizeFuelPrice::readFile)
             .forEach(record -> addNewRecord(groupedRecord, record));
        var totalTime = (System.currentTimeMillis() - t) / 1000;

        System.out.println("Processed %d records in %d seconds".formatted(groupedRecord.size(), totalTime));

        // Get a summary of grouped data and transform to CSV rows
        var csvBody = groupedRecord.entrySet()
                                   .stream()
                                   .map(entry -> {
                                       var summary = entry.getValue().stream().mapToDouble(v -> v).summaryStatistics();
                                       return new FuelPriceSummary(entry.getKey(), summary.getMin(), summary.getAverage(), summary.getMax());
                                   })
                                   .map(SummarizeFuelPrice::toCsvRow)
                                   .collect(Collectors.joining("\n"));
        var csvContent = CSV_HEADER + "\n" + csvBody;

        Files.deleteIfExists(PROCESSED_OUT_PATH);
        Files.writeString(PROCESSED_OUT_PATH, csvContent, StandardCharsets.ISO_8859_1);

    }

    private static String toCsvRow(SummarizeFuelPrice.FuelPriceSummary record) {
        return String.join(CSV_DELIMITER,
                           csvText(record.group.month),
                           csvText(record.group.year),
                           csvText(record.group.state),
                           csvText(record.group.type),
                           csvNumber(record.min),
                           csvNumber(record.average),
                           csvNumber(record.max));
    }

    private static Stream<String> readFile(Path file) {
        try {
            return Files.readAllLines(file, StandardCharsets.ISO_8859_1).stream().skip(1);
        } catch (Exception e) {
            System.out.println("Error reading file: " + file.toString());
            System.out.println("Error: " + e.getMessage());
            return Stream.empty();
        }
    }

    private static void addNewRecord(HashMap<FuelPriceGroup, List<Double>> groupedData, String line) {
        var records = line.split("\\t|;");
        if (records.length >= IDX_VALUE) {
            try {
                var dateParts = records[IDX_DATE].split("/");
                var priceStr = records[IDX_VALUE].replaceAll("\\,", ".").replaceAll("[^\\d.]", "");
                var price = NumberFormat.getInstance().parse(priceStr).doubleValue();
                String month = dateParts[1];
                String year = dateParts[2];
                String state = records[IDX_STATE];
                String type = records[IDX_TYPE];
                var group = new FuelPriceGroup(year, month, state, type);
                groupedData.putIfAbsent(group, new ArrayList<Double>());
                groupedData.get(group).add(price);
            } catch (ParseException e) {
                System.out.println("Error processing value, skipping processing. Line: " + line);
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("skipping line from processing: " + line);
        }
    }

    private static Optional<Options> loadOption(String... args) {
        try {
            return Optional.of(Options.valueOf(args[0]));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static void download(String url) {
        try {
            System.out.println("Now Downloading: " + url);
            var parts = url.split("/");
            var target = OUT_PATH.resolve(parts[parts.length - 1]);
            FileUtils.copyURLToFile(new URL(url), target.toFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String csvText(String s) {
        return CSV_TEXT_QUOTE + "%s\"".formatted(s);
    }

    private static String csvNumber(Number number) {
        return number.toString();
    }

}
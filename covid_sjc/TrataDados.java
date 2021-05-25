//usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 16
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrataDados {

    private static final Path INPUT = Paths.get("input.csv");
    private static final Path OUTPUT = Paths.get("output.csv");
    private static final String SEPARATOR = ",";
    private static final String HEADER = String.join(SEPARATOR,
		                                     "REGISTER_DATE",
						     "CASES",
						     "DEATHS");

    // CSV IDX
    private static final int DATE_IDX = 1;
    private static final int CASES_IDX = 7;
    private static final int DEATHS_IDX = 10;

    public static void main(String args[]) throws Exception {
        var body = Files.lines(INPUT)
	     .skip(1)
	     .map(l -> l.split(SEPARATOR))
	     .map(l -> String.join(SEPARATOR, 
				   l[DATE_IDX],
				   l[CASES_IDX], 
			           l[DEATHS_IDX]))
	     .collect(Collectors.joining("\n"));	    
	var output = HEADER + "\n" + body;
	Files.writeString(OUTPUT, output);
    }
}

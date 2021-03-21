//DEPS commons-io:commons-io:2.6
//JAVA 16


import java.nio.file.*;
import org.apache.commons.io.FileUtils;
import java.net.URL;
public class BaixaArquivosTransferencia {

	private static final String BASE_URL = "http://www.portaltransparencia.gov.br/download-de-dados/transferencias/%s";

	private static final String NAME_TEMPLATE = "%d%02d";

	private static final Path OUT_PATH = Paths.get("downloaded");

	record Bound (int month, int year){}

	public static void main(String... args) throws Exception{
		if (args.length < 2) {
			exit("Provide the from and to date in format MM-yyyy");
		}
		var from = toBound(args[0]);
		var to = toBound(args[1]);
		System.out.println(from + " - " + to);
		if (from.year > to.year || to.month > 12 || from.month > 12) exit("Invalid date range");
                if (! OUT_PATH.toFile().exists()){
			Files.createDirectory(OUT_PATH);
		}
		for (int i = from.year; i <= to.year; i++) {
		     var fromMonth = 1;	
		     var toMonth = 12;
		     if (i == from.year) fromMonth = from.month;
		     if (i == to.year) toMonth = to.month;
		     for (int j = fromMonth;j <= toMonth; j++) {
			  var name = NAME_TEMPLATE.formatted(i, j);
		          var url = BASE_URL.formatted(name);
			  var outFile = OUT_PATH.resolve(name + ".zip").toFile();
			  System.out.println("Now downloading " + name + "...");
                          FileUtils.copyURLToFile(new URL(url), outFile);
		     }
		}

	}

	static void exit(String msg) {
	    System.out.println(msg);
	    System.exit(0);
	}

	static Bound toBound(String arg) {
		var parts = arg.split("-");
		return new Bound(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}

}

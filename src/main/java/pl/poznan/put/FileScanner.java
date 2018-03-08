package pl.poznan.put;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import pl.poznan.put.db.File;
import pl.poznan.put.db.service.DatabaseService;

@Slf4j
public final class FileScanner {
  private static final Option PATH_OPTION =
      Option.builder("p")
          .longOpt("path")
          .desc("Base path which will be searched recursively for mmCIF files")
          .required()
          .numberOfArgs(1)
          .type(java.io.File.class)
          .build();
  private static final Options OPTIONS = new Options().addOption(FileScanner.PATH_OPTION);

  public static void main(final String[] args) throws ParseException, IOException {
    final CommandLineParser parser = new DefaultParser();
    final CommandLine commandLine = parser.parse(FileScanner.OPTIONS, args);
    final FileScanner scanner =
        new FileScanner(
            (java.io.File) commandLine.getParsedOptionValue(FileScanner.PATH_OPTION.getOpt()));
    scanner.scan();
  }

  private final java.io.File baseDirectory;

  private FileScanner(final java.io.File baseDirectory) {
    super();
    this.baseDirectory = baseDirectory;
  }

  private void scan() {
    final DatabaseService service = new DatabaseService();

    try {
      final Iterator<java.io.File> iterator =
          FileUtils.iterateFiles(baseDirectory, new String[] {"cif"}, true);

      while (iterator.hasNext()) {
        final java.io.File file = iterator.next();
        FileScanner.log.debug("Processing file: {}", file);

        final Optional<File> optional = service.getFileByPath(file.getAbsolutePath());
        if (!optional.isPresent()) {
          final File fileDb = new File();
          fileDb.setPath(file.getAbsolutePath());

          service.beginTransaction();
          service.persist(fileDb);
          service.commit();
        }
      }

      FileScanner.log.info("Successfully finished scanning filesystem");
    } finally {
      service.close();
    }
  }
}

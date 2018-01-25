package pl.poznan.put;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import pl.poznan.put.db.service.FileService;
import pl.poznan.put.utility.ExecHelper;

public final class FileScanner {
  private static final Option PATH_OPTION =
      Option.builder("p")
          .longOpt("path")
          .desc("Base path which will be searched recursively for mmCIF files")
          .required()
          .numberOfArgs(1)
          .type(File.class)
          .build();
  private static final Options OPTIONS = new Options().addOption(FileScanner.PATH_OPTION);

  public static void main(final String[] args) throws ParseException, IOException {
    final CommandLineParser parser = new DefaultParser();
    final CommandLine commandLine = parser.parse(FileScanner.OPTIONS, args);
    final FileScanner scanner =
        new FileScanner((File) commandLine.getParsedOptionValue(FileScanner.PATH_OPTION.getOpt()));
    scanner.scan();
  }

  private final File baseDirectory;

  private FileScanner(final File baseDirectory) {
    super();
    this.baseDirectory = baseDirectory;
  }

  private void scan() throws IOException {
    final FileService manager = new FileService();

    try {
      final Iterator<File> iterator =
          FileUtils.iterateFiles(baseDirectory, new String[] {"cif.gz"}, true);

      while (iterator.hasNext()) {
        final File file = iterator.next();
        final String stdout = ExecHelper.execute("md5sum", file.getAbsolutePath());
        final String checksum = stdout.split("\\s")[0];

        final Optional<pl.poznan.put.db.File> optional =
            manager.getFileByPath(file.getAbsolutePath());
        if (!optional.isPresent()) {
          final pl.poznan.put.db.File fileDb = new pl.poznan.put.db.File();
          fileDb.setPath(file.getAbsolutePath());
          fileDb.setChecksum(checksum);
          manager.getTransaction().begin();
          manager.persist(fileDb);
          manager.getTransaction().commit();
        }
      }
    } finally {
      manager.close();
    }
  }
}

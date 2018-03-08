package pl.poznan.put;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import pl.poznan.put.db.File;
import pl.poznan.put.db.Model;
import pl.poznan.put.db.Structure;
import pl.poznan.put.db.service.DatabaseService;
import pl.poznan.put.pdb.PdbParsingException;

@Slf4j
public final class FileAnalyzer {
  public static void main(final String[] args) throws IOException, PdbParsingException {
    final FileAnalyzer scanner = new FileAnalyzer();
    scanner.analyze();
  }

  private FileAnalyzer() {
    super();
  }

  private void analyze() throws IOException, PdbParsingException {
    final DatabaseService service = new DatabaseService();

    try {
      for (final File file : service.getFilesToProcess()) {
        if (file.getStructure() == null) {
          service.beginTransaction();

          final Structure structure = Structure.fromFile(file);
          file.setProcessed(true);

          service.persist(structure);
          service.persist(file);

          service.commit();

          if (FileAnalyzer.log.isInfoEnabled()) {
            final List<Model> models = structure.getModels();
            final int modelCount = models.size();
            final int chainCount =
                models.stream().flatMapToInt(model -> IntStream.of(model.getChains().size())).sum();
            final int residueCount =
                models
                    .stream()
                    .flatMap(model -> model.getChains().stream())
                    .flatMapToInt(chain -> IntStream.of(chain.getResidues().size()))
                    .sum();
            FileAnalyzer.log.info(
                "Finised processing {} with {} models, {} chains, {} nucleotides",
                file.getPath(),
                modelCount,
                chainCount,
                residueCount);
          }
        }
      }
    } finally {
      service.close();
    }
  }
}

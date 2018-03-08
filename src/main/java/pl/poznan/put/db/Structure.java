package pl.poznan.put.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.structure.tertiary.StructureManager;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Structure {
  public static Structure fromFile(final File file) throws IOException, PdbParsingException {
    final List<? extends PdbModel> pdbModels =
        StructureManager.loadStructure(new java.io.File(file.getPath()));
    final List<Model> models =
        pdbModels.stream().map(Model::fromPdbModel).collect(Collectors.toList());

    final Structure structure = new Structure();
    structure.setFile(file);
    structure.setModels(models);
    return structure;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @OneToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "file_id")
  private File file;

  @OneToMany(mappedBy = "structure", cascade = CascadeType.PERSIST)
  private List<Model> models = Collections.emptyList();

  public void setFile(final File file) {
    if (Objects.equals(this.file, file)) {
      return;
    }

    this.file = file;
    file.setStructure(this);
  }

  public void setModels(final List<Model> models) {
    if (Objects.equals(this.models, models)) {
      return;
    }
    if (CollectionUtils.isEqualCollection(this.models, models)) {
      return;
    }

    this.models.forEach(model -> model.setStructure(null));
    this.models = new ArrayList<>(models);
    this.models.forEach(model -> model.setStructure(this));
  }

  public final void addModel(final Model model) {
    if (models.contains(model)) {
      return;
    }
    models.add(model);
  }
}

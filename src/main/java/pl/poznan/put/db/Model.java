package pl.poznan.put.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbModel;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Model {
  public static Model fromPdbModel(final PdbModel pdbModel) {
    final List<PdbChain> pdbChains = pdbModel.getChains();
    final List<Chain> chains =
        pdbChains.stream().map(Chain::fromPdbChain).collect(Collectors.toList());

    final Model model = new Model();
    model.setNumber(pdbModel.getModelNumber());
    model.setChains(chains);
    return model;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "structure_id")
  private Structure structure;

  @Column private int number;

  @OneToMany(mappedBy = "model", cascade = CascadeType.PERSIST)
  private List<Chain> chains = Collections.emptyList();

  public void setStructure(final Structure structure) {
    if (Objects.equals(this.structure, structure)) {
      return;
    }

    this.structure = structure;
    this.structure.addModel(this);
  }

  public void setChains(final List<Chain> chains) {
    if (Objects.equals(this.chains, chains)) {
      return;
    }
    if (CollectionUtils.isEqualCollection(this.chains, chains)) {
      return;
    }

    this.chains.forEach(chain -> chain.setModel(null));
    this.chains = new ArrayList<>(chains);
    this.chains.forEach(chain -> chain.setModel(this));
  }

  public final void addChain(final Chain chain) {
    if (chains.contains(chain)) {
      return;
    }
    chains.add(chain);
  }
}

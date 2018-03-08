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
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbChain;
import pl.poznan.put.pdb.analysis.PdbCompactFragment;
import pl.poznan.put.pdb.analysis.PdbResidue;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Chain {
  public static Chain fromPdbChain(final PdbChain pdbChain) {
    final boolean isRna = pdbChain.getMoleculeType() == MoleculeType.RNA;
    final List<Residue> residues;

    if (isRna) {
      final List<PdbResidue> pdbResidues = pdbChain.getResidues();
      final PdbCompactFragment fragment = new PdbCompactFragment("", pdbResidues);
      residues =
          pdbResidues
              .stream()
              .map(pdbResidue -> Residue.fromPdbResidue(fragment, pdbResidue))
              .collect(Collectors.toList());
    } else {
      residues = Collections.emptyList();
    }

    final Chain chain = new Chain();
    chain.setName(pdbChain.getIdentifier());
    chain.setRna(isRna);
    chain.setResidues(residues);
    return chain;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "model_id")
  private Model model;

  @Column private String name;

  @Column(name = "is_rna")
  private boolean isRna;

  @OneToMany(mappedBy = "chain", cascade = CascadeType.PERSIST)
  private List<Residue> residues = Collections.emptyList();

  public void setModel(final Model model) {
    if (Objects.equals(this.model, model)) {
      return;
    }

    this.model = model;
    this.model.addChain(this);
  }

  public void setResidues(final List<Residue> residues) {
    if (Objects.equals(this.residues, residues)) {
      return;
    }
    if (CollectionUtils.isEqualCollection(this.residues, residues)) {
      return;
    }

    this.residues.forEach(residue -> residue.setChain(null));
    this.residues = new ArrayList<>(residues);
    this.residues.forEach(residue -> residue.setChain(this));
  }

  public final void addResidue(final Residue residue) {
    if (residues.contains(residue)) {
      return;
    }
    residues.add(residue);
  }
}

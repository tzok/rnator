package pl.poznan.put.db;

import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.analysis.PdbCompactFragment;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Residue {
  public static Residue fromPdbResidue(
      final PdbCompactFragment fragment, final ChainNumberICode pdbResidue) {
    final Dihedral dihedral = Dihedral.fromPdbResidue(fragment, pdbResidue);
    final Pucker pucker = Pucker.fromPdbResidue(fragment, pdbResidue);

    final Residue residue = new Residue();
    residue.setNumber(pdbResidue.getResidueNumber());
    residue.setInsertionCode(pdbResidue.getInsertionCode());
    residue.setDihedral(dihedral);
    residue.setPucker(pucker);
    return residue;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "chain_id")
  private Chain chain;

  @Column private int number;

  @Column(name = "insertion_code")
  private String insertionCode;

  @OneToOne(mappedBy = "residue", cascade = CascadeType.PERSIST)
  private Dihedral dihedral;

  @OneToOne(mappedBy = "residue", cascade = CascadeType.PERSIST)
  private Pucker pucker;

  public void setChain(final Chain chain) {
    if (Objects.equals(this.chain, chain)) {
      return;
    }

    this.chain = chain;
    this.chain.addResidue(this);
  }

  public void setDihedral(final Dihedral dihedral) {
    if (Objects.equals(this.dihedral, dihedral)) {
      return;
    }

    this.dihedral = dihedral;
    this.dihedral.setResidue(this);
  }

  public void setPucker(final Pucker pucker) {
    if (Objects.equals(this.pucker, pucker)) {
      return;
    }

    this.pucker = pucker;
    this.pucker.setResidue(this);
  }
}

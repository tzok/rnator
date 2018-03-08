package pl.poznan.put.db;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.analysis.PdbCompactFragment;
import pl.poznan.put.rna.torsion.RNATorsionAngleType;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Pucker {
  public static Pucker fromPdbResidue(
      final PdbCompactFragment fragment, final ChainNumberICode pdbResidue) {
    final double pseudorotation =
        fragment
            .getTorsionAngleValue(pdbResidue, RNATorsionAngleType.PSEUDOPHASE_PUCKER)
            .getValue()
            .getRadians();

    final Pucker pucker = new Pucker();
    pucker.setPseudorotation(pseudorotation);
    return pucker;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @OneToOne
  @JoinColumn(name = "residue_id")
  private Residue residue;

  @Column private double pseudorotation;

  public void setResidue(final Residue residue) {
    if (Objects.equals(this.residue, residue)) {
      return;
    }

    this.residue = residue;
    this.residue.setPucker(this);
  }
}

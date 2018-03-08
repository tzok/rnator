package pl.poznan.put.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
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
import pl.poznan.put.torsion.MasterTorsionAngleType;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Dihedral {
  private static final MasterTorsionAngleType[] ANGLE_TYPES = {
    RNATorsionAngleType.ALPHA,
    RNATorsionAngleType.BETA,
    RNATorsionAngleType.GAMMA,
    RNATorsionAngleType.DELTA,
    RNATorsionAngleType.EPSILON,
    RNATorsionAngleType.ZETA,
    RNATorsionAngleType.NU0,
    RNATorsionAngleType.NU1,
    RNATorsionAngleType.NU2,
    RNATorsionAngleType.NU3,
    RNATorsionAngleType.NU4,
    RNATorsionAngleType.CHI,
    RNATorsionAngleType.ETA,
    RNATorsionAngleType.THETA
  };

  public static Dihedral fromPdbResidue(
      final PdbCompactFragment fragment, final ChainNumberICode pdbResidue) {
    final List<BiConsumer<Dihedral, Double>> dihedralSetters =
        new ArrayList<>(Dihedral.ANGLE_TYPES.length);
    dihedralSetters.add(Dihedral::setAlpha);
    dihedralSetters.add(Dihedral::setBeta);
    dihedralSetters.add(Dihedral::setGamma);
    dihedralSetters.add(Dihedral::setDelta);
    dihedralSetters.add(Dihedral::setEpsilon);
    dihedralSetters.add(Dihedral::setZeta);
    dihedralSetters.add(Dihedral::setNu0);
    dihedralSetters.add(Dihedral::setNu1);
    dihedralSetters.add(Dihedral::setNu2);
    dihedralSetters.add(Dihedral::setNu3);
    dihedralSetters.add(Dihedral::setNu4);
    dihedralSetters.add(Dihedral::setChi);
    dihedralSetters.add(Dihedral::setEta);
    dihedralSetters.add(Dihedral::setTheta);

    final Dihedral dihedral = new Dihedral();
    for (int i = 0; i < Dihedral.ANGLE_TYPES.length; i++) {
      dihedralSetters
          .get(i)
          .accept(
              dihedral,
              fragment
                  .getTorsionAngleValue(pdbResidue, Dihedral.ANGLE_TYPES[i])
                  .getValue()
                  .getRadians());
    }
    return dihedral;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @OneToOne
  @JoinColumn(name = "residue_id")
  private Residue residue;

  @Column private double alpha;

  @Column private double beta;

  @Column private double gamma;

  @Column private double delta;

  @Column private double epsilon;

  @Column private double zeta;

  @Column private double nu0;

  @Column private double nu1;

  @Column private double nu2;

  @Column private double nu3;

  @Column private double nu4;

  @Column private double chi;

  @Column private double eta;

  @Column private double theta;

  public void setResidue(final Residue residue) {
    if (Objects.equals(this.residue, residue)) {
      return;
    }

    this.residue = residue;
    this.residue.setDihedral(this);
  }
}

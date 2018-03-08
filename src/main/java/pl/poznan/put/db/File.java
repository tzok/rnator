package pl.poznan.put.db;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class File {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column private String path;

  @Column private boolean processed;

  @OneToOne(mappedBy = "file")
  private Structure structure;

  public void setStructure(final Structure structure) {
    if (Objects.equals(this.structure, structure)) {
      return;
    }

    this.structure = structure;
    structure.setFile(this);
  }
}

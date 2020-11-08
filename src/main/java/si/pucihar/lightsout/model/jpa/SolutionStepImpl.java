package si.pucihar.lightsout.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter @Setter
public class SolutionStepImpl extends IdEntity {
  @ManyToOne(targetEntity = SolutionImpl.class, optional = false)
  private SolutionImpl solution;

  private int moveIndex;

  private int pressRow; //Y coordinate
  private int pressColumn; //X coordinate
}

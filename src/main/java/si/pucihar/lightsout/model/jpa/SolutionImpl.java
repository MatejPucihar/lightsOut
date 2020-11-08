package si.pucihar.lightsout.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class SolutionImpl extends IdEntity{
  @ManyToOne(targetEntity = PlayerImpl.class)
  private PlayerImpl player;
  @ManyToOne(targetEntity = ProblemImpl.class)
  private ProblemImpl problem;
  @OneToMany(targetEntity = SolutionStepImpl.class, mappedBy = "solution")
  private List<SolutionStepImpl> solutionSteps = new ArrayList<>();
}

package si.pucihar.lightsout.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class ProblemImpl extends IdEntity {
  private static final Jsonb JSONB = JsonbBuilder.create();

  @Column(nullable = false)
  private String initialProblemState;

  @Getter @Setter
  @ManyToOne(targetEntity = PlayerImpl.class, optional = false)
  private PlayerImpl creator;

  @Getter @Setter
  @OneToMany(targetEntity = SolutionImpl.class, mappedBy = "problem")
  private List<SolutionImpl> solutions = new ArrayList<>();

  public int[][] getInitialProblemState() {
    return JSONB.fromJson(initialProblemState, int[][].class);
  }

  public void setInitialProblemState(int[][] initialProblemState) {
    this.initialProblemState = JSONB.toJson(initialProblemState);
  }
}

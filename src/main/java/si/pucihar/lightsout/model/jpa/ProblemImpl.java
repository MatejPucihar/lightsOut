package si.pucihar.lightsout.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter @Setter
public class ProblemImpl extends IdEntity {
  @Column(nullable = false)
  private String initialProblemState;

  @ManyToOne(targetEntity = PlayerImpl.class, optional = false)
  private PlayerImpl creator;

  @OneToMany(targetEntity = SolutionImpl.class, mappedBy = "problem")
  private List<SolutionImpl> solutions = new ArrayList<>();

  public void setInitialProblemStateArray(int[][] initialProblemState) {
    this.initialProblemState =
      Arrays.stream(initialProblemState)
        .map(row -> Arrays.stream(row).boxed()
          .map(String::valueOf)
          .collect(Collectors.joining(",")))
        .collect(Collectors.joining(";"));
  }

  public Integer[][] getInitialProblemStateArray(){
    return Arrays.stream(initialProblemState.split(";"))
      .map(row ->
        Arrays.stream(row.split(","))
          .map(Integer::valueOf)
          .toArray(Integer[]::new))
      .toArray(Integer[][]::new);
  }
}

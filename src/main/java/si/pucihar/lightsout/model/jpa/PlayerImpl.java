package si.pucihar.lightsout.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class PlayerImpl extends IdEntity{
  @Column(nullable = false)
  private String username;
  @Column(nullable = false)
  private int age;
  @OneToMany(targetEntity = ProblemImpl.class, mappedBy = "creator")
  private List<ProblemImpl> problems = new ArrayList<>();
  @OneToMany(targetEntity = SolutionImpl.class, mappedBy = "player")
  private List<SolutionImpl> solutions = new ArrayList<>();
}

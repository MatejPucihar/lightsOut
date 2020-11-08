package si.pucihar.lightsout.model;

import lombok.*;
import si.pucihar.lightsout.model.jpa.IdEntity;
import si.pucihar.lightsout.model.jpa.ProblemImpl;

import javax.json.bind.annotation.JsonbTransient;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor //json deserialization... default constructor
@RequiredArgsConstructor
public class Problem {
  private Long id;
  @NonNull private int[][] initialProblemState;
  @NonNull private long creatorId;
  private List<Long> solutionIds;

  @JsonbTransient
  public static Problem from(ProblemImpl problemImpl) {
    return new Problem(
      problemImpl.getId(),
      problemImpl.getInitialProblemState(),
      problemImpl.getCreator().getId(),
      problemImpl.getSolutions().stream().map(IdEntity::getId).collect(Collectors.toList())
    );
  }
}

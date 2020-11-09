package si.pucihar.lightsout.model;

import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import si.pucihar.lightsout.model.jpa.SolutionImpl;

import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@NoArgsConstructor //json deserialization... default constructor
@AllArgsConstructor
public class Solution {
  private Long id;
  @NonNull private long playerId;
  @NonNull private long problemId;
  @NonNull private List<SolutionStep> solutionSteps;

  public static Solution from(SolutionImpl solutionImpl){
    return new Solution(
      solutionImpl.getId(),
      solutionImpl.getPlayer().getId(),
      solutionImpl.getProblem().getId(),
      solutionImpl.getSolutionSteps().stream().map(SolutionStep::from).collect(Collectors.toList())
    );
  }
}

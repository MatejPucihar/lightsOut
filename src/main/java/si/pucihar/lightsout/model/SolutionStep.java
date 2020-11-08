package si.pucihar.lightsout.model;

import lombok.*;
import si.pucihar.lightsout.model.jpa.SolutionStepImpl;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@NoArgsConstructor //json deserialization... default constructor
public class SolutionStep {
  private Long id;
  private Long solutionId;

  @NonNull private int moveIndex;
  @NonNull private int pressRow; //Y coordinate
  @NonNull private int pressColumn; //X coordinate

  public static SolutionStep from(SolutionStepImpl solutionStepImpl){
    return new SolutionStep(
      solutionStepImpl.getId(),
      solutionStepImpl.getSolution().getId(),
      solutionStepImpl.getMoveIndex(),
      solutionStepImpl.getPressRow(),
      solutionStepImpl.getPressColumn()
    );
  }
}

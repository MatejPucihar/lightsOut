package si.pucihar.lightsout.solver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LightsOutSolverTest {
  private static final LightsOutSolver SOLVER = new LightsOutSolver();

  @Test
  void isProblemInWrongFormatSolvable() {
    Assertions.assertFalse(SOLVER.isSolvable("bullshit").isValid());
    Assertions.assertFalse(SOLVER.isSolvable("bullshit").isValid());
    Assertions.assertFalse(SOLVER.isSolvable("bullshit").isValid());
    Assertions.assertFalse(SOLVER.isSolvable("bullshit").isValid());

  }
}

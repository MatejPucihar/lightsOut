package si.pucihar.lightsout.solver;

import javolution.text.Text;
import org.jscience.mathematics.number.ModuloInteger;
import org.jscience.mathematics.vector.DenseMatrix;
import org.junit.jupiter.api.Test;
import si.pucihar.lightsout.model.SolutionStep;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static si.pucihar.lightsout.solver.ExampleMatrices.*;

public class LightsOutSolverTest {
  private static final LightsOutSolver SOLVER = new LightsOutSolver();

  @Test
  void checkFormatSolvable() {
    assertFalse(SOLVER.isSolvable(m3x3WithSomeSpecialLights).isValid());
    assertFalse(SOLVER.isSolvable(m2x2Solved).isValid());
    assertFalse(SOLVER.isSolvable(m4x3).isValid());
    assertFalse(SOLVER.isSolvable(m9x9).isValid());

    assertTrue(SOLVER.isSolvable(m3x3Solved).isValid());
    assertTrue(SOLVER.isSolvable(m4x4Solved).isValid());
    assertTrue(SOLVER.isSolvable(m5x5Solved).isValid());
    assertTrue(SOLVER.isSolvable(m6x6Solved).isValid());
    assertTrue(SOLVER.isSolvable(m7x7Solved).isValid());
    assertTrue(SOLVER.isSolvable(m8x8Solved).isValid());
  }

  @Test
  void testSomeWellKnownUnsolvableProblems() {
    assertFalse(SOLVER.isSolvable(m5x5Unsolvable).isValid());
    assertFalse(SOLVER.isSolvable(m4x4Unsolvable).isValid());
  }

  @Test
  void testSomeSolvableScenarios() {
    assertTrue(SOLVER.isSolvable(m3x3).isValid());
    assertTrue(SOLVER.isSolvable(m4x4).isValid());
    assertTrue(SOLVER.isSolvable(m5x5).isValid());
    assertTrue(SOLVER.isSolvable(m6x6).isValid());
    assertTrue(SOLVER.isSolvable(m7x7).isValid());
    assertTrue(SOLVER.isSolvable(m8x8).isValid());
  }

  @Test
  void stepsSolveProblem3x3() {
    final List<SolutionStep> solutionSteps = getSolutionSteps(m3x3);
    assertTrue(SOLVER.stepsSolveProblem(m3x3, solutionSteps).isValid());
  }

  @Test
  void stepsSolveProblem5x5() {
    final List<SolutionStep> solutionSteps = getSolutionSteps(m5x5);
    assertTrue(SOLVER.stepsSolveProblem(m5x5, solutionSteps).isValid());
  }

  @Test
  void stepsDontSolveProblem5x5WithoutOneStep() {
    final List<SolutionStep> solutionSteps = getSolutionSteps(m5x5);
    solutionSteps.remove(solutionSteps.get(solutionSteps.size() - 1));
    assertFalse(SOLVER.stepsSolveProblem(m5x5, solutionSteps).isValid());
  }

  private List<SolutionStep> getSolutionSteps(int[][] matrix) {
    final DenseMatrix<ModuloInteger> rowEchelon =
      SOLVER.buildRowEchelonGaussianMatrixOfButtonPressCorrelationsWithProblemMatrix(matrix);
    final String vector = rowEchelon.getColumn(rowEchelon.getNumberOfColumns() - 1).toText().toString()
      .replaceAll("[^0-9.]", "");

    final List<SolutionStep> solutionSteps = new ArrayList<>();
    for (int row = 0; row < matrix.length; row++) {
      for (int column = 0; column < matrix.length; column++) {
        if (vector.charAt(row * matrix.length + column) == '1') {
          solutionSteps.add(new SolutionStep(solutionSteps.size(), row, column));
        }
      }
    }
    return solutionSteps;
  }
}

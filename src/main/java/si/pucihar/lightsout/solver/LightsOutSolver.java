package si.pucihar.lightsout.solver;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.jscience.mathematics.number.LargeInteger;
import org.jscience.mathematics.number.ModuloInteger;
import org.jscience.mathematics.vector.DenseMatrix;
import org.jscience.mathematics.vector.DenseVector;
import si.pucihar.lightsout.model.SolutionStep;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.jscience.mathematics.number.ModuloInteger.*;
import static org.jscience.mathematics.number.ModuloInteger.setModulus;

@Slf4j
@ApplicationScoped
public class LightsOutSolver {
  private static final Jsonb JSONB = JsonbBuilder.create();

  static {
    setModulus(LargeInteger.valueOf(2));
  }

  public SolverResult isSolvable(int[][] initialProblemState) {
    final String problemValidation = validateProblem(initialProblemState);
    if (!StringUtils.isNullOrEmpty(problemValidation)) {
      return SolverResult.failure(problemValidation);
    }

    final DenseMatrix<ModuloInteger> rowEchelonForm =
      buildRowEchelonGaussianMatrixOfButtonPressCorrelationsWithProblemMatrix(initialProblemState);
    log.info("Problem: \n " + JSONB.toJson(initialProblemState));
    log.info("Solution: \n " + rowEchelonForm.toText());

    //check if any row has ZERO on diagonal and last element(the solution vector) is 1.
    for (int row = rowEchelonForm.getNumberOfRows() - 1; row >= 0; row--) {
      if (rowEchelonForm.get(row, row).equals(ZERO) && rowEchelonForm.get(row, rowEchelonForm.getNumberOfRows()).equals(ONE)) {
        return SolverResult.failure("no solution exist!"); //no solution exists!
      }
    }

    return SolverResult.success();
  }

  public SolverResult stepsSolveProblem(int[][] initialProblemState, List<SolutionStep> solutionSteps) {
    if (!StringUtils.isNullOrEmpty(validateProblem(initialProblemState))) {
      throw new RuntimeException("problems should be validated before checking if solution steps solve the problem!");
    }

    final boolean allStepsAreWithinBounds = solutionSteps.stream().allMatch(solutionStep ->
      solutionStep.getPressColumn() < initialProblemState.length &&
        solutionStep.getPressRow() < initialProblemState.length);
    if (!allStepsAreWithinBounds) {
      return SolverResult.failure("solution consists of impossible steps for given problem!");
    }

    ModuloInteger[][] problemMatrix = Arrays.stream(initialProblemState)
      .map(row -> Arrays.stream(row).mapToObj(value -> valueOf(String.valueOf(value))).toArray(ModuloInteger[]::new))
      .toArray(ModuloInteger[][]::new);

    for (SolutionStep solutionStep : solutionSteps) {
      final List<Integer> correlationIndexes =
        getCorrelationIndexes(solutionStep.getPressRow(), solutionStep.getPressColumn(), problemMatrix.length);
      for (int i = 0; i < correlationIndexes.size(); i++) {
        int row = correlationIndexes.get(i) / problemMatrix.length;
        int column = correlationIndexes.get(i) % problemMatrix.length;
        problemMatrix[row][column] = problemMatrix[row][column].plus(ONE);
      }
    }

    final boolean problemSolved = Arrays.stream(problemMatrix).flatMap(Arrays::stream).allMatch(light -> light.equals(ZERO));
    return problemSolved ? SolverResult.success() : SolverResult.failure("problem was not solved!");
  }

  private String validateProblem(int[][] initialProblemState) {
    final boolean noIllegalElements = Arrays.stream(initialProblemState)
      .flatMapToInt(Arrays::stream).allMatch(a -> a == 0 || a == 1);
    final boolean squareMatrix = Arrays.stream(initialProblemState)
      .allMatch(a -> a.length == initialProblemState.length);
    final boolean correctMatrixSize = initialProblemState.length < 9 && initialProblemState.length > 2;

    if (!noIllegalElements) {
      return "there are illegal characters in initial problem matrix! " + initialProblemState;
    }
    if (!squareMatrix) {
      return "initial problem matrix is not square! " + initialProblemState;
    }
    if (!correctMatrixSize) {
      return "matrix size must be 2<size<9 but is " + initialProblemState.length;
    }
    return null;
  }

  //package private
  DenseMatrix<ModuloInteger> buildRowEchelonGaussianMatrixOfButtonPressCorrelationsWithProblemMatrix(
    final int[][] initialProblemStateMatrix) {
    final List<ModuloInteger> lightsVector = Arrays.stream(initialProblemStateMatrix)
      .flatMapToInt(Arrays::stream)
      .mapToObj(i -> valueOf(String.valueOf(i)))
      .collect(Collectors.toList());

    final DenseMatrix<ModuloInteger> correlationMatrix =
      buildButtonPressCorrelationMatrix(initialProblemStateMatrix.length, lightsVector);

    return toGaussianRowEchelonForm(correlationMatrix);
  }

  private DenseMatrix<ModuloInteger> toGaussianRowEchelonForm(DenseMatrix<ModuloInteger> matrix) {
    DenseMatrix<ModuloInteger> matrixToUse = matrix.copy();

    //step1: upper triangular
    for (int rowToUse = 0; rowToUse < matrixToUse.getNumberOfRows(); rowToUse++) {
      for (int row = rowToUse + 1; row < matrixToUse.getNumberOfRows(); row++) {
        if (!matrixToUse.getRow(row).get(rowToUse).equals(ZERO)) {
          final DenseVector<ModuloInteger> newRow = matrixToUse.getRow(row).plus(matrixToUse.getRow(rowToUse));
          matrixToUse = replaceRow(matrixToUse, newRow, row);
        }
      }
      //swap rows if next to process has 0 on diagonal (row permutation)
      if (rowToUse + 1 < matrix.getNumberOfRows() && matrixToUse.get(rowToUse + 1, rowToUse + 1).equals(ZERO)) {
        for (int i = rowToUse + 2; i < matrixToUse.getNumberOfRows(); i++) {
          final DenseVector<ModuloInteger> row = matrixToUse.getRow(i);
          if (row.get(rowToUse + 1).equals(ONE)) {
            matrixToUse = swapRows(matrixToUse, rowToUse + 1, i);
            break;
          }
        }
      }
    }

    //step2: row echelon form
    for (int rowPosition = matrixToUse.getNumberOfRows() - 1; rowPosition >= 0; rowPosition--) {
      if (matrixToUse.get(rowPosition, rowPosition).equals(ONE)) {
        for (int rowReplacementCandidate = rowPosition - 1; rowReplacementCandidate >= 0; rowReplacementCandidate--) {
          if (matrixToUse.get(rowReplacementCandidate, rowPosition).equals(ONE)) {
            final DenseVector<ModuloInteger> newRow = matrixToUse.getRow(rowReplacementCandidate).plus(matrixToUse.getRow(rowPosition));
            matrixToUse = replaceRow(matrixToUse, newRow, rowReplacementCandidate);
          }
        }
      }
    }
    return matrixToUse;
  }

  private DenseMatrix<ModuloInteger> swapRows(DenseMatrix<ModuloInteger> matrixToUse, int i, int j) {
    final ArrayList<DenseVector<ModuloInteger>> matrix = new ArrayList<>();
    for (int i1 = 0; i1 < matrixToUse.getNumberOfRows(); i1++) {
      if (i1 == j) {
        matrix.add(matrixToUse.getRow(i));
      } else if (i1 == i) {
        matrix.add(matrixToUse.getRow(j));
      } else {
        matrix.add(matrixToUse.getRow(i1));
      }
    }
    return DenseMatrix.valueOf(matrix);
  }

  private DenseMatrix<ModuloInteger> replaceRow(
    DenseMatrix<ModuloInteger> matrixToUse, DenseVector<ModuloInteger> newRow,
    int rowIndex) {
    final List<DenseVector<ModuloInteger>> newMatrix = new ArrayList<>();
    for (int i = 0; i < matrixToUse.getNumberOfRows(); i++) {
      if (rowIndex != i) {
        newMatrix.add(matrixToUse.getRow(i));
      } else {
        newMatrix.add(newRow);
      }
    }
    return DenseMatrix.valueOf(newMatrix);
  }

  private DenseMatrix<ModuloInteger> buildButtonPressCorrelationMatrix(int length, List<ModuloInteger> b) {
    final ModuloInteger[][] correlationMatrix = new ModuloInteger[length * length][length * length + 1];
    for (int matrixRow = 0; matrixRow < length; matrixRow++) {
      for (int matrixColumn = 0; matrixColumn < length; matrixColumn++) {
        final List<Integer> correlationIndexes = getCorrelationIndexes(matrixRow, matrixColumn, length);
        for (int correlationMatrixRow = 0; correlationMatrixRow < (length * length); correlationMatrixRow++) {
          correlationMatrix[correlationMatrixRow][matrixRow * length + matrixColumn] =
            correlationIndexes.contains(correlationMatrixRow) ? ONE : ZERO;
        }
      }
    }
    for (int i = 0; i < length * length; i++) {
      correlationMatrix[i][length * length] = b.get(i);
    }
    return DenseMatrix.valueOf(correlationMatrix);
  }

  private List<Integer> getCorrelationIndexes(int matrixRow, int matrixColumn, int matrixLength) {
    final ArrayList<Integer> correlations = new ArrayList<>();
    correlations.add(matrixRow * matrixLength + matrixColumn);
    if (matrixRow > 0) {
      correlations.add((matrixRow - 1) * matrixLength + matrixColumn);
    }
    if (matrixRow < matrixLength - 1) {
      correlations.add((matrixRow + 1) * matrixLength + matrixColumn);
    }
    if (matrixColumn > 0) {
      correlations.add(matrixRow * matrixLength + matrixColumn - 1);
    }
    if (matrixColumn < matrixLength - 1) {
      correlations.add(matrixRow * matrixLength + matrixColumn + 1);
    }
    return correlations;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class SolverResult {
    private final boolean valid;
    private final String message;

    public static SolverResult success() {
      return new SolverResult(true, null);
    }

    public static SolverResult failure(String message) {
      return new SolverResult(false, message);
    }
  }
}

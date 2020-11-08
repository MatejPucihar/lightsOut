package si.pucihar.lightsout.service;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import si.pucihar.lightsout.model.Player;
import si.pucihar.lightsout.model.Problem;
import si.pucihar.lightsout.model.Solution;
import si.pucihar.lightsout.model.SolutionStep;
import si.pucihar.lightsout.model.jpa.PlayerImpl;
import si.pucihar.lightsout.model.jpa.ProblemImpl;
import si.pucihar.lightsout.model.jpa.SolutionImpl;
import si.pucihar.lightsout.model.jpa.SolutionStepImpl;
import si.pucihar.lightsout.solver.LightsOutSolver;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
public class SolutionsServiceTest {
  @InjectMock
  LightsOutSolver lightsOutSolver;
  @Inject
  EntityManager em;
  @Inject
  SolutionsService solutionsService;

  @BeforeEach
  public void setUp() {
    reset(lightsOutSolver);
  }

  @Test
  @TestTransaction
  void saveWrongSolution() {
    final Player persistedPlayer = getPersistedPlayer();
    final Problem problem = getPersistedProblem(persistedPlayer.getId(), "problem");

    final SolutionStep solutionStep1 = new SolutionStep(1, 2, 3);
    final SolutionStep solutionStep2 = new SolutionStep(2, 2, 4);
    final List<SolutionStep> solutionSteps = Arrays.asList(solutionStep1, solutionStep2);

    when(lightsOutSolver.stepsSolveProblem(eq(problem.getInitialProblemState()), eq(solutionSteps)))
      .thenReturn(LightsOutSolver.ValidationResult.failure("wrong solution."));

    assertThrows(BadRequestException.class, () -> solutionsService.saveSolution(
      new Solution(persistedPlayer.getId(), problem.getId(), solutionSteps)));
  }

  @Test
  @TestTransaction
  void saveSolution() {
    final Player persistedPlayer = getPersistedPlayer();
    final Problem persistedProblem = getPersistedProblem(persistedPlayer.getId(), "problem");

    final SolutionStep solutionStep1 = new SolutionStep(1, 2, 3);
    final SolutionStep solutionStep2 = new SolutionStep(2, 2, 4);
    final List<SolutionStep> solutionSteps = Arrays.asList(solutionStep1, solutionStep2);

    when(lightsOutSolver.stepsSolveProblem(eq(persistedProblem.getInitialProblemState()), eq(solutionSteps)))
      .thenReturn(LightsOutSolver.ValidationResult.success());

    final Solution solution = solutionsService.saveSolution(new Solution(persistedPlayer.getId(), persistedProblem.getId(), solutionSteps));

    verify(lightsOutSolver).stepsSolveProblem(eq(persistedProblem.getInitialProblemState()), eq(solutionSteps));
    assertEquals(solution,
      Solution.from(em.createQuery("SELECT s from SolutionImpl s", SolutionImpl.class).getSingleResult()));
    assertEquals(solution.getSolutionSteps().size(), 2);
  }

  @Test
  void getAllSolutionsForNonexistentProblemId() {
    Assertions.assertThrows(NotFoundException.class, () -> solutionsService.getAllSolutionsForProblemId(12L));
  }

  @Test
  @TestTransaction
  void getAllSolutionsForProblemIdWithoutSolutions() {
    final Problem persistedProblem = getPersistedProblem(getPersistedPlayer().getId(), "problem");
    assertEquals(0, solutionsService.getAllSolutionsForProblemId(persistedProblem.getId()).size());
  }

  @Test
  @TestTransaction
  void getAllSolutionsForProblemId() {
    final Player persistedPlayer = getPersistedPlayer();
    final Problem persistedProblem1 = getPersistedProblem(persistedPlayer.getId(), "problem");
    final Solution persistedSolution1 = getPersistedSolution(persistedPlayer.getId(), persistedProblem1.getId(), 3);
    final Solution persistedSolution2 = getPersistedSolution(persistedPlayer.getId(), persistedProblem1.getId(), 4);

    final List<Solution> allSolutionsForProblemId = solutionsService.getAllSolutionsForProblemId(persistedProblem1.getId());
    assertEquals(2, allSolutionsForProblemId.size());
    assertTrue(allSolutionsForProblemId.contains(persistedSolution1));
    assertTrue(allSolutionsForProblemId.contains(persistedSolution2));
  }

  @Test
  void getAllSolutionsForNonexistentPlayerWithUsername() {
    assertThrows(NotFoundException.class, () -> solutionsService.getAllSolutionsForPlayerWithUsername("username"));
  }

  @Test
  @TestTransaction
  void getAllSolutionsForPlayerWithoutSolutionsWithUsername() {
    final Player persistedPlayer = getPersistedPlayer();
    assertEquals(0, solutionsService.getAllSolutionsForPlayerWithUsername(persistedPlayer.getUsername()).size());
  }

  @Test
  @TestTransaction
  void getAllSolutionsForPlayerWithUsername() {
    final Player persistedPlayer = getPersistedPlayer();
    final Problem problem = getPersistedProblem(persistedPlayer.getId(), "problem");
    final Solution persistedSolution1 = getPersistedSolution(persistedPlayer.getId(), problem.getId(), 2);
    final Solution persistedSolution2 = getPersistedSolution(persistedPlayer.getId(), problem.getId(), 3);

    final List<Solution> allSolutionsForPlayerWithUsername =
      solutionsService.getAllSolutionsForPlayerWithUsername(persistedPlayer.getUsername());

    assertTrue(allSolutionsForPlayerWithUsername.containsAll(Arrays.asList(persistedSolution1, persistedSolution2)));
    assertEquals(2, allSolutionsForPlayerWithUsername.size());
  }

  @Test
  @TestTransaction
  void getAllSolutions() {
    final Player persistedPlayer1 = getPersistedPlayer();
    final Player persistedPlayer2 = getPersistedPlayer();

    final Problem problem1 = getPersistedProblem(persistedPlayer1.getId(), "problem1");
    final Problem problem2 = getPersistedProblem(persistedPlayer1.getId(), "problem2");

    final Solution persistedSolution1 = getPersistedSolution(persistedPlayer1.getId(), problem1.getId(), 2);
    final Solution persistedSolution2 = getPersistedSolution(persistedPlayer2.getId(), problem2.getId(), 3);

    final List<Solution> allSolutions = solutionsService.getAllSolutions();

    assertTrue(allSolutions.containsAll(Arrays.asList(persistedSolution1, persistedSolution2)));
    assertEquals(2, allSolutions.size());
  }

  private Player getPersistedPlayer() {
    final PlayerImpl player = new PlayerImpl();
    player.setUsername("username");
    player.setAge(12);
    em.persist(player);
    return Player.from(player);
  }

  private Problem getPersistedProblem(long playerId, String initialProblem) {
    final ProblemImpl problemImpl = new ProblemImpl();
    problemImpl.setInitialProblemState(initialProblem);
    final PlayerImpl player = em.getReference(PlayerImpl.class, playerId);
    problemImpl.setCreator(player);
    em.persist(problemImpl);
    player.getProblems().add(problemImpl);
    return Problem.from(problemImpl);
  }

  private Solution getPersistedSolution(long playerId, long problemId, int numOfSteps){
    final SolutionImpl solutionImpl = new SolutionImpl();
    final PlayerImpl player = em.getReference(PlayerImpl.class, playerId);
    final ProblemImpl problem = em.getReference(ProblemImpl.class, problemId);

    solutionImpl.setPlayer(player);
    solutionImpl.setProblem(problem);
    em.persist(solutionImpl);
    problem.getSolutions().add(solutionImpl);
    player.getSolutions().add(solutionImpl);

    for (int i = 0; i < numOfSteps; i++) {
      final SolutionStepImpl solutionStepImpl = new SolutionStepImpl();
      solutionStepImpl.setSolution(solutionImpl);
      solutionStepImpl.setMoveIndex(i);
      solutionStepImpl.setPressColumn(i);
      solutionStepImpl.setPressRow(i);
      em.persist(solutionStepImpl);
      solutionImpl.getSolutionSteps().add(solutionStepImpl);
    }

    return Solution.from(solutionImpl);
  }
}

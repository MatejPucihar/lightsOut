package si.pucihar.lightsout.service;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import si.pucihar.lightsout.model.Player;
import si.pucihar.lightsout.model.Problem;
import si.pucihar.lightsout.model.jpa.PlayerImpl;
import si.pucihar.lightsout.model.jpa.ProblemImpl;
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
public class ProblemsServiceTest {
  @InjectMock
  LightsOutSolver lightsOutSolver;
  @Inject
  ProblemsService problemsService;
  @Inject
  EntityManager em;

  @BeforeEach
  public void setUp() {
    reset(lightsOutSolver);
  }

  @Test
  @TestTransaction
  void saveProblem() {
    final Player persistedPlayer = getPersistedPlayer();

    when(lightsOutSolver.isSolvable(eq("initialProblemState"))).thenReturn(LightsOutSolver.ValidationResult.success());
    final Problem problem = problemsService.saveProblem(new Problem("initialProblemState", persistedPlayer.getId()));

    assertTrue(em.createQuery("SELECT p FROM ProblemImpl p", ProblemImpl.class).getResultStream()
      .map(Problem::from)
      .anyMatch(foundProblem -> foundProblem.equals(problem)));
    verify(lightsOutSolver).isSolvable(eq("initialProblemState"));
  }

  @Test
  @TestTransaction
  void saveUnsolvableProblem() {
    final Player persistedPlayer = getPersistedPlayer();

    when(lightsOutSolver.isSolvable(eq("initialProblemState"))).thenReturn(LightsOutSolver.ValidationResult.failure("unsolvable"));
    assertThrows(BadRequestException.class,
      () -> problemsService.saveProblem(new Problem("initialProblemState", persistedPlayer.getId())),
      "unsolvable");

    assertEquals(em.createQuery("SELECT p FROM ProblemImpl p", ProblemImpl.class).getResultList().size(), 0);
    verify(lightsOutSolver).isSolvable(eq("initialProblemState"));
  }

  @Test
  void getNonexistentProblemForId() {
    assertThrows(NotFoundException.class, () -> problemsService.getProblemForId(1L));
  }

  @Test
  @TestTransaction
  void findProblem() {
    final Player player = getPersistedPlayer();
    final Problem problem = getPersistedProblem(player.getId(), "initialProblem");

    Assertions.assertEquals(problemsService.getProblemForId(problem.getId()), problem);
  }

  @Test
  void getAllProblemsForNonexistentCreator() {
    assertThrows(NotFoundException.class, () -> problemsService.getAllProblemsForCreator("creator"));
  }

  @Test
  @TestTransaction
  void getAllProblemsForCreatorWithoutProblems() {
    assertEquals(0, problemsService.getAllProblemsForCreator(getPersistedPlayer().getUsername()).size());
  }

  @Test
  @TestTransaction
  void getAllProblemsForCreator() {
    final Player persistedPlayer = getPersistedPlayer();
    final Problem problem1 = getPersistedProblem(persistedPlayer.getId(), "problem1");
    final Problem problem2 = getPersistedProblem(persistedPlayer.getId(), "problem2");

    final List<Problem> allProblemsForCreator = problemsService.getAllProblemsForCreator(persistedPlayer.getUsername());

    assertEquals(2, allProblemsForCreator.size());
    assertTrue(allProblemsForCreator.contains(problem1));
    assertTrue(allProblemsForCreator.contains(problem2));
  }

  @Test
  @TestTransaction
  void findAllProblems() {
    final Player persistedPlayer1 = getPersistedPlayer();
    final Problem problem1 = getPersistedProblem(persistedPlayer1.getId(), "problem1");
    final Problem problem2 = getPersistedProblem(persistedPlayer1.getId(), "problem2");
    final Player persistedPlayer2 = getPersistedPlayer();
    final Problem problem3 = getPersistedProblem(persistedPlayer2.getId(), "problem3");
    final Problem problem4 = getPersistedProblem(persistedPlayer2.getId(), "problem4");
    final List<Problem> problems = Arrays.asList(problem1, problem2, problem3, problem4);

    final List<Problem> allProblems = problemsService.getAllProblems();
    assertTrue(allProblems.size() == 4 && allProblems.containsAll(problems));
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
}

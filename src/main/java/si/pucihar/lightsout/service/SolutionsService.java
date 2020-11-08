package si.pucihar.lightsout.service;

import si.pucihar.lightsout.model.Solution;
import si.pucihar.lightsout.model.jpa.PlayerImpl;
import si.pucihar.lightsout.solver.LightsOutSolver;
import si.pucihar.lightsout.model.jpa.ProblemImpl;
import si.pucihar.lightsout.model.jpa.SolutionImpl;
import si.pucihar.lightsout.model.jpa.SolutionStepImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SolutionsService {
  @Inject
  EntityManager em;
  @Inject
  LightsOutSolver lightsOutSolver;

  @Transactional
  public List<Solution> getAllSolutions() {
    return em.createQuery("SELECT s FROM SolutionImpl s", SolutionImpl.class).getResultStream()
      .map(Solution::from)
      .collect(Collectors.toList());
  }

  @Transactional
  public List<Solution> getAllSolutionsForPlayerWithUsername(String username) {
    final List<PlayerImpl> players = em.createQuery(
      "select player " +
        "from PlayerImpl player " +
        "where player.username = :username",
      PlayerImpl.class)
      .setParameter("username", username)
      .getResultList();
    if (players.isEmpty()){
      throw new NotFoundException();
    }
    return players.stream()
      .map(PlayerImpl::getSolutions)
      .flatMap(Collection::stream)
      .map(Solution::from)
      .collect(Collectors.toList());
  }

  @Transactional
  public List<Solution> getAllSolutionsForProblemId(long problemId) {
    return Optional.ofNullable(em.find(ProblemImpl.class, problemId))
      .map(problem -> problem.getSolutions().stream().map(Solution::from).collect(Collectors.toList()))
      .orElseThrow(NotFoundException::new);
  }

  @Transactional
  public Solution saveSolution(Solution solution) {
    final ProblemImpl problem = em.getReference(ProblemImpl.class, solution.getProblemId());
    final PlayerImpl player = em.getReference(PlayerImpl.class, solution.getPlayerId());

    final LightsOutSolver.SolverResult solverResult = lightsOutSolver.stepsSolveProblem(
      problem.getInitialProblemState(), solution.getSolutionSteps());

    if (!solverResult.isValid()){
      throw new BadRequestException(
        Response.status(Response.Status.BAD_REQUEST)
          .entity(solverResult.getMessage())
          .build());
    }

    final SolutionImpl solutionImpl = new SolutionImpl();
    solutionImpl.setPlayer(player);
    solutionImpl.setProblem(problem);
    em.persist(solutionImpl);
    problem.getSolutions().add(solutionImpl);
    player.getSolutions().add(solutionImpl);

    solution.getSolutionSteps().forEach(solutionStep -> {
      final SolutionStepImpl solutionStepImpl = new SolutionStepImpl();
      solutionStepImpl.setSolution(solutionImpl);
      solutionStepImpl.setMoveIndex(solutionStep.getMoveIndex());
      solutionStepImpl.setPressColumn(solutionStep.getPressColumn());
      solutionStepImpl.setPressRow(solutionStep.getPressRow());
      em.persist(solutionStepImpl);
      solutionImpl.getSolutionSteps().add(solutionStepImpl);
    });

    return Solution.from(solutionImpl);
  }
}

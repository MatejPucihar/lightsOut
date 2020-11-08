package si.pucihar.lightsout.service;

import lombok.extern.slf4j.Slf4j;
import si.pucihar.lightsout.model.jpa.PlayerImpl;
import si.pucihar.lightsout.solver.LightsOutSolver;
import si.pucihar.lightsout.model.Problem;
import si.pucihar.lightsout.model.jpa.ProblemImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class ProblemsService {
  @Inject
  EntityManager em;
  @Inject
  LightsOutSolver lightsOutSolver;

  @Transactional
  public List<Problem> getAllProblems() {
    return em.createQuery("SELECT p from ProblemImpl p", ProblemImpl.class).getResultStream()
      .map(Problem::from)
      .collect(Collectors.toList());
  }

  @Transactional
  public List<Problem> getAllProblemsForCreator(String creatorUsername) {
    final List<PlayerImpl> creators = em.createQuery(
      "SELECT player " +
        "FROM PlayerImpl player " +
        "WHERE player.username = :username", PlayerImpl.class)
      .setParameter("username", creatorUsername)
      .getResultList();
    if (creators.isEmpty()){
      throw new NotFoundException(String.format("No players with username %s exist!", creatorUsername));
    }
    return creators.stream()
      .map(PlayerImpl::getProblems)
      .flatMap(Collection::stream)
      .map(Problem::from)
      .collect(Collectors.toList());
  }

  @Transactional
  public Problem getProblemForId(long problemId) {
    return Optional.ofNullable(em.find(ProblemImpl.class, problemId))
      .map(Problem::from)
      .orElseThrow(NotFoundException::new);
  }

  @Transactional
  public Problem saveProblem(Problem problem) {
    log.info("Starting solver for problem: " + problem.getInitialProblemState() + " at " + LocalDateTime.now());
    final LightsOutSolver.ValidationResult validationResult = lightsOutSolver.isSolvable(problem.getInitialProblemState());
    log.info("Solver finished problem at " + LocalDateTime.now());

    if (!validationResult.isValid()){
      throw new BadRequestException(
        Response.status(Response.Status.BAD_REQUEST)
          .entity(validationResult.getMessage())
          .build());
    }

    final ProblemImpl problemImpl = new ProblemImpl();
    problemImpl.setInitialProblemState(problem.getInitialProblemState());
    final PlayerImpl player = em.getReference(PlayerImpl.class, problem.getCreatorId());
    problemImpl.setCreator(player);
    em.persist(problemImpl);
    player.getProblems().add(problemImpl);
    return Problem.from(problemImpl);
  }
}

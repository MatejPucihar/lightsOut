package si.pucihar.lightsout.resource;

import org.h2.util.StringUtils;
import si.pucihar.lightsout.model.Problem;
import si.pucihar.lightsout.service.ProblemsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/problems")
public class ProblemsResource {
  @Inject
  ProblemsService problemsService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Problem> getAllProblems() {
    return problemsService.getAllProblems();
  }

  @GET
  @Path("/creator/{creatorUsername}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Problem> getProblemsForCreator(@PathParam("creatorUsername") String creatorUsername) {
    if (StringUtils.isNullOrEmpty(creatorUsername)) {
      throw new BadRequestException(
        Response.status(Response.Status.BAD_REQUEST)
          .entity("creatorUsername is required!")
          .build());
    }
    return problemsService.getAllProblemsForCreator(creatorUsername);
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Problem getProblemWithId(@PathParam("id") long problemId) {
    return problemsService.getProblemForId(problemId);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Problem saveProblem(Problem problem) {
    if (problem == null) {
      throw new BadRequestException(
        Response.status(Response.Status.BAD_REQUEST)
          .entity("empty problem payload!")
          .build());
    }

    return problemsService.saveProblem(problem);
  }
}


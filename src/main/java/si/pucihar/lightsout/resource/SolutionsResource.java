package si.pucihar.lightsout.resource;

import si.pucihar.lightsout.model.Solution;
import si.pucihar.lightsout.service.SolutionsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/solutions")
public class SolutionsResource {
  @Inject
  SolutionsService solutionsService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Solution> getAllSolutions(){
    return solutionsService.getAllSolutions();
  }

  @GET
  @Path("/solver/{username}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Solution> getAllSolutionsForPlayerWithUsername(@PathParam("username") String username){
    return solutionsService.getAllSolutionsForPlayerWithUsername(username);
  }

  @GET
  @Path("/problem/{problemId}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Solution> getAllSolutionsForProblem(@PathParam("problemId") long problemId){
    return solutionsService.getAllSolutionsForProblemId(problemId);
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Solution saveSolution(Solution solution){
    if (solution == null) {
      throw new BadRequestException(
        Response.status(Response.Status.BAD_REQUEST)
          .entity("empty solution payload!")
          .build());
    }
    return solutionsService.saveSolution(solution);
  }
}

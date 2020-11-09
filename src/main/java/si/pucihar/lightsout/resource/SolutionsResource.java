package si.pucihar.lightsout.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.pucihar.lightsout.model.Player;
import si.pucihar.lightsout.model.Problem;
import si.pucihar.lightsout.model.Solution;
import si.pucihar.lightsout.service.SolutionsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static si.pucihar.lightsout.resource.ExamplePayloads.*;

@Path("/solutions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolutionsResource {
  @Inject
  SolutionsService solutionsService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "getAllSolutions", description = "retrieves all solutions")
  @APIResponse(
    name = "success",
    responseCode = "200",
    content = @Content(
      schema = @Schema(type = SchemaType.ARRAY, implementation = Solution.class, required = true,
        requiredProperties = {"id", "playerId", "problemId", "solutionSteps"}),
      examples = @ExampleObject(
        name = "saved solutions",
        description = "saved solutions",
        value = SAVED_SOLUTIONS
      )
    ))
  public List<Solution> getAllSolutions(){
    return solutionsService.getAllSolutions();
  }

  @GET
  @Path("/solver/{username}")
  @Operation(summary = "getProblemWithId", description = "retrieves problem with specified id.")
  @Parameter(
    name = "username",
    description = "player username",
    schema = @Schema(type = SchemaType.STRING),
    required = true
  )
  @APIResponses(
    value = {
      @APIResponse(
        name = "success",
        responseCode = "200",
        content = @Content(
          schema = @Schema(minItems = 0, type = SchemaType.ARRAY, implementation = Solution.class, required = true,
            requiredProperties = {"id", "playerId", "problemId", "solutionSteps"}),
          examples = @ExampleObject(
            name = "saved solutions",
            description = "saved solutions",
            value = SAVED_SOLUTIONS
          )
        )),
      @APIResponse(
        name = "player username not found",
        description = "player username not found",
        responseCode = "404"
      )
    })
  public List<Solution> getAllSolutionsForPlayerWithUsername(@PathParam("username") String username){
    return solutionsService.getAllSolutionsForPlayerWithUsername(username);
  }

  @GET
  @Path("/problem/{problemId}")
  @Operation(summary = "getProblemWithId", description = "retrieves problem with specified id.")
  @Parameter(
    name = "problemId",
    description = "problem id",
    schema = @Schema(type = SchemaType.STRING),
    required = true
  )
  @APIResponses(
    value = {
      @APIResponse(
        name = "success",
        responseCode = "200",
        content = @Content(
          schema = @Schema(implementation = Solution.class, required = true,
            requiredProperties = {"id", "playerId", "problemId", "solutionSteps"}),
          examples = @ExampleObject(
            name = "saved solution",
            description = "saved solution",
            value = SAVED_SOLUTION
          )
        )),
      @APIResponse(
        name = "problem with id does not exist",
        description = "problem with id does not exist",
        responseCode = "404"
      )
    })
  public List<Solution> getAllSolutionsForProblem(@PathParam("problemId") long problemId){
    return solutionsService.getAllSolutionsForProblemId(problemId);
  }

  @POST
  @Operation(summary = "saveSolution", description = "saves nev solution.")
  @RequestBody(
    required = true,
    content = @Content(
      schema = @Schema(implementation = Solution.class, required = true,
        requiredProperties = {"playerId", "problemId", "solutionSteps"}),
      examples = @ExampleObject(
        name = "new solution",
        description = "saves new solution",
        value = NEW_SOLUTION
      )
    ))
  @APIResponses(
    value = {
      @APIResponse(
        name = "success",
        responseCode = "200",
        content = @Content(
          schema = @Schema(implementation = Solution.class, required = true,
            requiredProperties = {"id", "playerId", "problemId", "solutionSteps"}),
          examples = @ExampleObject(
            name = "saved solution",
            description = "saved solution",
            value = SAVED_SOLUTION
          )
        )),
      @APIResponse(
        name = "bad or empty solution",
        description = "solution does not solve problem or solution is empty",
        responseCode = "400"
      )
    })
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

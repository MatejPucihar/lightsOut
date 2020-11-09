package si.pucihar.lightsout.resource;

import lombok.NonNull;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.h2.util.StringUtils;
import si.pucihar.lightsout.model.Player;
import si.pucihar.lightsout.model.Problem;
import si.pucihar.lightsout.service.ProblemsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static si.pucihar.lightsout.resource.ExamplePayloads.*;

@Path("/problems")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProblemsResource {
  @Inject
  ProblemsService problemsService;

  @GET
  @Operation(summary = "getAllProblems", description = "retrieves all persisted problems.")
  @APIResponse(
    name = "success",
    responseCode = "200",
    content = @Content(
      schema = @Schema(minItems = 0, type = SchemaType.ARRAY, implementation = Problem.class, required = true,
        requiredProperties = {"id", "initialProblemState", "creatorId", "solutionIds"}),
      examples = @ExampleObject(
        name = "saved problems",
        description = "saved problems",
        value = SAVED_PROBLEMS
      )
    ))
  public List<Problem> getAllProblems() {
    return problemsService.getAllProblems();
  }

  @GET
  @Path("/creator/{creatorUsername}")
  @Operation(summary = "getProblemsForCreator", description = "retrieves all persisted problems for creator username.")
  @Parameter(
    name = "username",
    schema = @Schema(type = SchemaType.STRING),
    required = true
  )
  @APIResponses(
    value = {
      @APIResponse(
        name = "success",
        responseCode = "200",
        content = @Content(
          schema = @Schema(implementation = Problem.class, required = true, requiredProperties = {"id", "initialProblemState", "creatorId", "creatorId"}),
          examples = @ExampleObject(
            name = "saved player",
            description = "new player with id assigned.",
            value = SAVED_PROBLEMS
          )
        ))
      ,
      @APIResponse(
        name = "empty username",
        description = "empty username",
        responseCode = "400"
      ),
      @APIResponse(
        name = "nonexistent username",
        description = "nonexistent username",
        responseCode = "404"
      ),
    })
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
  @Operation(summary = "getProblemWithId", description = "retrieves problem with specified id.")
  @Parameter(
    name = "id",
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
          schema = @Schema(implementation = Problem.class, required = true,
            requiredProperties = {"id", "initialProblemState", "creatorId", "solutionIds"}),
          examples = @ExampleObject(
            name = "saved problem",
            description = "saved problem",
            value = SAVED_PROBLEM
          )
        )),
      @APIResponse(
        name = "problem not found",
        description = "problem not found",
        responseCode = "404"
      )
    })
  public Problem getProblemWithId(@PathParam("id") long problemId) {
    return problemsService.getProblemForId(problemId);
  }

  @POST
  @Operation(summary = "saveProblem", description = "saves new problem.")
  @RequestBody(
    required = true,
    content = @Content(
      schema = @Schema(implementation = Problem.class, required = true, requiredProperties = {"initialProblemState", "creatorId"}),
      examples = @ExampleObject(
        name = "new problem",
        description = "saves new problem",
        value = NEW_PROBLEM
      )
    ))
  @APIResponses(
    value = {
      @APIResponse(
        name = "success",
        responseCode = "200",
        content = @Content(
          schema = @Schema(implementation = Problem.class, required = true,
            requiredProperties = {"id", "initialProblemState", "creatorId", "solutionIds"}),
          examples = @ExampleObject(
            name = "saved problem",
            description = "saved problem",
            value = SAVED_PROBLEM
          )
        )),
      @APIResponse(
        name = "broken or unsolvable problem",
        description = "broken or unsolvable problem",
        responseCode = "400"
      )
    })
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


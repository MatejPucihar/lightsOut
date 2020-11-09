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
import org.h2.util.StringUtils;
import si.pucihar.lightsout.model.Player;
import si.pucihar.lightsout.service.PlayersService;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static si.pucihar.lightsout.resource.ExamplePayloads.*;

@Path("/players")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class PlayersResource {
  @Inject
  PlayersService playersService;

  @GET
  @Operation(summary = "Retrieve all players", description = "Retrieves all players")
  @APIResponse(
    responseCode = "200",
    description = "saved players",
    content = @Content(
      schema = @Schema(type = SchemaType.ARRAY, required = true, implementation = Player.class, requiredProperties = {"id", "username", "age"}),
      examples = @ExampleObject(
        name = "saved players",
        description = "saved players response",
        value = SAVED_PLAYERS
      )
    ))
  public List<Player> getAllPlayers() {
    return playersService.getAllPlayers();
  }

  @POST
  @Operation(summary = "save new player", description = "Creates new player with given age and username.")
  @RequestBody(
    required = true,
    content = @Content(
      schema = @Schema(implementation = Player.class, required = true, requiredProperties = {"username", "age"}),
      examples = @ExampleObject(
        name = "new player",
        description = "saves new player with parameters username and age",
        value = NEW_PLAYER
      )
    ))
  @APIResponses(
	value = {
    @APIResponse(
      name = "success",
      responseCode = "200",
      content = @Content(
        schema = @Schema(implementation = Player.class, required = true, requiredProperties = {"id", "username", "age"}),
        examples = @ExampleObject(
          name = "saved player",
          description = "new player with id assigned.",
          value = SAVED_PLAYER
        )
      ))
    ,
    @APIResponse(
      name = "bad request, no username",
      responseCode = "400",
      description = "missing username or age"
    ),
	}
)
  public Player savePlayer(Player player) {
    if (StringUtils.isNullOrEmpty(player.getUsername())) {
      throw new BadRequestException(
        Response.status(Response.Status.BAD_REQUEST)
          .entity("username is required!")
          .build());
    }
    if (player.getAge() == null) {
      throw new BadRequestException(
        Response.status(Response.Status.BAD_REQUEST)
          .entity("age is required!")
          .build());
    }
    return playersService.savePlayer(player);
  }

  @GET
  @Path("/{username}")
  @Operation(summary = "Get all players with username", description = "Retrieves all players with given username")
  @Parameter(
    name = "username",
    schema = @Schema(type = SchemaType.STRING),
    required = true
  )
  @APIResponse(
    responseCode = "200",
    content = @Content(
      schema = @Schema(implementation = Player.class, required = true, requiredProperties = {"id", "username", "age"}),
      examples = @ExampleObject(
        name = "saved player",
        description = "saved player",
        value = SAVED_PLAYERS
      )
    ))
  public List<Player> getPlayersWithUsername(@PathParam("username") String username) {
    if (StringUtils.isNullOrEmpty(username)) {
      throw new BadRequestException(
        Response.status(Response.Status.BAD_REQUEST)
          .entity("username is required!")
          .build());
    }
    return playersService.getPlayersByUsername(username);
  }
}

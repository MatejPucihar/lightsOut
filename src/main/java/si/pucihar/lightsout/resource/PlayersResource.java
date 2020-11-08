package si.pucihar.lightsout.resource;

import org.h2.util.StringUtils;
import si.pucihar.lightsout.model.Player;
import si.pucihar.lightsout.service.PlayersService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/players")
public class PlayersResource {
  @Inject
  PlayersService playersService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Player> getAllPlayers() {
    return playersService.getAllPlayers();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Player savePlayer(Player player) {
    if (StringUtils.isNullOrEmpty(player.getUsername())){
      throw new BadRequestException(
        Response.status(Response.Status.BAD_REQUEST)
          .entity("username is required!")
          .build());
    }
    if (player.getAge() == null){
      throw new BadRequestException(
        Response.status(Response.Status.BAD_REQUEST)
          .entity("age is required!")
          .build());
    }
    return playersService.savePlayer(player);
  }

  @GET
  @Path("/{username}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Player> getPlayersWithUsername(@PathParam("username") String username) {
    if (StringUtils.isNullOrEmpty(username)){
      throw new BadRequestException(
        Response.status(Response.Status.BAD_REQUEST)
          .entity("username is required!")
          .build());
    }
    return playersService.getPlayersByUsername(username);
  }
}

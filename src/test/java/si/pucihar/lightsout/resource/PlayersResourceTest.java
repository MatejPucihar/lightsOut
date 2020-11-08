package si.pucihar.lightsout.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import si.pucihar.lightsout.model.Player;
import si.pucihar.lightsout.service.PlayersService;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;

@QuarkusTest
public class PlayersResourceTest {
  private static final Jsonb jsonb = JsonbBuilder.create();
  private static final List<Player> players = Collections.singletonList(new Player("11", 12));

  @InjectMock
  PlayersService playersService;

  @BeforeEach
  public void setUp() {
    reset(playersService);
  }

  @Test
  void getAllPlayersTest() {
    when(playersService.getAllPlayers()).thenReturn(players);
    given()
      .when().get("/players")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body(equalTo(jsonb.toJson(players)));
    verify(playersService).getAllPlayers();
  }

  @Test
  void savePlayerWithoutUsername() {
    given().body("{\"age\":12}").contentType(ContentType.JSON).post("/players")
      .then()
      .statusCode(400);
    verifyNoInteractions(playersService);
  }

  @Test
  void savePlayerWithoutAge() {
    given().body("{\"username\":\"username\"}").contentType(ContentType.JSON).post("/players")
      .then()
      .statusCode(400);
    verifyNoInteractions(playersService);
  }

  @Test
  void savePlayer() {
    when(playersService.savePlayer(any(Player.class)))
      .thenAnswer((Answer<Player>) invocationOnMock -> invocationOnMock.getArgument(0));
    given().body(jsonb.toJson(players.get(0))).contentType(ContentType.JSON).post("/players")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body(equalTo(jsonb.toJson(players.get(0))));

    verify(playersService).savePlayer(eq(players.get(0)));
  }

  @Test
  void getPlayersWithUsername() {
    when(playersService.getPlayersByUsername(eq("username"))).thenReturn(players);
    given()
      .when().get("/players/username")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body(equalTo(jsonb.toJson(players)));
    verify(playersService).getPlayersByUsername(eq("username"));
  }
}

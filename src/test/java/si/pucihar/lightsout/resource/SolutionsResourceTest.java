package si.pucihar.lightsout.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import si.pucihar.lightsout.model.Solution;
import si.pucihar.lightsout.model.SolutionStep;
import si.pucihar.lightsout.service.SolutionsService;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.trustStore;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;

@QuarkusTest
public class SolutionsResourceTest {
  private final static List<Solution> SOLUTIONS = Collections.singletonList(
    new Solution(1L, 2L,
      Collections.singletonList(new SolutionStep(3, 4, 5))));
  private static final Jsonb jsonb = JsonbBuilder.create();

  @InjectMock
  SolutionsService solutionsService;

  public void setUp() {
    reset(solutionsService);
  }

  @Test
  void getAllSolutions() {
    when(solutionsService.getAllSolutions()).thenReturn(SOLUTIONS);
    given()
      .when().get("/solutions")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body(equalTo(jsonb.toJson(SOLUTIONS)));
    verify(solutionsService).getAllSolutions();
  }

  @Test
  void getAllSolutionsForPlayerWithUsername() {
    when(solutionsService.getAllSolutionsForPlayerWithUsername(eq("username"))).thenReturn(SOLUTIONS);
    given()
      .when().get("/solutions/solver/username")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body(equalTo(jsonb.toJson(SOLUTIONS)));
    verify(solutionsService).getAllSolutionsForPlayerWithUsername(eq("username"));
  }

  @Test
  void saveSolution() {
    when(solutionsService.saveSolution(eq(SOLUTIONS.get(0))))
      .thenAnswer((Answer<Solution>) invocationOnMock -> invocationOnMock.getArgument(0));

    given().body(jsonb.toJson(SOLUTIONS.get(0))).contentType(ContentType.JSON).post("/solutions")
      .then()
      .statusCode(200)
      .body(equalTo(jsonb.toJson(SOLUTIONS.get(0))));

    verify(solutionsService).saveSolution(SOLUTIONS.get(0));
  }

  @Test
  void saveEmptySolution() {
    given().body("").contentType(ContentType.JSON).post("/solutions")
      .then()
      .statusCode(400)
      .body(equalTo("empty solution payload!"));

    verifyNoInteractions(solutionsService);
  }
}

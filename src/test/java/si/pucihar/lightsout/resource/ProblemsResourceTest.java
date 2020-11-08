package si.pucihar.lightsout.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import si.pucihar.lightsout.model.Problem;
import si.pucihar.lightsout.service.ProblemsService;
import si.pucihar.lightsout.solver.ExampleMatrices;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ProblemsResourceTest {
  private static final Jsonb jsonb = JsonbBuilder.create();
  private static final List<Problem> problems = Collections.singletonList(new Problem(ExampleMatrices.m3x3Solved, 11L));

  @InjectMock
  ProblemsService problemsService;

  @BeforeEach
  public void setUp() {
    reset(problemsService);
  }

  @Test
  public void getAllProblems() {
    when(problemsService.getAllProblems()).thenReturn(problems);

    given()
      .when().get("/problems")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body(equalTo(jsonb.toJson(problems)));

    verify(problemsService).getAllProblems();
  }

  @Test
  void getProblemsForCreator() {
    when(problemsService.getAllProblemsForCreator(eq("creatorUsername"))).thenReturn(problems);

    given()
      .when().get("/problems/creator/creatorUsername")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body(equalTo(jsonb.toJson(problems)));

    verify(problemsService).getAllProblemsForCreator(eq("creatorUsername"));
  }

  @Test
  void findProblemWithId() {
    when(problemsService.getProblemForId(eq(123L))).thenReturn(problems.get(0));

    given()
      .when().get("/problems/123")
      .then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body(equalTo(jsonb.toJson(problems.get(0))));

    verify(problemsService).getProblemForId(eq(123L));
  }


  @Test
  void saveEmptyProblem() {
    given().body("").contentType(ContentType.JSON).post("/problems")
      .then()
      .statusCode(400);
  }

  @Test
  void saveNonArrayProblem() {
    given().body("notAnIntArray").contentType(ContentType.JSON).post("/problems")
      .then()
      .statusCode(400);
  }

  @Test
  void saveProblem() {
    when(problemsService.saveProblem(any(Problem.class)))
      .thenAnswer((Answer<Problem>) invocationOnMock -> invocationOnMock.getArgument(0));
    given().body(jsonb.toJson(problems.get(0))).contentType(ContentType.JSON).post("/problems")
      .then()
      .statusCode(200)
      .body(equalTo(jsonb.toJson(problems.get(0))));
    verify(problemsService).saveProblem(eq(problems.get(0)));
  }
}

package si.pucihar.lightsout.resource;

import lombok.val;
import si.pucihar.lightsout.model.Player;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Arrays;

public interface ExamplePayloads {
  Jsonb JSONB = JsonbBuilder.create();

  String NEW_PLAYER = "{\"age\":12, \"username\":\"username\"}";
  String SAVED_PLAYER = "{\"id\":1234, \"age\":12, \"username\":\"username\"}";
  String SAVED_PLAYERS = "[{\"age\":12,\"id\":1,\"username\":\"username1\"},{\"age\":13,\"id\":1,\"username\":\"username2\"}]";

  String NEW_PROBLEM = "{\"creatorId\":12,\"initialProblemState\":[[0,1,1],[0,0,0],[1,1,1]]}";
  String SAVED_PROBLEM = "{\"id\":1234, \"creatorId\":12,\"initialProblemState\":[[0,1,1],[0,0,0],[1,1,1]]}";
  String SAVED_PROBLEM_WITH_SOLUTION_IDS = "{\"creatorId\":554,\"id\":123,\"initialProblemState\":[[0,1,1],[1,0,0],[1,1,1]],\"solutionIds\":[12345,567]}";
  String SAVED_PROBLEMS = "[{\"creatorId\":3,\"id\":1,\"initialProblemState\":[[0,0,0],[1,1,1],[0,1,1]],\"solutionIds\":[56,78]},{\"creatorId\":4,\"id\":2,\"initialProblemState\":[[0,0,0],[1,1,1],[0,1,1]],\"solutionIds\":[]}]";

  String NEW_SOLUTION = "{\"playerId\":1,\"problemId\":2,\"solutionSteps\":[{\"moveIndex\":3,\"pressColumn\":5,\"pressRow\":4},{\"moveIndex\":7,\"pressColumn\":9,\"pressRow\":8}]}";
  String SAVED_SOLUTION = "{\"id\":123,\"playerId\":1,\"problemId\":2,\"solutionSteps\":[{\"id\":88,\"moveIndex\":3,\"pressColumn\":5,\"pressRow\":4,\"solutionId\":123},{\"id\":99,\"moveIndex\":7,\"pressColumn\":9,\"pressRow\":8,\"solutionId\":123}]}";
  String SAVED_SOLUTIONS = "[{\"id\":123,\"playerId\":1,\"problemId\":2,\"solutionSteps\":[{\"id\":88,\"moveIndex\":3,\"pressColumn\":5,\"pressRow\":4,\"solutionId\":123},{\"id\":99,\"moveIndex\":7,\"pressColumn\":9,\"pressRow\":8,\"solutionId\":123}]}, {\"id\":123,\"playerId\":1,\"problemId\":2,\"solutionSteps\":[{\"id\":88,\"moveIndex\":3,\"pressColumn\":5,\"pressRow\":4,\"solutionId\":123},{\"id\":99,\"moveIndex\":7,\"pressColumn\":9,\"pressRow\":8,\"solutionId\":123}]}]";

}



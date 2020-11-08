package si.pucihar.lightsout.model;

import lombok.*;
import si.pucihar.lightsout.model.jpa.IdEntity;
import si.pucihar.lightsout.model.jpa.PlayerImpl;

import javax.json.bind.annotation.JsonbTransient;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@NoArgsConstructor //json deserialization... default constructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Player {
  private Long id;
  @NonNull private String username;
  @NonNull private Integer age;
  private List<Long> problems;

  @JsonbTransient
  public static Player from(PlayerImpl playerImpl){
    return new Player(
      playerImpl.getId(),
      playerImpl.getUsername(),
      playerImpl.getAge(),
      playerImpl.getProblems().stream().map(IdEntity::getId).collect(Collectors.toList()));
  }
}

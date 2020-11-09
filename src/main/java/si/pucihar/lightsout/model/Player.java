package si.pucihar.lightsout.model;

import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import si.pucihar.lightsout.model.jpa.IdEntity;
import si.pucihar.lightsout.model.jpa.PlayerImpl;

import javax.annotation.Nullable;
import javax.json.bind.annotation.JsonbTransient;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@NoArgsConstructor //json deserialization... default constructor
@AllArgsConstructor
public class Player {
  private Long id;
  @NonNull private String username;
  @NonNull private Integer age;

  @JsonbTransient
  public static Player from(PlayerImpl playerImpl){
    return new Player(
      playerImpl.getId(),
      playerImpl.getUsername(),
      playerImpl.getAge());
  }
}

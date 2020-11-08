package si.pucihar.lightsout.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

@MappedSuperclass
@Getter @Setter
public abstract class IdEntity {
  @Id
  @SequenceGenerator(name = "hibernate_seq", sequenceName = "hibernate_seq", allocationSize = 1, initialValue = 1)
  @GeneratedValue(generator = "hibernate_seq")
  private Long id;
}

package si.pucihar.lightsout.service;

import si.pucihar.lightsout.model.jpa.PlayerImpl;
import si.pucihar.lightsout.model.Player;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PlayersService {
  @Inject
  EntityManager em;

  @Transactional
  public Player savePlayer(Player player){
    final PlayerImpl playerImpl = new PlayerImpl();
    playerImpl.setAge(player.getAge());
    playerImpl.setUsername(player.getUsername());
    em.persist(playerImpl);
    return Player.from(playerImpl);
  }

  @Transactional
  public List<Player> getAllPlayers(){
    return em.createQuery("SELECT p from PlayerImpl p", PlayerImpl.class).getResultStream()
      .map(Player::from)
      .collect(Collectors.toList());
  }

  @Transactional
  public List<Player> getPlayersByUsername(String username){
    return em.createQuery("SELECT p from PlayerImpl p where p.username = :username", PlayerImpl.class)
      .setParameter("username", username)
      .getResultStream()
      .map(Player::from)
      .collect(Collectors.toList());
  }
}

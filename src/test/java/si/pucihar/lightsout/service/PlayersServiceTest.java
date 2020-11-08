package si.pucihar.lightsout.service;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import si.pucihar.lightsout.model.Player;
import si.pucihar.lightsout.model.jpa.PlayerImpl;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class PlayersServiceTest {
  @Inject
  PlayersService playersService;
  @Inject
  EntityManager em;

  @Test
  void saveNullPlayer() {
    assertThrows(NullPointerException.class, () -> playersService.savePlayer(null));
  }

  @Test
  @TestTransaction
  void savePlayer() {
    final Player player = playersService.savePlayer(new Player("username", 12));

    final List<Player> players =
      em.createQuery("select p from PlayerImpl p", PlayerImpl.class)
        .getResultStream()
        .map(Player::from)
        .collect(Collectors.toList());
    assertEquals(players.size(), 1);
    assertEquals(player, players.get(0));
  }

  @Test
  @TestTransaction
  void getAllPlayersOnEmptyDb() {
    assertEquals(0, playersService.getAllPlayers().size());
  }

  @Test
  @TestTransaction
  void getAllPlayers() {
    final PlayerImpl playerImpl1 = new PlayerImpl();
    playerImpl1.setAge(13);
    playerImpl1.setUsername("abc");
    em.persist(playerImpl1);

    final PlayerImpl playerImpl2 = new PlayerImpl();
    playerImpl2.setAge(46);
    playerImpl2.setUsername("def");
    em.persist(playerImpl2);

    final List<Player> savedPlayers = playersService.getAllPlayers();

    assertEquals(2, savedPlayers.size());
    assertTrue(savedPlayers.contains(Player.from(playerImpl1)));
    assertTrue(savedPlayers.contains(Player.from(playerImpl2)));
  }

  @Test
  @TestTransaction
  void findPlayer() {
    final PlayerImpl playerImpl1 = new PlayerImpl();
    playerImpl1.setAge(13);
    playerImpl1.setUsername("abc");
    em.persist(playerImpl1);

    final List<Player> foundPlayers = playersService.getPlayersByUsername(playerImpl1.getUsername());

    assertEquals(1, foundPlayers.size());
    assertEquals(Player.from(playerImpl1), foundPlayers.get(0));
  }

  @Test
  @TestTransaction
  void findNonExistentPlayer() {
    final PlayerImpl playerImpl1 = new PlayerImpl();
    playerImpl1.setAge(13);
    playerImpl1.setUsername("abc");
    em.persist(playerImpl1);

    final List<Player> foundPlayers = playersService.getPlayersByUsername("nonExistentUsername");
    assertEquals(0, foundPlayers.size());
  }
}

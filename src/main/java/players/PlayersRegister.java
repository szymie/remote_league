package players;


import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class PlayersRegister {

    private Map<Integer, Player> players = new HashMap<>();

    public Player addPlayer(Player player) {

        Player newPlayer = new Player(player);

        newPlayer.setId(Player.nextId++);
        players.put(newPlayer.getId(), newPlayer);

        return newPlayer;
    }

    public Optional<Player> get(int playerId) {
        return Optional.ofNullable(players.get(playerId));
    }

    public List<Player> getByPosition(Player.Position position) {
        return players.values().stream()
                .filter(player -> player.getPosition() == position)
                .collect(Collectors.toList());
    }
}

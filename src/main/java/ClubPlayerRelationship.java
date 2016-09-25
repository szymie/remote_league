import clubs.Club;
import clubs.ClubNotFoundException;
import clubs.ClubsRegister;
import players.Player;
import players.PlayerAlreadyBoundException;
import players.PlayerNotFoundException;
import players.PlayersRegister;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClubPlayerRelationship {

    private Map<String, List<Player>> clubPlayers = new HashMap<>();
    private Set<Integer> boundPlayers = new HashSet<>();
    private PlayersRegister playersRegister;
    private ClubsRegister clubsRegister;

    public ClubPlayerRelationship(PlayersRegister playersRegister, ClubsRegister clubsRegister) {
        this.playersRegister = playersRegister;
        this.clubsRegister = clubsRegister;
    }

    public void addPlayerToClub(Player player, Club club) throws ClubNotFoundException, PlayerNotFoundException, PlayerAlreadyBoundException {

        clubsRegister.getByFullName(club.getFullName()).orElseThrow(ClubNotFoundException::new);
        playersRegister.get(player.getId()).orElseThrow(PlayerNotFoundException::new);

        if(boundPlayers.contains(player.getId())) {
            throw new PlayerAlreadyBoundException();
        }

        List<Player> players = Optional.ofNullable(clubPlayers.get(club.getFullName())).orElse(new ArrayList<>());

        players.add(player);
        boundPlayers.add(player.getId());

        clubPlayers.put(club.getFullName(), players);
    }

    public List<Player> getPlayersBoundWithClub(Club club) throws ClubNotFoundException {

        clubsRegister.getByFullName(club.getFullName()).orElseThrow(ClubNotFoundException::new);

        return Optional.ofNullable(clubPlayers.get(club.getFullName())).orElse(new ArrayList<>());
    }

    public void removePlayerFromClub(Player player, Club club) throws ClubNotFoundException, PlayerNotFoundException {

        clubsRegister.getByFullName(club.getFullName()).orElseThrow(ClubNotFoundException::new);
        playersRegister.get(player.getId()).orElseThrow(PlayerNotFoundException::new);

        List<Player> players = clubPlayers.get(club.getFullName());

        if(players != null) {
            players.removeIf(p -> p.getId().equals(player.getId()));
            clubPlayers.put(club.getFullName(), players);
            boundPlayers.remove(player.getId());
        }
    }
}

import clubs.Club;
import clubs.ClubAlreadyExistsException;
import clubs.ClubNotFoundException;
import fixtures.Fixture;
import fixtures.FixtureAlreadyPlayedException;
import fixtures.FixturesLeftException;
import players.Player;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import players.Player.Position;
import players.PlayerAlreadyBoundException;
import players.PlayerNotFoundException;
import seasons.Season;
import seasons.SeasonNotCompletedException;
import seasons.SeasonNotFoundException;
import table.Table;

public interface RemoteLeague extends Remote {

    public Player createPlayer(Player player) throws RemoteException;
    public Optional<Player> findPlayerById(int id) throws RemoteException;
    public List<Player> findPlayersPosition(Position position) throws RemoteException;

    public Club createClub(Club club) throws RemoteException, ClubAlreadyExistsException;
    public Optional<Club> findClubById(int id) throws RemoteException;
    public Optional<Club> findClubFullName(String fullName) throws RemoteException;

    public void bindPlayerWithClub(Player player, Club club) throws RemoteException,
            PlayerNotFoundException, ClubNotFoundException, PlayerAlreadyBoundException;

    public List<Player> findPlayersBoundWithClub(Club club) throws RemoteException, ClubNotFoundException;

    public void unbindPlayerWithClub(Player player, Club club) throws RemoteException,
            PlayerNotFoundException, ClubNotFoundException;
    
    public Season createSeason(Season season) throws RemoteException, SeasonNotCompletedException;

    public void addClubToCurrentSeason(Club club) throws RemoteException,
            ClubNotFoundException, SeasonNotFoundException;

    public void addFixtureToCurrentSeason(Fixture fixture) throws RemoteException, ClubNotFoundException, SeasonNotFoundException, FixtureAlreadyPlayedException;

    public List<Fixture> findFixturesBySeasonId(int seasonId) throws RemoteException, SeasonNotFoundException;

    public void closeCurrentSeason() throws RemoteException, SeasonNotFoundException, FixturesLeftException;

    public Table calculateTableForSeason(int seasonId) throws RemoteException, SeasonNotFoundException;

    //pobranie zwycięzcy dla danego sezonu
}

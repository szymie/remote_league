import clubs.*;
import fixtures.Fixture;
import fixtures.FixtureAlreadyPlayedException;
import fixtures.FixturesLeftException;
import players.*;
import players.Player.Position;
import seasons.Season;
import seasons.SeasonNotCompletedException;
import seasons.SeasonNotFoundException;
import seasons.SeasonRegister;
import table.Table;
import table.TableRegister;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Optional;


public class RemoteLeagueService extends UnicastRemoteObject implements RemoteLeague {

    private PlayersRegister playersRegister = new PlayersRegister();
    private ClubsRegister clubsRegister = new ClubsRegister();
    private ClubPlayerRelationship clubPlayerRelationship;
    private SeasonRegister seasonRegister;
    private TableRegister tableRegister;

    public RemoteLeagueService() throws RemoteException {
        clubPlayerRelationship = new ClubPlayerRelationship(playersRegister, clubsRegister);
        seasonRegister = new SeasonRegister(clubsRegister);
        tableRegister = new TableRegister(clubsRegister, seasonRegister);
    }

    @Override
    public Player createPlayer(Player player) throws RemoteException {
        return playersRegister.addPlayer(player);
    }

    @Override
    public Optional<Player> findPlayerById(int id) {
        return playersRegister.get(id);
    }

    @Override
    public List<Player> findPlayersPosition(Position position) {
        return playersRegister.getByPosition(position);
    }

    @Override
    public Club createClub(Club club) throws RemoteException, ClubAlreadyExistsException {
        return clubsRegister.addClub(club);
    }

    @Override
    public Optional<Club> findClubById(int id) throws RemoteException {
        return clubsRegister.get(id);
    }

    @Override
    public Optional<Club> findClubFullName(String fullName) throws RemoteException {
        return clubsRegister.getByFullName(fullName);
    }

    @Override
    public void bindPlayerWithClub(Player player, Club club) throws RemoteException, PlayerNotFoundException, ClubNotFoundException, PlayerAlreadyBoundException {
        clubPlayerRelationship.addPlayerToClub(player, club);
    }

    @Override
    public List<Player> findPlayersBoundWithClub(Club club) throws RemoteException, ClubNotFoundException {
        return clubPlayerRelationship.getPlayersBoundWithClub(club);
    }

    @Override
    public void unbindPlayerWithClub(Player player, Club club) throws RemoteException, PlayerNotFoundException, ClubNotFoundException {
        clubPlayerRelationship.removePlayerFromClub(player, club);
    }

    @Override
    public Season createSeason(Season season) throws RemoteException, SeasonNotCompletedException {
        return seasonRegister.addSeason(season);
    }

    @Override
    public void addClubToCurrentSeason(Club club) throws RemoteException, ClubNotFoundException, SeasonNotFoundException {
        seasonRegister.addClubToCurrentSeason(club);
    }

    @Override
    public void addFixtureToCurrentSeason(Fixture fixture) throws RemoteException, ClubNotFoundException, SeasonNotFoundException, FixtureAlreadyPlayedException {
        seasonRegister.addFixtureToCurrentSeason(fixture);
    }

    @Override
    public List<Fixture> findFixturesBySeasonId(int seasonId) throws RemoteException, SeasonNotFoundException {
        return seasonRegister.getFixturesBySeasonId(seasonId);
    }

    @Override
    public void closeCurrentSeason() throws RemoteException, SeasonNotFoundException, FixturesLeftException {
        seasonRegister.closeCurrentSeason();
    }

    @Override
    public Table calculateTableForSeason(int seasonId) throws RemoteException, SeasonNotFoundException {
        return tableRegister.getTableForSeason(seasonId);
    }
}
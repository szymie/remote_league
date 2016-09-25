package seasons;

import clubs.Club;
import clubs.ClubNotFoundException;
import clubs.ClubsRegister;
import fixtures.Fixture;
import fixtures.FixtureAlreadyPlayedException;
import fixtures.FixturesLeftException;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class SeasonRegister {

    private Map<Integer, Season> seasons = new HashMap<>();
    private Queue<Season> seasonQueue = new LinkedBlockingQueue<>();
    private Map<Integer, Set<Integer>> seasonClubs = new HashMap<>();
    private Map<Integer, List<Fixture>> seasonFixtures = new HashMap<>();

    private ClubsRegister clubsRegister;

    public SeasonRegister(ClubsRegister clubsRegister) {
        this.clubsRegister = clubsRegister;
    }

    public Season addSeason(Season season) throws SeasonNotCompletedException {

        if(!isAddNewSeasonPossible()) {
            throw new SeasonNotCompletedException();
        }

        season.setState(Season.State.IN_PROGRESS);

        Season newSeason = new Season(season);

        newSeason.setId(Season.nextId++);
        seasons.put(newSeason.getId(), newSeason);

        seasonQueue.offer(newSeason);

        seasonClubs.put(newSeason.getId(), new LinkedHashSet<>());

        seasonFixtures.put(newSeason.getId(), new ArrayList<>());

        return newSeason;
    }

    private boolean isAddNewSeasonPossible() {
        Season lastSeason = seasonQueue.peek();
        return lastSeason == null || lastSeason.getState() == Season.State.COMPLETED;
    }

    public Optional<Season> get(int seasonId) {
        return Optional.ofNullable(seasons.get(seasonId));
    }

    public void addClubToCurrentSeason(Club club) throws ClubNotFoundException, SeasonNotFoundException {

        clubsRegister.get(club.getId()).orElseThrow(ClubNotFoundException::new);

        Season lastSeason = checkIfInProgressSeasonExists();

        Set<Integer> currentSeasonClubs = seasonClubs.get(lastSeason.getId());

        currentSeasonClubs.add(club.getId());

        seasonClubs.put(lastSeason.getId(), currentSeasonClubs);
    }

    private Season checkIfInProgressSeasonExists() throws SeasonNotFoundException {

        Season lastSeason = seasonQueue.peek();

        if(lastSeason == null || lastSeason.getState() == Season.State.COMPLETED) {
            throw new SeasonNotFoundException();
        } else {
            return lastSeason;
        }
    }

    public void addFixtureToCurrentSeason(Fixture fixture) throws ClubNotFoundException, SeasonNotFoundException, FixtureAlreadyPlayedException {

        int homeClubId = fixture.getHomeClubId();
        int awayClubId = fixture.getAwayClubId();

        clubsRegister.get(homeClubId).orElseThrow(() -> new ClubNotFoundException("Home club not found"));
        clubsRegister.get(awayClubId).orElseThrow(() -> new ClubNotFoundException("Away club not found"));

        Season lastSeason = checkIfInProgressSeasonExists();

        checkIfClubAddedToSeason(homeClubId, lastSeason.getId(), "Home club not found at requested season");
        checkIfClubAddedToSeason(awayClubId, lastSeason.getId(), "Away club not found at requested season");

        List<Fixture> fixtures = seasonFixtures.get(lastSeason.getId());

        Optional<Fixture> fixtureOptional = fixtures.stream()
                .filter(f ->
                        f.getHomeClubId() == fixture.getHomeClubId() &&
                                f.getAwayClubId() == fixture.getAwayClubId())
                .findFirst();

        if(fixtureOptional.isPresent()) {
            throw new FixtureAlreadyPlayedException();
        }

        Fixture newFixture = new Fixture(fixture);
        newFixture.setId(Fixture.nextId++);

        fixtures.add(newFixture);

        seasonFixtures.put(lastSeason.getId(), fixtures);
    }

    private void checkIfClubAddedToSeason(int clubId, int seasonId, String errorMessage) throws ClubNotFoundException {
        if(!isClubAddedToSeason(clubId, seasonId)) {
            throw new ClubNotFoundException(errorMessage);
        }
    }

    public boolean isClubAddedToSeason(int clubId, int seasonId) {
        Set<Integer> seasons = seasonClubs.get(seasonId);
        return seasons.stream().anyMatch(id -> id == clubId);
    }

    public List<Fixture> getFixturesBySeasonId(int seasonId) throws SeasonNotFoundException {
        return Optional.ofNullable(seasonFixtures.get(seasonId)).orElseThrow(SeasonNotFoundException::new);
    }


    public void closeCurrentSeason() throws SeasonNotFoundException, FixturesLeftException {

        Season lastSeason = seasonQueue.peek();

        if(lastSeason == null || lastSeason.getState() == Season.State.COMPLETED) {
            throw new SeasonNotFoundException();
        } else {
            lastSeason = seasonQueue.poll();
        }

        int leagueSize = seasonClubs.get(lastSeason.getId()).size();

        int numberOfFixturesToPlay = getFixturesNumberFromLeagueSize(leagueSize);
        int numberOfFixturesPlayed = seasonFixtures.get(lastSeason.getId()).size();

        if(numberOfFixturesPlayed < numberOfFixturesToPlay) {
            throw new FixturesLeftException();
        }

        lastSeason.setState(Season.State.COMPLETED);

        seasonQueue.offer(lastSeason);
    }

    private int getFixturesNumberFromLeagueSize(int leagueSize) {
        return leagueSize * leagueSize - leagueSize;
    }

    public List<Club> getClubsForSeason(int seasonId) throws SeasonNotFoundException {

        Set<Integer> clubs = seasonClubs.get(seasonId);

        if(clubs == null) {
            throw new SeasonNotFoundException();
        }

        return clubs.stream()
                .map(clubId -> clubsRegister.get(clubId))
                .filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());

    }
}

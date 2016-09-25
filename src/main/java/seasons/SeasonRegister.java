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
    private Queue<Season> seasonsQueue = new LinkedBlockingQueue<>();
    private Map<Integer, Set<Integer>> seasonClubs = new HashMap<>();
    private Map<Integer, List<Fixture>> seasonFixtures = new HashMap<>();

    private ClubsRegister clubsRegister;

    public SeasonRegister(ClubsRegister clubsRegister) {
        this.clubsRegister = clubsRegister;
    }

    public Season addSeason(Season season) throws SeasonNotCompletedException {

        if (!isAddNewSeasonPossible()) {
            throw new SeasonNotCompletedException();
        }

        season.setState(Season.State.CREATED);

        Season newSeason = new Season(season);

        newSeason.setId(Season.nextId++);
        seasons.put(newSeason.getId(), newSeason);

        seasonsQueue.offer(newSeason);

        seasonClubs.put(newSeason.getId(), new LinkedHashSet<>());

        seasonFixtures.put(newSeason.getId(), new ArrayList<>());

        return newSeason;
    }

    private boolean isAddNewSeasonPossible() {
        Season lastSeason = seasonsQueue.peek();
        return lastSeason == null || lastSeason.getState() == Season.State.COMPLETED;
    }

    public Optional<Season> get(int seasonId) {
        return Optional.ofNullable(seasons.get(seasonId));
    }

    public void addClubToCurrentSeason(Club club) throws ClubNotFoundException, SeasonNotFoundException, SeasonAlreadyStartedException {

        clubsRegister.get(club.getId()).orElseThrow(ClubNotFoundException::new);

        Season lastSeason = checkIfCreatedSeasonExists();

        Set<Integer> currentSeasonClubs = seasonClubs.get(lastSeason.getId());

        currentSeasonClubs.add(club.getId());

        seasonClubs.put(lastSeason.getId(), currentSeasonClubs);
    }

    private Season checkIfCreatedSeasonExists() throws SeasonNotFoundException, SeasonAlreadyStartedException {

        Season lastSeason = seasonsQueue.peek();

        if (lastSeason != null && lastSeason.getState() == Season.State.CREATED) {
            return lastSeason;
        } else if (lastSeason == null || lastSeason.getState() == Season.State.COMPLETED) {
            throw new SeasonNotFoundException();
        } else {
            throw new SeasonAlreadyStartedException();
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

        if (fixtureOptional.isPresent()) {
            throw new FixtureAlreadyPlayedException();
        }

        Fixture newFixture = new Fixture(fixture);
        newFixture.setId(Fixture.nextId++);

        fixtures.add(newFixture);

        seasonFixtures.put(lastSeason.getId(), fixtures);
    }

    private Season checkIfInProgressSeasonExists() throws SeasonNotFoundException {

        Season lastSeason = seasonsQueue.peek();

        if (lastSeason == null || lastSeason.getState() != Season.State.IN_PROGRESS) {
            throw new SeasonNotFoundException();
        } else {
            return lastSeason;
        }
    }

    private void checkIfClubAddedToSeason(int clubId, int seasonId, String errorMessage) throws ClubNotFoundException {
        if (!isClubAddedToSeason(clubId, seasonId)) {
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

        Season lastSeason = seasonsQueue.peek();

        if (lastSeason == null || lastSeason.getState() == Season.State.COMPLETED) {
            throw new SeasonNotFoundException();
        } else {
            lastSeason = seasonsQueue.poll();
        }

        int leagueSize = seasonClubs.get(lastSeason.getId()).size();

        int numberOfFixturesToPlay = getFixturesNumberFromLeagueSize(leagueSize);
        int numberOfFixturesPlayed = seasonFixtures.get(lastSeason.getId()).size();

        if (numberOfFixturesPlayed < numberOfFixturesToPlay) {
            throw new FixturesLeftException();
        }

        lastSeason.setState(Season.State.COMPLETED);

        seasonsQueue.offer(lastSeason);
    }


    public void startCurrentSeason() throws SeasonNotFoundException, SeasonAlreadyStartedException, NotEnoughClubsException {

        Season lastSeason = seasonsQueue.peek();

        if (lastSeason == null || lastSeason.getState() == Season.State.COMPLETED) {
            throw new SeasonNotFoundException();
        } else if(lastSeason.getState() == Season.State.IN_PROGRESS) {
            throw new SeasonAlreadyStartedException();
        } else {

            Set<Integer> clubs = seasonClubs.get(lastSeason.getId());

            if(clubs.size() < 2) {
                throw new NotEnoughClubsException();
            }

            lastSeason = seasonsQueue.poll();
            lastSeason.setState(Season.State.IN_PROGRESS);
            seasonsQueue.offer(lastSeason);
        }
    }

    private int getFixturesNumberFromLeagueSize(int leagueSize) {
        return leagueSize * leagueSize - leagueSize;
    }

    public List<Club> getClubsForSeason(int seasonId) throws SeasonNotFoundException {

        Set<Integer> clubs = seasonClubs.get(seasonId);

        if (clubs == null) {
            throw new SeasonNotFoundException();
        }

        return clubs.stream()
                .map(clubId -> clubsRegister.get(clubId))
                .filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());

    }
}
package table;

import clubs.Club;
import clubs.ClubsRegister;
import fixtures.Fixture;
import seasons.SeasonNotFoundException;
import seasons.SeasonRegister;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableRegister {

    private ClubsRegister clubsRegister;
    private SeasonRegister seasonRegister;

    private static final short WIN_POINTS = 3;
    private static final short DRAW_POINT = 1;

    public TableRegister(ClubsRegister clubsRegister, SeasonRegister seasonRegister) {
        this.clubsRegister = clubsRegister;
        this.seasonRegister = seasonRegister;
    }

    public Table getTableForSeason(int seasonId) throws SeasonNotFoundException {

        Map<Integer, TablePosition> positions = seasonRegister.getClubsForSeason(seasonId).stream()
                .collect(Collectors.toMap(Club::getId, TablePosition::fromClub));

        List<Fixture> fixtures = seasonRegister.getFixturesBySeasonId(seasonId);

        fixtures.forEach(fixture -> settleGame(fixture, positions, this::homeWin, this::awayWin, this::draw));

        List<TablePosition> tablePositions = positions.values().stream()
                .sorted(this::compareTablePositions)
                .collect(Collectors.toList());

        fillTablePositionsWithRanks(tablePositions);

        return createTable(fixtures, tablePositions);
    }

    private void settleGame(
            Fixture fixture,
            Map<Integer, TablePosition> positions,
            UpdatePositions homeWin,
            UpdatePositions awayWin,
            UpdatePositions draw) {

        if(fixture.getGoalsHomeClub() > fixture.getGoalsAwayClub()) {
            homeWin.update(fixture, positions);
        } else if(fixture.getGoalsAwayClub() > fixture.getGoalsHomeClub()) {
            awayWin.update(fixture, positions);
        } else {
            draw.update(fixture, positions);
        }
    }

    private void homeWin(Fixture fixture, Map<Integer, TablePosition> positions) {

        TablePosition homeClubTablePosition = positions.get(fixture.getHomeClubId());

        updateWin(fixture, homeClubTablePosition);

        TablePosition awayClubTablePosition = positions.get(fixture.getAwayClubId());

        updateLost(fixture, awayClubTablePosition);
    }

    private void updateWin(Fixture fixture, TablePosition tablePosition) {

        int goalsMax = Math.max(fixture.getGoalsHomeClub(), fixture.getGoalsAwayClub());
        int goalsMin = Math.min(fixture.getGoalsHomeClub(), fixture.getGoalsAwayClub());

        tablePosition.points += WIN_POINTS;
        tablePosition.gamesPlayed++;
        tablePosition.gamesWon++;
        tablePosition.goalsFor += goalsMax;
        tablePosition.goalsAgainst += goalsMin;
    }

    private void awayWin(Fixture fixture, Map<Integer, TablePosition> positions) {

        TablePosition homeClubTablePosition = positions.get(fixture.getHomeClubId());

        updateLost(fixture, homeClubTablePosition);

        TablePosition awayClubTablePosition = positions.get(fixture.getAwayClubId());

        updateWin(fixture, awayClubTablePosition);
    }

    private void updateLost(Fixture fixture, TablePosition tablePosition) {

        int goalsMax = Math.max(fixture.getGoalsHomeClub(), fixture.getGoalsAwayClub());
        int goalsMin = Math.min(fixture.getGoalsHomeClub(), fixture.getGoalsAwayClub());

        tablePosition.gamesPlayed++;
        tablePosition.gamesLost++;
        tablePosition.goalsFor += goalsMin;
        tablePosition.goalsAgainst += goalsMax;
    }

    private void draw(Fixture fixture, Map<Integer, TablePosition> positions) {

        TablePosition homeClubTablePosition = positions.get(fixture.getHomeClubId());

        updateDraw(fixture, homeClubTablePosition);

        TablePosition awayClubTablePosition = positions.get(fixture.getAwayClubId());

        updateDraw(fixture, awayClubTablePosition);
    }

    private void updateDraw(Fixture fixture, TablePosition tablePosition) {

        tablePosition.points += DRAW_POINT;
        tablePosition.gamesPlayed++;
        tablePosition.gamesDrawn++;
        tablePosition.goalsFor += fixture.getGoalsHomeClub();
        tablePosition.goalsAgainst += fixture.getGoalsAwayClub();
    }

    private Table createTable(List<Fixture> fixtures, List<TablePosition> tablePositions) {

        int matchDay = fixtures.stream().mapToInt(Fixture::getMatchDay).max().orElse(0);

        return new Table(matchDay, tablePositions);
    }

    private int compareTablePositions(TablePosition leftTablePosition, TablePosition rightTablePosition) {

        int pointsDifference = rightTablePosition.points - leftTablePosition.points;

        if(pointsDifference != 0) {
            return pointsDifference;
        }

        int goalsBalanceDifference = (rightTablePosition.goalsFor - rightTablePosition.goalsAgainst) - (leftTablePosition.goalsFor - leftTablePosition.goalsAgainst);

        if(goalsBalanceDifference != 0) {
            return goalsBalanceDifference;
        }

        int goalsScoredDifference = rightTablePosition.goalsFor - leftTablePosition.goalsFor;

        if(goalsScoredDifference != 0) {
            return goalsScoredDifference;
        }

        return 0;
    }

    private void fillTablePositionsWithRanks(List<TablePosition> tablePositions) {

        short currentRank = 1;

        for(TablePosition tablePosition : tablePositions) {
            tablePosition.rank = currentRank++;
        }
    }
}

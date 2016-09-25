package fixtures;

import java.io.Serializable;
import java.rmi.Remote;

public class Fixture implements Serializable, Remote {

    public static int nextId = 1;

    private Integer id;
    private int homeClubId;
    private int awayClubId;
    private int goalsHomeClub;
    private int goalsAwayClub;
    private int matchDay;

    public Fixture() {
    }

    public Fixture(Fixture fixture) {
        id = fixture.id;
        homeClubId = fixture.homeClubId;
        awayClubId = fixture.awayClubId;
        goalsHomeClub = fixture.goalsHomeClub;
        goalsAwayClub = fixture.goalsAwayClub;
        matchDay = fixture.matchDay;
    }

    public Fixture(int homeClubId, int awayClubId, int goalsHomeClub, int goalsAwayClub, int matchDay) {
        this.homeClubId = homeClubId;
        this.awayClubId = awayClubId;
        this.goalsHomeClub = goalsHomeClub;
        this.goalsAwayClub = goalsAwayClub;
        this.matchDay = matchDay;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getHomeClubId() {
        return homeClubId;
    }

    public void setHomeClubId(int homeClubId) {
        this.homeClubId = homeClubId;
    }

    public int getAwayClubId() {
        return awayClubId;
    }

    public void setAwayClubId(int awayClubId) {
        this.awayClubId = awayClubId;
    }

    public int getGoalsHomeClub() {
        return goalsHomeClub;
    }

    public void setGoalsHomeClub(int goalsHomeClub) {
        this.goalsHomeClub = goalsHomeClub;
    }

    public int getGoalsAwayClub() {
        return goalsAwayClub;
    }

    public void setGoalsAwayClub(int goalsAwayClub) {
        this.goalsAwayClub = goalsAwayClub;
    }

    public int getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(int matchDay) {
        this.matchDay = matchDay;
    }
}


package table;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.List;

public class Table implements Remote, Serializable {

    private int matchDay;
    private List<TablePosition> standings;

    public Table() {
    }

    public Table(int matchDay, List<TablePosition> standings) {
        this.matchDay = matchDay;
        this.standings = standings;
    }

    public int getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(int matchDay) {
        this.matchDay = matchDay;
    }

    public List<TablePosition> getStandings() {
        return standings;
    }

    public void setStandings(List<TablePosition> standings) {
        this.standings = standings;
    }
}
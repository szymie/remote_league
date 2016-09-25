package seasons;

import java.io.Serializable;
import java.rmi.Remote;

public class Season implements Remote, Serializable {

    static int nextId = 1;

    private Integer id;
    private String name;
    private State state;

    public enum State {
        IN_PROGRESS,
        COMPLETED
    }

    public Season() {
    }

    public Season(Season season) {
        id = season.id;
        name = season.name;
        state = season.state;
    }

    public Season(String name, State state) {
        this.name = name;
        this.state = state;
    }

    public Season(Integer id, String name, State state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}

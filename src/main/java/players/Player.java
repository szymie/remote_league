package players;

import java.io.Serializable;

public class Player implements Serializable {

    static int nextId = 1;

    private Integer id;
    private String firstName;
    private String lastName;
    private Position position;
    private String country;

    public enum Position {
        STRIKER,
        MIDFIELD,
        DEFENCE,
        GOALKEEPER
    }

    public Player() {
    }

    public Player(Player player) {
        id = player.id;
        firstName = player.firstName;
        lastName = player.lastName;
        position = player.position;
        country = player.country;
    }

    public Player(String firstName, String lastName, Position position, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.country = country;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }

    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
}

package clubs;

import java.io.Serializable;
import java.rmi.Remote;

public class Club implements Serializable, Remote {

    static int nextId = 1;

    private Integer id;
    private String fullName;
    private String nickname;
    private String stadium;

    public Club() {
    }

    public Club(String fullName, String nickname, String stadium) {
        this.fullName = fullName;
        this.nickname = nickname;
        this.stadium = stadium;
    }

    public Club(Club club) {
        id = club.id;
        fullName = club.fullName;
        nickname = club.nickname;
        stadium = club.stadium;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }
}

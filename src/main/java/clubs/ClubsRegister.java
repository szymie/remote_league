package clubs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClubsRegister {

    private Map<Integer, Club> clubs = new HashMap<>();

    public Club addClub(Club club) throws ClubAlreadyExistsException {

        if(contains(club.getFullName())) {
           throw new ClubAlreadyExistsException();
        }

        Club newClub = new Club(club);

        newClub.setId(Club.nextId++);
        clubs.put(newClub.getId(), newClub);

        return newClub;
    }

    private boolean contains(String fullName) {
        return clubs.values().stream().anyMatch(club -> club.getFullName().equals(fullName));
    }
    public Optional<Club> get(int clubId) {
        return Optional.ofNullable(clubs.get(clubId));
    }

    public Optional<Club> getByFullName(String fullName) {
        return clubs.values().stream()
                .filter(club -> club.getFullName().equals(fullName))
                .findFirst();
    }
}

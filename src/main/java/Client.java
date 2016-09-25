import clubs.Club;
import fixtures.Fixture;
import players.Player;
import seasons.Season;
import table.Table;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.List;

public class Client {

    public static void main(String args[]) {

        System.setProperty("java.security.policy", "file:////home/szymie/IdeaProjects/remote_league/src/main/java/.policy");

        if (System.getSecurityManager() == null){
            System.setSecurityManager(new RMISecurityManager());
        }

        try {

            String name = "//localhost:1099/RemoteLeagueService";

            RemoteLeague remoteLeague = (RemoteLeague) Naming.lookup(name);

            Club chelsea = new Club("Chelsea F.C.", "The Blues", "Stamford Bridge");
            Club arsenal = new Club("Arsenal F.C.", "The Gunners", "Emirates Stadium");
            Player edenHazard = new Player("Eden", "Hazard", Player.Position.MIDFIELD, "Belgium");
            Player frankLampard = new Player("Frank", "Lampard", Player.Position.MIDFIELD, "Belgium");

            chelsea = remoteLeague.createClub(chelsea);
            arsenal = remoteLeague.createClub(arsenal);

            edenHazard = remoteLeague.createPlayer(edenHazard);
            frankLampard = remoteLeague.createPlayer(frankLampard);

            remoteLeague.bindPlayerWithClub(edenHazard, chelsea);
            remoteLeague.bindPlayerWithClub(frankLampard, chelsea);

            remoteLeague.findPlayersBoundWithClub(chelsea).forEach(player -> System.out.println(player.getLastName()));

            remoteLeague.unbindPlayerWithClub(frankLampard, chelsea);

            remoteLeague.findPlayersBoundWithClub(chelsea).forEach(player -> System.out.println(player.getLastName()));

            remoteLeague.unbindPlayerWithClub(frankLampard, chelsea);

            //---

            Season season = new Season("2016/2017", Season.State.IN_PROGRESS);
            season = remoteLeague.createSeason(season);

            remoteLeague.addClubToCurrentSeason(chelsea);
            remoteLeague.addClubToCurrentSeason(arsenal);

            remoteLeague.startCurrentSeason();

            remoteLeague.closeCurrentSeason();

            remoteLeague.addFixtureToCurrentSeason(new Fixture(chelsea.getId(), arsenal.getId(), 1, 0, 1));
            remoteLeague.addFixtureToCurrentSeason(new Fixture(arsenal.getId(), chelsea.getId(), 0, 3, 2));

            List<Fixture> fixtures = remoteLeague.findFixturesBySeasonId(season.getId());

            remoteLeague.closeCurrentSeason();

            Table table = remoteLeague.calculateTableForSeason(season.getId());

            System.out.println(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

import java.rmi.Naming;

public class Server {

    public static void main(String[] args) {

        System.setProperty("java.security.policy", "file:////home/szymie/IdeaProjects/remote_library/src/main/java/.policy");

        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            String serviceName = "//localhost:1099/RemoteLeagueService";
            RemoteLeague remoteLeague = new RemoteLeagueService();
            Naming.rebind(serviceName, remoteLeague);
            System.out.println("Server is running!");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
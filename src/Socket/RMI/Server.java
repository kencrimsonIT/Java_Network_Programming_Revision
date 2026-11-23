package Socket.RMI;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private static final int PORT = 6000;

    static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.rebind("Search", new SearcherImpl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

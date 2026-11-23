package Socket.TCP_IP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { //Create serverSocket as port listener
            System.out.println("Waiting for connection...");
            while (true) {
                Socket socket = serverSocket.accept(); //Accept ServerSocket
                new ServerProcess(socket).start();     //Start the server thread
                System.out.println("Connection established!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package Socket.RMI;

import Socket.TCP_IP.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.StringTokenizer;

public class ServerProcess {
    private Registry registry;
    private Socket socket;
    private BufferedReader userIn;
    private PrintWriter userOut;
    private Searcher dao;

    public ServerProcess() throws IOException, NotBoundException {
        this.registry = LocateRegistry.getRegistry("localhost", 5000);
        this.userIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.userOut = new PrintWriter(this.socket.getOutputStream(), true);
    }

    public void run() {
        sendResponse(userOut, "Hello there!");
        String command, param, syntax;
        String username = null;
        boolean isSignedIn = false;
        boolean isQuit = false;
        StringTokenizer st;

        //Before sign in
        while (!isSignedIn && !isQuit) {
            try {
                this.userOut.print(">");
                this.userOut.flush();
                command = this.userIn.readLine();
                if (command.isBlank()) {
                    sendResponse(userOut, "Please enter your username!");
                    continue;
                }
                st = new StringTokenizer(command);
                syntax = st.nextToken();
                param = command.substring(syntax.length()).trim();

                //Command cases
                switch (syntax.toUpperCase()) {
                    case "QUIT":
                        sendResponse(userOut, "Bye!");
                        isQuit = true;
                        break;

                    case "USER":
                        if (this.dao.checkUsername(param)){
                            username = param;
                            sendResponse(userOut, "You are now logged in!");
                        } else {
                            sendResponse(userOut, "Cannot find username!");
                        }
                        break;

                    case "PASS":
                        if (username == null) {
                            sendResponse(userOut, "Please enter your username!");
                            continue;
                        }
                        isSignedIn = this.dao.checkSignIn(username, param);
                        if (!isSignedIn) {
                            sendResponse(userOut, "You are not logged in!");
                        } else {
                            sendResponse(userOut, "You are now logged in!");
                        }
                        break;

                    default:
                        sendResponse(userOut, "Unknown command!");
                        break;
                }
            } catch (RemoteException e) {
                sendResponse(userOut, "Remote exception!");
                e.printStackTrace();
            }
            catch (IOException e) {
                isQuit = true;
                e.printStackTrace();
            }
        }

        //After sign in
        while (isSignedIn && !isQuit) {
            try {
                this.userOut.print(">");
                this.userOut.flush();
                command = this.userIn.readLine();
                if (command.isBlank()) {
                    sendResponse(userOut, "Please enter your username!");
                    continue;
                }
                st = new StringTokenizer(command);
                syntax = st.nextToken();
                param = command.substring(syntax.length()).trim();

                //Command cases
                switch (syntax.toUpperCase()) {
                    case "QUIT":
                        sendResponse(userOut, "Bye!");
                        isQuit = true;
                        break;

                    case "FINDID":
                        displayStudentList(this.dao.findStudentById(param));
                        break;

                    case "FINDNAME":
                        displayStudentList(this.dao.findStudentByName(param));
                        break;

                    default:
                        sendResponse(userOut, "Unknown command!");
                        break;
                }
            } catch (RemoteException e) {
                sendResponse(userOut, "Remote exception!");
                e.printStackTrace();
            } catch (IOException e) {
                isQuit = true;
                e.printStackTrace();
            }
        }

        //Close server
        try {
            this.socket.close();
            this.userIn.close();
            this.userOut.close();
        } catch (IOException e) {
            isQuit = true;
            e.printStackTrace();
        }
    }

    private void sendResponse(PrintWriter pw, String message) {
        pw.println(message);
        pw.flush();
    }

    private void displayStudentList(List<StudentRMI> studentList) {
        if (studentList == null ||  studentList.isEmpty()) {
            sendResponse(userOut, "No students found!");
            return;
        }

        for (StudentRMI student : studentList) {
            sendResponse(userOut, student.toString());
        }
    }

    static void main(String[] args) throws IOException, NotBoundException {
        ServerProcess process = new ServerProcess();
        process.run();
    }
}

package Socket.TCP_IP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

public class ServerProcess extends Thread {
    private Socket socket;
    private BufferedReader userIn;
    private PrintWriter userOut;
    private Dao dao;

    public ServerProcess(Socket socket) {
        try {
            this.socket = socket;
            this.userIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.userOut = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String command, param, syntax;
        String username = null;
        boolean isSignedIn = false;
        StringTokenizer st;
        sendResponse(userOut, "Hello there!"); //Display response after user connected server
        QUIT:

        //Before sign in
        while (!isSignedIn) {
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
                switch (command.toUpperCase()) {
                    case "QUIT":
                        sendResponse(userOut, "Bye!");
                        break QUIT;

                    case "USER":
                        if (this.dao.checkUsername(param)) {
                            username = param;
                            sendResponse(userOut, "Your username: " + username);
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
                            sendResponse(userOut, "Incorrect username or password!");
                        } else {
                            sendResponse(userOut, "Signed in successfully!");
                        }
                        break;

                    default:
                        sendResponse(userOut, "Unknown command!");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        QUIT:
        //After sign in
        while (isSignedIn) {
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
                switch (command.toUpperCase()) {
                    case "QUIT":
                        sendResponse(userOut, "Bye!");
                        break QUIT;

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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Close server
        try {
            this.socket.close();
            this.userIn.close();
            this.userOut.close();
            this.dao.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Send response helper method
    private void sendResponse(PrintWriter pw, String message) {
        pw.println(message);
        pw.flush();
    }

    //Display student list helper method
    private void displayStudentList(List<Student> studentList) {
        if (studentList == null ||  studentList.isEmpty()) {
            sendResponse(userOut, "No students found!");
            return;
        }

        for (Student student : studentList) {
            sendResponse(userOut, student.toString());
        }
    }
}

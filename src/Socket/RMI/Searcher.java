package Socket.RMI;

import java.rmi.Remote;
import java.util.List;

public interface Searcher extends Remote {
    boolean checkUsername(String username);
//    boolean checkSignIn(String username, String password);
    boolean checkSignIn(String username, String param);
    List<StudentRMI> findStudentById(String param);
    List<StudentRMI> findStudentByName(String param);
}

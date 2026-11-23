package Socket.RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class SearcherImpl extends UnicastRemoteObject implements Searcher {
    protected SearcherImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean checkUsername(String username) {
        return new Dao().checkUsername(username);
    }

    @Override
    public boolean checkSignIn(String username, String param) {
        return new Dao().checkSignIn(username, param);
    }

    @Override
    public List<StudentRMI> findStudentById(String param) {
        return new Dao().findStudentById(param);
    }

    @Override
    public List<StudentRMI> findStudentByName(String param) {
        return new Dao().findStudentByName(param);
    }
}

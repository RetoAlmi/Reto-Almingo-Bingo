import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ComputeService extends Remote {
    // Método remoto de ejemplo (ej. sumar dos números)
    public int sumar(int a, int b) throws RemoteException;
}
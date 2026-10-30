import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// Implementación de la interfaz remota
class ComputeServiceImpl extends UnicastRemoteObject implements ComputeService {
    
    public ComputeServiceImpl() throws RemoteException {
        super(); // Constructor necesario para exportar el objeto
    }

    @Override
    public int sumar(int a, int b) throws RemoteException {
        System.out.println("Petición recibida: sumando " + a + " + " + b);
        return a + b;
    }
}

// Clase principal del Servidor
public class Server {
    public static void main(String[] args) {
        try {
            // 1. Crear el registro RMI en el puerto por defecto (1099)
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // 2. Instanciar el objeto remoto
            ComputeService service = new ComputeServiceImpl();
            
            // 3. Vincular el objeto remoto con un nombre único en el registro
            registry.rebind("ComputeService", service);
            
            System.out.println("Servidor RMI iniciado y listo...");
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
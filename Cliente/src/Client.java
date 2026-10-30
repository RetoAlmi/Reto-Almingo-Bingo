import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        try {
            // 1. Obtener el registro RMI corriendo en el servidor (localhost o IP remota)
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            
            // 2. Buscar el objeto remoto mediante su nombre registrado
            ComputeService service = (ComputeService) registry.lookup("ComputeService");
            
            // 3. Invocar el método remoto
            int resultado = service.sumar(15, 25);
            
            System.out.println("Resultado obtenido del servidor: " + resultado);
            
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
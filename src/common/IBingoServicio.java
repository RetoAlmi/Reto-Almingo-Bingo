package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBingoServicio extends Remote {
    // REGISTRO DE UN NUEVO JUGADOR
    int registrarJugador(String nombre) throws RemoteException;
    
    // OBTENER EL CARTON ASIGNADO DEL JUGADOR
    int[][] obtenerCarton(int idJugador) throws RemoteException;
    
    // OBTENER EL ULTIMO NUMERO SACADO DEL SERVIDOR
    int obtenerUltimoNumero() throws RemoteException;
    
    // MARCAR UN NUMERO EN EL CARTON DEL JUGADOR
    boolean marcarNumero(int idJugador, int numero) throws RemoteException;
    
    // CANTAR LINEA
    boolean cantarLinea(int idJugador) throws RemoteException;
    
    // CANTAR BINGO
    boolean cantarBingo(int idJugador) throws RemoteException;
    
    // RESPONDER A UNA PREGUNTA DE SOSTENIBILIDAD (DEVUELVE true SI ES CORRECTO)
    boolean responderPregunta(int idJugador, String respuesta) throws RemoteException;
    
    // OBTENER LA PREGUNTA DE SOSTENIBILIDAD ACTUAL PARA EL JUGADOR
    String obtenerPreguntaActual(int idJugador) throws RemoteException;
}
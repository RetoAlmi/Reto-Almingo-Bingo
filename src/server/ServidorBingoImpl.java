package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

// IMPORTAR CLASES E INTERFACES COMUNES
import common.Carton;
import common.IBingoServicio;
import common.PreguntaSostenibilidad;

public class ServidorBingoImpl extends UnicastRemoteObject implements IBingoServicio {
	private static final long serialVersionUID = 1L;
	
	// ID --> NOMBRE
	private Map<Integer, String> jugadores;
	// ID --> CARTONES
	private Map<Integer, Carton> cartones;
	// NUMEROS DEL 1 AL 90
	private List<Integer> bombo;
	// ULTIMO NUMERO SACADO
	private int ultimoNumeroSacado;
	// LISTA PREGUNTAS
	private List<PreguntaSostenibilidad> bancoPreguntas;
	// ID JUGADOR --> PREGUNTA ACTUAL
	private Map<Integer, PreguntaSostenibilidad> preguntasAsignadas;
	// CANTIDAD JUGADORES
	private int contadorJugadores = 0;
	
	// IMPLEMENTACION DEL SERVIDOR DEL BINGO
	public ServidorBingoImpl() throws RemoteException {
		// EXPORTAR AUTOMATICAMENTE
		super();
		jugadores = new HasMaps<>();
		cartones = new HasMaps<>();
		bombo = new ArrayList<>();
		preguntasAsignadas = new HasMaps<>();
		ultimoNumeroSacado = 0;
		
		// INICIALIZAR BOMBO CON NUMEROS DEL 1 AL 90
		for (int contador = 1; contador <= 90; contador++) {
			bombo.add(contador);
		}
		Collections.shuffle(bombo);
		
		// BANCO DE PREGUNTAS DE SOSTENIBILIDAD
		bancoPreguntas = new ArrayList<>();
		// PREGUNTAS
        bancoPreguntas.add(new PreguntaSostenibilidad("Pregunta1", "respuesta"));
        bancoPreguntas.add(new PreguntaSostenibilidad("Pregunta2", "respuesta"));
	}
	
	// REGISTRAR JUGADOR
	@Override
	public synchronized int registrarJugador(String nombre) throws RemoteException {
		contadorJugadores++;
		int id = contadorJugadores;
		jugadores.put(id, nombre);
		cartones.put(id, new Carton());
		System.out.println("Jugadores conectado: " + nombre + " (ID: " + id + ")");
		return id;
	}
	
	// OBTENER CARTON
	public int[][] obtenerCarton(int idJugador) throws RemoteException {
		return cartones.get(idJugador).getNumeros();
	}
	
	// OBTENER ULTIMO NUMERO

    @Override
    public synchronized int obtenerUltimoNumero() throws RemoteException {
        return ultimoNumeroSacado;
    }
}












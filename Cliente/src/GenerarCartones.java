/* 90 BOLAS --> CARTONES: 3 FILAS x 9 COLUMNAS, 15 NUMEROS, 5 POR FILA, FALTAN 12
 * 
 */

import java.rmi.*;

public class GenerarCartones {
	private int filas;
	private int columnas;
	private int vacios;
	private int numeros;
	private int jugadores;
	private int[][] carton;
	
	public GenerarCartones() {
		filas = 3;
		columnas = 9;
		vacios = 12;
		numeros = 15;
		jugadores = 0;
	}
	
	public GenerarCartones(int jugadores) {
		this.jugadores = jugadores;
	}

	public int getJugadores() {
		return jugadores;
	}

	public void setJugadores(int jugadores) {
		this.jugadores = jugadores;
	}
}

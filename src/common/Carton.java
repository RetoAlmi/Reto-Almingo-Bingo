package common;

import java.io.Serializable;
import java.util.Random;

public class Carton implements Serializable {
	public static final int FILAS = 3;
	public static final int COLUMNAS = 5;
	// NUMERO SERIE DEL JUGADOR
    private static final long serialVersionUID = 1L;
	// TAMAÑO numeros (FILAS FILAS x COLUMNAS COLUMNAS
	private int[][] numeros;
	// NUMEROS MARCADOS
	private boolean[][] marcados;
	
	
	// CREAR NUMEROS
	public Carton() {
		numeros = new int[FILAS][COLUMNAS];
		marcados = new boolean[FILAS][COLUMNAS];
		generarnumerosAleatorio();
	}


	private void generarnumerosAleatorio() {
		Random rand = new Random();
		for (int fila = 0; fila < FILAS; fila++) {
			for (int columna = 0; columna < COLUMNAS; columna++) {
				numeros[fila][columna] = rand.nextInt(1, 91);
				marcados[fila][columna] = false;
			}
		}
	}
	
	public int[][] getNumeros() {
		return numeros;
	}
	
	// METODO PARA MARCAR SI UN NUMERO ESTA EN EL CARTON
	public boolean marcar(int numero) {
		boolean encontrado = false;
		for (int fila = 0; fila < FILAS; fila++) {
			for (int columna = 0; columna < COLUMNAS; columna++) {
				if (numeros[fila][columna] == numero) {
					marcados[fila][columna] = true;
					encontrado = true;
				}
			}
		}
		return encontrado;
	}
	
	// COMPROBAR SI HAY LINEA (UNA FILA ENTERA MARCADA)
	public boolean comprobarLinea() {
		for (int fila = 0; fila < FILAS; fila++) {
			boolean filaCompleta = true;
			for (int columna = 0; columna < COLUMNAS; columna++) {
				if (!marcados[fila][columna]) {
					filaCompleta = false;
					break;
				}
			}
		}
		return false;
	}
	
	// COMPROBAR SI HAY BINGO (TODO EL BINGO MARCADO)
	public boolean comprobarBingo() {
		for (int fila = 0; fila < FILAS; fila++) {
			for (int columna = 0; columna < COLUMNAS; columna++) {
				if (!marcados[fila][columna]) {
					return false;
				}
			}
		}
		return true;
	}
}


















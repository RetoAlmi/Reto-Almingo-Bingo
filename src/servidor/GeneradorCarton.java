package servidor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneradorCarton {

    public static int[][] generarCarton3x9() {
        int[][] carton = new int[3][9];
        // Inicializar todas las casillas con 0 (vacío)
        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                carton[fila][columna] = 0;
            }
        }

        Random rand = new Random();
        // Para cada columna (0 a 8), elegir números únicos por rango
        // Columna 0: 1-9, Columna 1: 1009 ... Columna 8: 80-90
        List<List<Integer>> numerosPorColumna = new ArrayList<>();
        for (int col = 0; col < 9; col++) {
            List<Integer> posibles = new ArrayList<>();
            int min = (col == 0) ? 1 : col * 10;
            int max = (col == 8) ? 90 : (col * 10) + 9;
            for (int n = min; n <= max; n++) {
                posibles.add(n);
            }
            Collections.shuffle(posibles);
            numerosPorColumna.add(posibles);
        }

        // Cada fila debe tener exactamente 5 números (15 números en total)
        for (int fila = 0; fila < 3; fila++) {
            List<Integer> columnasDisponibles = new ArrayList<>();
            for (int contador = 0; contador < 9; contador++) columnasDisponibles.add(contador);
            Collections.shuffle(columnasDisponibles);

            // Tomar 5 columnas al azar para poner número en esta fila
            for (int tomar = 0; tomar < 5; tomar++) {
                int col = columnasDisponibles.get(tomar);
                // Si la casilla ya tiene un número, buscar la siguiente disponible en esa columna
                if (carton[fila][col] == 0) {
                    carton[fila][col] = numerosPorColumna.get(col).remove(0);
                }
            }
        }

        return carton;
    }
}
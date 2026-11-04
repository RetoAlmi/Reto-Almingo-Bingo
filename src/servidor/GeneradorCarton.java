package servidor;

import java.util.Random;

public class GeneradorCarton {

    public static int[][] generarCarton3x9() {
        int[][] carton = new int[3][9];
        Random rnd = new Random();
        
        // 1. Inicializar toda la matriz con 0 (12 huecos vacíos en total, 4 por fila)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                carton[i][j] = 0;
            }
        }
        
        // 2. Rellenar cada fila para que tenga exactamente 5 números
        for (int i = 0; i < 3; i++) {
            int numerosColocados = 0;
            while (numerosColocados < 5) {
                int j = rnd.nextInt(9); // Columna aleatoria de 0 a 8
                
                // Si la celda está vacía (es 0)
                if (carton[i][j] == 0) {
                    // Definir rango según la columna del bingo
                    int min = (j == 0) ? 1 : j * 10;
                    int max = (j == 8) ? 90 : (j * 10) + 9;
                    
                    int num = min + rnd.nextInt((max - min) + 1);
                    
                    // Comprobar que el número no esté repetido en la misma columna verticalmente
                    boolean repetido = false;
                    for (int f = 0; f < 3; f++) {
                        if (carton[f][j] == num) {
                            repetido = true;
                            break;
                        }
                    }
                    
                    if (!repetido) {
                        carton[i][j] = num;
                        numerosColocados++;
                    }
                }
            }
        }

        return carton;
    }
}
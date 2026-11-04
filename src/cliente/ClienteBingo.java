package cliente;

import comun.Config;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Random;

public class ClienteBingo {

    public static void main(String[] args) {
        Scanner scTeclado = new Scanner(System.in);
        
        System.out.println("=== CLIENTE DEL BINGO ALMINGO ===");
        System.out.print("Introduce tu nombre: ");
        String nombre = scTeclado.next();
        
        // Generar un UID único para la sesión del usuario
        long uid = System.currentTimeMillis();
        String nombreCarpeta = nombre + "-" + uid;
        
        // Ruta de la carpeta de este usuario en la red compartida
        File dirJugador = new File(Config.RUTA_COMPARTIDA + "\\usuarios\\" + nombreCarpeta);
        dirJugador.mkdirs();
        
        // 1. Guardar el archivo de identificación numero-{nombre-uid}.txt
        try {
            PrintWriter pwId = new PrintWriter(new File(dirJugador, "numero-" + nombreCarpeta + ".txt"));
            pwId.println(nombreCarpeta);
            pwId.println(nombre);
            pwId.close(); // Cerrar fichero según la teoría
        } catch (FileNotFoundException e) {
            System.out.println("Error al guardar el identificador.");
        }
        
        // 2. El cliente genera su propio cartón 3x9 (5 números por fila, 12 ceros)
        int[][] carton = generarCartonCliente();
        
        // 3. Guardar el cartón en carton.txt para que el servidor lo detecte
        try {
            PrintWriter pwCarton = new PrintWriter(new File(dirJugador, "carton.txt"));
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    pwCarton.println(carton[i][j]);
                }
            }
            pwCarton.close(); // Cerrar fichero
            System.out.println("\n[INFO] ¡Cartón generado y guardado en la red con éxito!");
            System.out.println("-> Carpeta asignada: " + nombreCarpeta);
        } catch (FileNotFoundException e) {
            System.out.println("Error al guardar el cartón.");
        }
        
        // Leer los números que han salido en partida.txt para comprobar aciertos
        int[] salidos = new int[100];
        int contadorSalidos = 0;
        
        try {
            Scanner scPartida = new Scanner(new File(Config.RUTA_COMPARTIDA + "\\partida.txt"));
            while (scPartida.hasNext()) { // Lectura secuencial con hasNext
                salidos[contadorSalidos] = scPartida.nextInt();
                contadorSalidos++;
            }
            scPartida.close();
        } catch (FileNotFoundException e) {
            // Si partida.txt no existe todavía, continúa con el array vacío
        }
        
        // Imprimir el cartón 3x9 por pantalla aplicando el formato de 0 y guiones (-)
        System.out.println("\n=== TU CARTÓN DE BINGO (3x9) ===");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                int valor = carton[i][j];
                
                if (valor == 0) {
                    System.out.print("[  0  ]\t");
                } else {
                    boolean haSalido = false;
                    for (int k = 0; k < contadorSalidos; k++) {
                        if (salidos[k] == valor) {
                            haSalido = true;
                            break;
                        }
                    }
                    
                    if (haSalido) {
                        System.out.print("[-" + valor + "]\t");
                    } else {
                        System.out.print("[" + valor + "]\t");
                    }
                }
            }
            System.out.println();
        }
        
        scTeclado.close();
    }

    // Método auxiliar para que el cliente genere el cartón aleatorio
    private static int[][] generarCartonCliente() {
        int[][] carton = new int[3][9];
        Random rnd = new Random();
        
        // Inicializar con 0 (12 huecos vacíos en total, 4 por fila)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                carton[i][j] = 0;
            }
        }
        
        // Rellenar cada fila para que tenga exactamente 5 números
        for (int i = 0; i < 3; i++) {
            int numerosColocados = 0;
            while (numerosColocados < 5) {
                int j = rnd.nextInt(9);
                
                if (carton[i][j] == 0) {
                    int min = (j == 0) ? 1 : j * 10;
                    int max = (j == 8) ? 90 : (j * 10) + 9;
                    
                    int num = min + rnd.nextInt((max - min) + 1);
                    
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
package cliente;

import comun.Config;
import java.io.*;
import java.util.*;

public class ClienteBingo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce tu ID de jugador: ");
        int idJugador = scanner.nextInt();
        
        System.out.println("Usuario: Alumnado");
        System.out.println("Contraseña: Almi26-27");
        File dirUsuario = new File(Config.RUTA_COMPARTIDA + "\\usuarios\\" + idJugador);
        File fCarton = new File(dirUsuario, "carton.txt");

        if (!fCarton.exists()) {
            System.out.println("Error: No se encuentra tu cartón en la carpeta compartida.");
            return;
        }

        int[][] carton = cargarCarton(fCarton);
        
        System.out.println("\n=== TU CARTÓN DE BINGO (3x9) ===");
        imprimirCarton(carton);

        System.out.println("\n1. Ver números que han salido");
        System.out.println("2. Responder Pregunta de Sostenibilidad (para cantar)");
        System.out.print("Selecciona opción: ");
        int opt = scanner.nextInt();

        if (opt == 1) {
            mostrarNumerosSalidos();
        } else if (opt == 2) {
            hacerPreguntaSostenibilidad(scanner);
        }
    }

    private static int[][] cargarCarton(File fCarton) {
        int[][] carton = new int[3][9];
        try (BufferedReader br = new BufferedReader(new FileReader(fCarton))) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    String linea = br.readLine();
                    if (linea != null) {
                        carton[i][j] = Integer.parseInt(linea.trim());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar el cartón: " + e.getMessage());
        }
        return carton;
    }

    private static void imprimirCarton(int[][] carton) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                if (carton[i][j] == -1) {
                    System.out.print("[  X  ]\t");
                } else {
                    System.out.printf("[%2d  ]\t", carton[i][j]);
                }
            }
            System.out.println();
        }
    }

    private static void mostrarNumerosSalidos() {
        File fPartida = new File(Config.RUTA_COMPARTIDA, "partida.txt");
        if (!fPartida.exists()) {
            System.out.println("Aún no ha comenzado la partida.");
            return;
        }

        System.out.println("\n--- Números extraídos hasta el momento ---");
        try (BufferedReader br = new BufferedReader(new FileReader(fPartida))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.print(linea + " ");
            }
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error leyendo números salidos: " + e.getMessage());
        }
    }

    private static void hacerPreguntaSostenibilidad(Scanner scanner) {
        // Elegir una pregunta aleatoria (por ejemplo carpeta 1)
        int numPregunta = 1; 
        File dirPregunta = new File(Config.RUTA_COMPARTIDA + "\\preguntas\\" + numPregunta);
        File fPregunta = new File(dirPregunta, "pregunta.txt");

        if (!fPregunta.exists()) {
            System.out.println("No se encontró la pregunta número " + numPregunta);
            return;
        }

        try {
            // Leer pregunta y respuesta correcta
            BufferedReader br = new BufferedReader(new FileReader(fPregunta));
            String enunciado = br.readLine();
            String respuestaCorrecta = br.readLine().trim().toLowerCase();
            br.close();

            System.out.println("\nPREGUNTA DE SOSTENIBILIDAD:");
            System.out.println(enunciado);

            // Mostrar opciones a, b, c, d
            String[] opciones = {"a", "b", "c", "d"};
            for (String op : opciones) {
                File fOp = new File(dirPregunta, op + ".txt");
                if (fOp.exists()) {
                    BufferedReader brOp = new BufferedReader(new FileReader(fOp));
                    System.out.println(op.toUpperCase() + ") " + brOp.readLine());
                    brOp.close();
                }
            }

            System.out.print("\nTu respuesta (a/b/c/d): ");
            String resUsuario = scanner.next().trim().toLowerCase();

            if (resUsuario.equals(respuestaCorrecta)) {
                System.out.println("¡Correcto! Respuesta validada.");
            } else {
                System.out.println("Incorrecto. La respuesta adecuada era la " + respuestaCorrecta.toUpperCase());
            }

        } catch (IOException e) {
            System.err.println("Error al procesar la pregunta: " + e.getMessage());
        }
    }
}
package cliente;

import comun.Config;
import java.io.*;
import java.util.*;

public class ClienteBingo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== CLIENTE DEL BINGO ALMINGO ===");
        System.out.print("Introduce tu nombre: ");
        String nombreUsuario = scanner.nextLine().trim();

        File dirUsuarios = new File(Config.RUTA_COMPARTIDA + "\\usuarios");
        if (!dirUsuarios.exists()) {
            dirUsuarios.mkdirs();
        }

        // Generar siempre un nuevo UID único para este acceso
        String uid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String nombreCarpeta = nombreUsuario + "-" + uid;
        File dirJugador = new File(dirUsuarios, nombreCarpeta);
        dirJugador.mkdirs();

        // 1. Guardar archivo numero-{nombre-uid}.txt
        File fId = new File(dirJugador, "numero-" + nombreCarpeta + ".txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(fId))) {
            pw.println(nombreCarpeta);
            pw.println(nombreUsuario);
        } catch (IOException e) {
            System.err.println("Error al guardar el identificador: " + e.getMessage());
        }

        // 2. Generar y guardar un nuevo cartón 3x9 exclusivo para esta sesión
        int[][] carton = servidor.GeneradorCarton.generarCarton3x9();
        File fCarton = new File(dirJugador, "carton.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(fCarton))) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    pw.println(carton[i][j]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el cartón: " + e.getMessage());
        }
        
        System.out.println("\n[INFO] ¡Nueva sesión creada con éxito!");
        System.out.println("-> Carpeta asignada: " + nombreCarpeta);

        // Cargar el cartón recién creado
        int[][] cartonCargado = cargarCarton(fCarton);
        
        // Obtener los números que han salido para tachar los que correspondan
        Set<Integer> numerosSalidos = cargarNumerosSalidosSet();

        System.out.println("\n=== TU CARTÓN DE BINGO (3x9) ===");
        imprimirCarton(cartonCargado, numerosSalidos);

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

    private static Set<Integer> cargarNumerosSalidosSet() {
        Set<Integer> salidos = new HashSet<>();
        File fPartida = new File(Config.RUTA_COMPARTIDA, "partida.txt");
        if (!fPartida.exists()) return salidos;

        try (BufferedReader br = new BufferedReader(new FileReader(fPartida))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    salidos.add(Integer.parseInt(linea.trim()));
                }
            }
        } catch (IOException e) {
            // Ignorar errores de lectura puntuales
        }
        return salidos;
    }

    private static void imprimirCarton(int[][] carton, Set<Integer> numerosSalidos) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                if (carton[i][j] == -1) {
                    System.out.print("[  0  ]\t"); // Hueco vacío representado como 0
                } else {
                    int num = carton[i][j];
                    if (numerosSalidos.contains(num)) {
                        System.out.printf("[-%2d ]\t", num); // Si ha salido, guion por delante
                    } else {
                        System.out.printf("[%2d  ]\t", num);
                    }
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
        int numPregunta = 1; 
        File dirPregunta = new File(Config.RUTA_COMPARTIDA + "\\preguntas\\" + numPregunta);
        File fPregunta = new File(dirPregunta, "pregunta.txt");

        if (!fPregunta.exists()) {
            System.out.println("No se encontró la pregunta número " + numPregunta);
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(fPregunta));
            String enunciado = br.readLine();
            String respuestaCorrecta = br.readLine().trim().toLowerCase();
            br.close();

            System.out.println("\nPREGUNTA DE SOSTENIBILIDAD:");
            System.out.println(enunciado);

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
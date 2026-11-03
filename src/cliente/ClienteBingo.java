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

        // Buscar si ya existe una carpeta de usuario con este nombre en la red
        File dirUsuarios = new File(Config.RUTA_COMPARTIDA + "\\usuarios");
        File dirJugador = buscarCarpetaPorNombre(dirUsuarios, nombreUsuario);

        // Si no existe, lo creamos automáticamente (así hace de registro automático en el cliente)
        if (dirJugador == null) {
            String uid = "USER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            dirJugador = new File(dirUsuarios, uid);
            dirJugador.mkdirs();

            // Guardar archivo numero-{uid}.txt
            File fId = new File(dirJugador, "numero-" + uid + ".txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(fId))) {
                pw.println(uid);
                pw.println(nombreUsuario);
            } catch (IOException e) {
                System.err.println("Error al guardar el identificador: " + e.getMessage());
            }

            // Generar y guardar el cartón 3x9
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
            
            System.out.println("\n[INFO] ¡Registro completado! UID asignado: " + uid);
        } else {
            System.out.println("\n[INFO] ¡Bienvenido de nuevo, " + nombreUsuario + "!");
        }

        // Cargar el cartón del directorio encontrado
        File fCarton = new File(dirJugador, "carton.txt");
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

    // Método auxiliar para buscar si el nombre ya se registró en alguna carpeta anterior
    private static File buscarCarpetaPorNombre(File dirUsuarios, String nombreBuscado) {
        if (!dirUsuarios.exists() || !dirUsuarios.isDirectory()) {
            return null;
        }

        File[] carpetas = dirUsuarios.listFiles(File::isDirectory);
        if (carpetas == null) return null;

        for (File carpeta : carpetas) {
            // Buscar archivos que empiecen por "numero-" dentro de cada subcarpeta
            File[] ficherosId = carpeta.listFiles((dir, name) -> name.startsWith("numero-"));
            if (ficherosId != null && ficherosId.length > 0) {
                try (BufferedReader br = new BufferedReader(new FileReader(ficherosId[0]))) {
                    br.readLine(); // Saltar la primera línea (el UID)
                    String nombreEnArchivo = br.readLine(); // Segunda línea (el nombre)
                    if (nombreEnArchivo != null && nombreEnArchivo.trim().equalsIgnoreCase(nombreBuscado)) {
                        return carpeta; // ¡Encontrado! Devuelve la carpeta de este usuario
                    }
                } catch (IOException e) {
                    // Ignorar errores de lectura puntuales y seguir buscando
                }
            }
        }
        return null;
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
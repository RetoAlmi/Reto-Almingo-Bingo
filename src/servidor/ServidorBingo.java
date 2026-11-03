package servidor;

import java.io.*;
import java.util.*;

import comun.Config;

public class ServidorBingo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        File directorioBase = new File(Config.RUTA_COMPARTIDA);
        
        if (!directorioBase.exists()) {
            directorioBase.mkdirs();
        }

        System.out.println("=== SERVIDOR DEL BINGO ALMINGO ===");
        System.out.println("1. Registrar Nuevo Jugador (Genera UID Automático)");
        System.out.println("2. Iniciar Bombo de Extracción");
        System.out.print("Selecciona una opción: ");
        int opcion = scanner.nextInt();

        if (opcion == 1) {
            registrarJugador(scanner);
        } else if (opcion == 2) {
            iniciarBombo(scanner);
        }
    }

    private static void registrarJugador(Scanner scanner) {
        scanner.nextLine(); // Limpiar buffer
        System.out.print("Introduce el Nombre del usuario: ");
        String nombre = scanner.nextLine();

        // Generar un UID único basado en la fecha actual en milisegundos o un UUID corto
        String uid = "USER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        File dirUsuario = new File(Config.RUTA_COMPARTIDA + "\\usuarios\\" + uid);
        dirUsuario.mkdirs();

        // 1. Crear archivo numero-{uid}.txt con el UID y el nombre
        File fId = new File(dirUsuario, "numero-" + uid + ".txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(fId))) {
            pw.println(uid);
            pw.println(nombre);
        } catch (IOException e) {
            System.err.println("Error guardando datos de usuario: " + e.getMessage());
        }

        // 2. Crear archivo carton.txt (27 líneas)
        int[][] carton = GeneradorCarton.generarCarton3x9();
        File fCarton = new File(dirUsuario, "carton.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(fCarton))) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    pw.println(carton[i][j]);
                }
            }
            System.out.println("\n¡Jugador registrado con éxito!");
            System.out.println("-> Nombre: " + nombre);
            System.out.println("-> UID Asignado: " + uid);
            System.out.println("-> Carpeta creada en: " + dirUsuario.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error guardando el cartón: " + e.getMessage());
        }
    }

    private static void iniciarBombo(Scanner scanner) {
        List<Integer> bombo = new ArrayList<>();
        for (int i = 1; i <= 90; i++) bombo.add(i);
        Collections.shuffle(bombo);

        File fPartida = new File(Config.RUTA_COMPARTIDA, "partida.txt");

        System.out.println("\n--- COMIENZA EL JUEGO ---");
        for (int bola : bombo) {
            System.out.println("\nPulsa ENTER para sacar la siguiente bola...");
            scanner.nextLine();
            scanner.nextLine(); // Doble enter por seguridad al limpiar buffer
            
            System.out.println("¡Ha salido el número: " + bola + "!");
            
            // Escribir bola sacada en partida.txt
            try (PrintWriter pw = new PrintWriter(new FileWriter(fPartida, true))) {
                pw.println(bola);
            } catch (IOException e) {
                System.err.println("Error al actualizar partida.txt: " + e.getMessage());
            }
        }
    }
}
package servidor;

import comun.Config;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class ServidorBingo {

    public static void main(String[] args) {
        Scanner scTeclado = new Scanner(System.in);
        int opcion = 0;
        
        // Bucle do...while para que el servidor no se cierre y repita el menú
        do {
            System.out.println("\n=== SERVIDOR DEL BINGO ALMINGO ===");
            System.out.println("1. Inicializar partida.txt");
            System.out.println("2. Registrar bola sacada del bombo");
            System.out.println("3. Detectar y listar cartones de los clientes");
            System.out.println("4. Ver cuántos usuarios hay registrados");
            System.out.println("5. Vaciar (borrar) todos los usuarios");
            System.out.println("6. Salir del Servidor");
            System.out.print("Selecciona una opción: ");
            
            if (scTeclado.hasNextInt()) {
                opcion = scTeclado.nextInt();
            } else {
                System.out.println("Por favor, introduce un número válido.");
                scTeclado.next(); // Limpiar entrada incorrecta
                continue;
            }
            
            if (opcion == 1) {
                try {
                    PrintWriter pw = new PrintWriter(new File(Config.RUTA_COMPARTIDA + "\\partida.txt"));
                    pw.print("");
                    pw.close(); // Cerrar fichero según la teoría
                    System.out.println("\n[INFO] ¡Partida inicializada!");
                } catch (FileNotFoundException e) {
                    System.out.println("Error al inicializar la partida.");
                }
                
            } else if (opcion == 2) {
                System.out.print("Introduce el número de la bola que ha salido (1-90): ");
                int bola = scTeclado.nextInt();
                
                int[] salidos = new int[100];
                int totalSalidos = 0;
                
                File fPartida = new File(Config.RUTA_COMPARTIDA + "\\partida.txt");
                if (fPartida.exists()) {
                    try {
                        Scanner scLectura = new Scanner(fPartida);
                        while (scLectura.hasNext()) {
                            salidos[totalSalidos] = scLectura.nextInt();
                            totalSalidos++;
                        }
                        scLectura.close();
                    } catch (FileNotFoundException e) {
                        // Ignorar si no existe
                    }
                }
                
                salidos[totalSalidos] = bola;
                totalSalidos++;
                
                try {
                    PrintWriter pwEscritura = new PrintWriter(fPartida);
                    for (int i = 0; i < totalSalidos; i++) {
                        pwEscritura.println(salidos[i]);
                    }
                    pwEscritura.close();
                    System.out.println("\n[INFO] ¡Bola " + bola + " registrada con éxito!");
                } catch (FileNotFoundException e) {
                    System.out.println("Error al actualizar la partida.");
                }
                
            } else if (opcion == 3) {
                File dirUsuarios = new File(Config.RUTA_COMPARTIDA + "\\usuarios");
                if (!dirUsuarios.exists()) {
                    System.out.println("No hay carpeta de usuarios todavía.");
                } else {
                    File[] listaCarpetas = dirUsuarios.listFiles();
                    if (listaCarpetas != null) {
                        System.out.println("\n--- JUGADORES Y CARTONES DETECTADOS ---");
                        for (int i = 0; i < listaCarpetas.length; i++) {
                            if (listaCarpetas[i].isDirectory()) {
                                String nombreCarpeta = listaCarpetas[i].getName();
                                System.out.println("\nJugador/Sesión: " + nombreCarpeta);
                                
                                File fCarton = new File(listaCarpetas[i], "carton.txt");
                                if (fCarton.exists()) {
                                    System.out.println("Cartón detectado en la red:");
                                    try {
                                        Scanner scCarton = new Scanner(fCarton);
                                        for (int f = 0; f < 3; f++) {
                                            for (int c = 0; c < 9; c++) {
                                                if (scCarton.hasNextInt()) {
                                                    System.out.print("[" + scCarton.nextInt() + "]\t");
                                                }
                                            }
                                            System.out.println();
                                        }
                                        scCarton.close();
                                    } catch (FileNotFoundException e) {
                                        System.out.println("Error al leer el cartón.");
                                    }
                                } else {
                                    System.out.println("Este usuario aún no ha generado su cartón.");
                                }
                            }
                        }
                    }
                }
                
            } else if (opcion == 4) {
                File dirUsuarios = new File(Config.RUTA_COMPARTIDA + "\\usuarios");
                int contadorUsuarios = 0;
                
                if (dirUsuarios.exists() && dirUsuarios.isDirectory()) {
                    File[] listaCarpetas = dirUsuarios.listFiles();
                    if (listaCarpetas != null) {
                        for (int i = 0; i < listaCarpetas.length; i++) {
                            if (listaCarpetas[i].isDirectory()) {
                                contadorUsuarios++;
                            }
                        }
                    }
                }
                System.out.println("\n[INFO] Número total de usuarios conectados/registrados: " + contadorUsuarios);
                
            } else if (opcion == 5) {
                File dirUsuarios = new File(Config.RUTA_COMPARTIDA + "\\usuarios");
                
                if (dirUsuarios.exists() && dirUsuarios.isDirectory()) {
                    File[] listaCarpetas = dirUsuarios.listFiles();
                    if (listaCarpetas != null) {
                        for (int i = 0; i < listaCarpetas.length; i++) {
                            if (listaCarpetas[i].isDirectory()) {
                                File[] ficherosInternos = listaCarpetas[i].listFiles();
                                if (ficherosInternos != null) {
                                    for (int j = 0; j < ficherosInternos.length; j++) {
                                        ficherosInternos[j].delete();
                                    }
                                }
                                listaCarpetas[i].delete();
                            }
                        }
                    }
                    System.out.println("\n[INFO] ¡Se han borrado todos los usuarios y sus carpetas de la red con éxito!");
                } else {
                    System.out.println("\n[INFO] No hay carpeta de usuarios que vaciar.");
                }
                
            } else if (opcion == 6) {
                System.out.println("\nSaliendo del servidor...");
            } else {
                System.out.println("\nOpción no válida.");
            }
            
        } while (opcion != 6); // El bucle se repite mientras no se pulse la opción 6 para salir
        
        scTeclado.close();
    }
}
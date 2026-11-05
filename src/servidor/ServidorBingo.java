package servidor;

import java.io.File;
import java.io.FileOutputStream; // Necesario para añadir al archivo sin sobrescribir
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class ServidorBingo {
	public static final String RUTA_COMPARTIDA = "\\\\DESKTOP-V7J205P\\Reto-Almingo-Bingo";
	public static final String RUTA_USUARIOS = RUTA_COMPARTIDA + "\\usuarios";
	public static final String RUTA_PARTIDA = RUTA_COMPARTIDA + "\\partida.txt";
	public static final String RUTA_TOTAL_BOLAS = RUTA_COMPARTIDA + "\\totalBolas.txt";

	public static void main(String[] args) {
		Scanner scTeclado = new Scanner(System.in);
		int opcion = 0;
		Random rnd = new Random();

		// Bucle do...while para que el servidor no se cierre y repita el menú
		do {
			System.out.println("\n=== SERVIDOR DEL BINGO ALMINGO ===");
			System.out.println("1. Inicializar partida");
			System.out.println("2. Sacar bola del bombo");
			System.out.println("3. Detectar y listar cartones de los clientes");
			System.out.println("4. Ver cuántos usuarios hay registrados");
			System.out.println("5. Eliminar todos los usuarios");
			System.out.println("6. Eliminar datos de la partida");
			System.out.println("7. Eliminar todos los datos");
			System.out.println("0. Salir del Servidor");
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
					PrintWriter pwPartida = new PrintWriter(new File(RUTA_PARTIDA));
					pwPartida.print("");
					pwPartida.close();
					PrintWriter pwTotalBolas = new PrintWriter(new File(RUTA_TOTAL_BOLAS));
					pwTotalBolas.print("");
					pwTotalBolas.close();
					System.out.println("\n[INFORMACION] ¡Partida inicializada!");
				} catch (FileNotFoundException e) {
					System.out.println("Error al inicializar la partida.");
				}

			}

			if (opcion == 2) {
				int bolaNueva;
				boolean encontrado;
				int totalSalidos = 0;
				int totalRestantes = 99;

				// 1. Generar número aleatorio y comprobar que no esté en partida.txt
				do {
					bolaNueva = rnd.nextInt(1, 100);
					encontrado = false;

					try (Scanner lector = new Scanner(new File(RUTA_PARTIDA))) {
						while (lector.hasNextInt()) {
							int numeroArchivo = lector.nextInt();
							if (numeroArchivo == bolaNueva) {
								encontrado = true;
								break;
							}
						}
					} catch (FileNotFoundException e) {
						// Si el archivo no existe todavía, asumimos que no está
					}

				} while (encontrado); // Repite si el número fue encontrado en el archivo

				int bola = bolaNueva; // Asignamos el número válido definitivo

				// 2. Leer cuántas bolas han salido y cuántas quedan en totalBolas.txt
				File fTotalBolas = new File(RUTA_TOTAL_BOLAS);
				if (fTotalBolas.exists()) {
					try (Scanner scLectura = new Scanner(fTotalBolas)) {
						if (scLectura.hasNextInt()) {
							totalSalidos = scLectura.nextInt();
						}
						if (scLectura.hasNextInt()) {
							totalRestantes = scLectura.nextInt();
						}
					} catch (FileNotFoundException e) {
						// Ignorar si ocurre algún error de lectura
					}
				}

				totalSalidos++;
				totalRestantes--;

				// 3. Guardar el nuevo número en partida.txt (añadiéndolo abajo sin borrar lo
				// anterior)
				File fPartida = new File(RUTA_PARTIDA);
				try {
					// Usamos FileOutputStream con 'true' para hacer append (añadir en la siguiente
					// línea)
					PrintWriter pwEscritura = new PrintWriter(new FileOutputStream(fPartida, true));
					pwEscritura.println(bola);
					pwEscritura.close();

					// Mostramos el mensaje tal y como pediste
					System.out.println("\n[INFORMACION] El número " + bola
							+ " no estaba en el archivo y ha sido registrado con éxito.");
				} catch (FileNotFoundException e) {
					System.out.println("Error al actualizar la partida.");
				}

				// 4. Actualizar los contadores en totalBolas.txt
				try {
					PrintWriter pwTotalBolas = new PrintWriter(fTotalBolas);
					pwTotalBolas.println(totalSalidos);
					pwTotalBolas.println(totalRestantes);
					pwTotalBolas.close();

					System.out.println("Bolas sacadas: " + totalSalidos);
					System.out.println("Bolas restantes: " + totalRestantes);
				} catch (FileNotFoundException e) {
					System.out.println("Error al actualizar totalBolas.txt.");
				}

			}

			if (opcion == 3) {
				File dirUsuarios = new File(RUTA_USUARIOS);
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

			}

			if (opcion == 4) {
				File dirUsuarios = new File(RUTA_USUARIOS);
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
				System.out.println(
						"\n[INFORMACION] Número total de usuarios conectados/registrados: " + contadorUsuarios);

			}
			if (opcion == 5) {
				eliminarUsuarios();

			} else {
				System.out.println("\n[INFORMACION] No hay carpeta de usuarios que vaciar.");
			}

			if (opcion == 6) {
				eliminarPartida();
				eliminarTotalBolas();

			} else {
				System.out.println("\n[INFORMACION] No hay carpeta de usuarios que vaciar.");
			}
			if (opcion == 7) {
				eliminarUsuarios();
				eliminarPartida();
				eliminarTotalBolas();

			}

			if (opcion == 0) {
				System.out.println("\nSaliendo del servidor...");
			} else {
				System.out.println("\nOpción no válida.");
			}

		} while (opcion != 0);

		scTeclado.close();
	}

	public static void eliminarUsuarios() {
		File dirUsuarios = new File(RUTA_USUARIOS);

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
			System.out
					.println("\n[INFORMACION] ¡Se han borrado todos los usuarios y sus carpetas de la red con éxito!");
		} else {
			System.out.println("\n[INFORMACION] No hay carpeta de usuarios que vaciar.");
		}
	}

	public static void eliminarPartida() {
		File partidaArchivo = new File(RUTA_PARTIDA);

		if (partidaArchivo.exists()) {
			partidaArchivo.delete();
			System.out.println("\n[INFORMACION] ¡Se ha borrado el archivo 'partida.txt' de la red con éxito!");

		} else {
			System.out.println("\n[INFORMACION] No hay carpeta de usuarios que vaciar.");
		}
	}

	public static void eliminarTotalBolas() {
		File totalBolasArchivo = new File(RUTA_TOTAL_BOLAS);

		if (totalBolasArchivo.exists()) {
			totalBolasArchivo.delete();
			System.out.println("\n[INFORMACION] ¡Se ha borrado el archivo 'totalBolas.txt' de la red con éxito!");

		} else {
			System.out.println("\n[INFORMACION] No hay carpeta de usuarios que vaciar.");
		}
	}
}
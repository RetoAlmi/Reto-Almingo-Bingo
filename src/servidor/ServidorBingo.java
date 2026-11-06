package servidor;

import java.io.File;
import java.io.FileOutputStream; // Necesario para aniadir al archivo sin sobrescribir
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class ServidorBingo {
	public static final String RUTA_COMPARTIDA = "\\\\DESKTOP-V7J205P\\Reto-Almingo-Bingo";
	public static final String RUTA_USUARIOS = RUTA_COMPARTIDA + "\\usuarios";
	public static final String RUTA_PARTIDA = RUTA_COMPARTIDA + "\\partida.txt";
	public static final String RUTA_TOTAL_BOLAS = RUTA_COMPARTIDA + "\\totalBolas.txt";
	public static final String RUTA_ESTADO_PARTIDA = RUTA_COMPARTIDA + "\\estadoPartida.txt";

	public static void main(String[] args) {
		Scanner scTeclado = new Scanner(System.in);
		int opcion = 0;
		Random rnd = new Random();

		// Bucle do...while para que el servidor no se cierre y repita el menu
		do {
			System.out.println("\n=== SERVIDOR DEL BINGO ALMINGO ===");
			System.out.println("1. Inicializar partida");
			System.out.println("2. Sacar bola del bombo");
			System.out.println("3. Detectar y listar cartones de los clientes");
			System.out.println("4. Ver cuantos usuarios hay registrados");
			System.out.println("5. Eliminar todos los usuarios");
			System.out.println("6. Eliminar datos de la partida");
			System.out.println("7. Eliminar todos los datos");
			System.out.println("8. Comprobar estado de la partida");
			System.out.println("-> Para comprobar si hay algun ganador.");
			System.out.println("-> En el caso de que haya un ganador, se finalizara la partida.");
			System.out.println("9. Sacar todas las bolas");
			System.out.println("0. Salir del Servidor");
			System.out.print("Selecciona una opcion: ");

			if (scTeclado.hasNextInt()) {
				opcion = scTeclado.nextInt();
			} else {
				System.out.println("Por favor, introduce un numero valido.");
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
					System.out.println("\n[INFO] Partida inicializada!");
				} catch (FileNotFoundException e) {
					System.out.println("Error al inicializar la partida.");
				}

			}

			else if (opcion == 2) {
				for (int bolise = 0; bolise < 90; bolise++) {
				int bolaNueva;
				boolean encontrado;
				int totalSalidos = 0;
				int totalRestantes = 90;

				// 1. Generar numero aleatorio y comprobar que no este en partida.txt
				do {
					bolaNueva = rnd.nextInt(1, 91);
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
						// Si el archivo no existe todavia, asumimos que no esta
					}

				} while (encontrado); // Repite si el numero fue encontrado en el archivo

				int bola = bolaNueva; // Asignamos el numero valido definitivo

				// 2. Leer cuantas bolas han salido y cuantas quedan en totalBolas.txt
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
						// Ignorar si ocurre algun error de lectura
					}
				}

				totalSalidos++;
				totalRestantes--;

				// 3. Guardar el nuevo numero en partida.txt (aniadiendolo en una nueva linea)
				File fPartida = new File(RUTA_PARTIDA);
				try {
					// Usamos FileOutputStream con 'true' para hacer append (aniadir en la siguiente
					// linea)
					PrintWriter pwEscritura = new PrintWriter(new FileOutputStream(fPartida, true));
					pwEscritura.println(bola);
					pwEscritura.close();

					// Mostramos el mensaje
					System.out.println(
							"\n[INFO] El numero " + bola + " no estaba en el archivo y ha sido registrado con exito.");
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

			}

			else if (opcion == 3) {
				File dirUsuarios = new File(RUTA_USUARIOS);
				if (!dirUsuarios.exists()) {
					System.out.println("No hay carpeta de usuarios todavia.");
				} else {
					File[] listaCarpetas = dirUsuarios.listFiles();
					if (listaCarpetas != null) {
						System.out.println("\n--- JUGADORES Y CARTONES DETECTADOS ---");
						for (int i = 0; i < listaCarpetas.length; i++) {
							if (listaCarpetas[i].isDirectory()) {
								String nombreCarpeta = listaCarpetas[i].getName();
								System.out.println("\nJugador/Sesion: " + nombreCarpeta);

								File fCarton = new File(listaCarpetas[i], "carton.txt");
								if (fCarton.exists()) {
									System.out.println("Carton detectado en la red:");
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
										System.out.println("Error al leer el carton.");
									}
								} else {
									System.out.println("Este usuario aun no ha generado su carton.");
								}
							}
						}
					}
				}

			}

			else if (opcion == 4) {
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
				System.out.println("\n[INFO] Numero total de usuarios conectados/registrados: " + contadorUsuarios);

			} else if (opcion == 5) {
				eliminarUsuarios();

			}

			else if (opcion == 6) {
				eliminarPartida();
				eliminarTotalBolas();
			}

			else if (opcion == 7) {
				eliminarUsuarios();
				eliminarPartida();
				eliminarTotalBolas();
			}
			
			else if (opcion == 8) {
				comprobarEstadoPartida();
			}
			
			else if (opcion == 9) {
				int bolaNueva;
				boolean encontrado;
				int totalSalidos = 0;
				int totalRestantes = 90;

				// 1. Generar numero aleatorio y comprobar que no este en partida.txt
				do {
					bolaNueva = rnd.nextInt(1, 91);
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
						// Si el archivo no existe todavia, asumimos que no esta
					}

				} while (encontrado); // Repite si el numero fue encontrado en el archivo

				int bola = bolaNueva; // Asignamos el numero valido definitivo

				// 2. Leer cuantas bolas han salido y cuantas quedan en totalBolas.txt
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
						// Ignorar si ocurre algun error de lectura
					}
				}

				totalSalidos++;
				totalRestantes--;

				// 3. Guardar el nuevo numero en partida.txt (aniadiendolo en una nueva linea)
				File fPartida = new File(RUTA_PARTIDA);
				try {
					// Usamos FileOutputStream con 'true' para hacer append (aniadir en la siguiente
					// linea)
					PrintWriter pwEscritura = new PrintWriter(new FileOutputStream(fPartida, true));
					pwEscritura.println(bola);
					pwEscritura.close();

					// Mostramos el mensaje
					System.out.println(
							"\n[INFO] El numero " + bola + " no estaba en el archivo y ha sido registrado con exito.");
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

			else if (opcion == 0) {
				System.out.println("\nSaliendo del servidor...");
			}

			else {
				System.out.println("\nOpcion no valida.");
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
			System.out.println("\n[INFO] Se han borrado todos los usuarios y sus carpetas de la red con exito!");
		} else {
			System.out.println("\n[INFO] No hay carpeta de usuarios que vaciar.");
		}
	}

	public static void eliminarPartida() {
		File partidaArchivo = new File(RUTA_PARTIDA);

		if (partidaArchivo.exists()) {
			partidaArchivo.delete();
			System.out.println("\n[INFO] Se ha borrado el archivo 'partida.txt' de la red con exito!");

		} else {
			System.out.println("\n[INFO] No hay carpeta de usuarios que vaciar.");
		}
	}

	public static void eliminarTotalBolas() {
		File totalBolasArchivo = new File(RUTA_TOTAL_BOLAS);

		if (totalBolasArchivo.exists()) {
			totalBolasArchivo.delete();
			System.out.println("\n[INFO] Se ha borrado el archivo 'totalBolas.txt' de la red con exito!");

		} else {
			System.out.println("\n[INFO] No hay carpeta de usuarios que vaciar.");
		}
	}
	
	public static void comprobarEstadoPartida() {
		File dirUsuarios = new File(RUTA_USUARIOS);
		if (!dirUsuarios.exists() || !dirUsuarios.isDirectory()) {
			System.out.println("\n[INFO] No hay carpeta de usuarios para comprobar.");
			return;
		}

		File[] listaCarpetas = dirUsuarios.listFiles();
		if (listaCarpetas == null) {
			System.out.println("\n[INFO] No hay carpetas de usuarios registradas.");
			return;
		}

		boolean hayGanador = false;

		// Recorrer todas las subcarpetas de los usuarios
		for (File carpetaUsuario : listaCarpetas) {
			if (carpetaUsuario.isDirectory()) {
				// Buscamos el archivo de resultado/comprobacion del cliente
				// (Puedes cambiar "resultado.txt" por el nombre exacto que genere tu cliente, ej: "comprobado.txt")
				File fResultado = new File(carpetaUsuario, "resultado.txt");
				
				if (fResultado.exists()) {
					try (Scanner scResultado = new Scanner(fResultado)) {
						if (scResultado.hasNextLine()) {
							String primeraLinea = scResultado.nextLine();
							// Comprobar si la primera linea contiene "Victoria"
							if (primeraLinea.contains("Victoria")) {
								hayGanador = true;
								
								// Leer todo el contenido del archivo resultado.txt
								StringBuilder contenidoResultado = new StringBuilder();
								contenidoResultado.append("=== GANADOR EN: ").append(carpetaUsuario.getName()).append(" ===\n");
								contenidoResultado.append(primeraLinea).append("\n");
								while (scResultado.hasNextLine()) {
									contenidoResultado.append(scResultado.nextLine()).append("\n");
								}
								contenidoResultado.append("=========================================\n\n");

								// Escribir/añadir todo el contenido a RUTA_ESTADO_PARTIDA
								File fEstado = new File(RUTA_ESTADO_PARTIDA);
								try (PrintWriter pwEstado = new PrintWriter(new FileOutputStream(fEstado, true))) {
									pwEstado.print(contenidoResultado.toString());
								}

								System.out.println("\n[VICTORIA DETECTADA!] El usuario de la carpeta '" + carpetaUsuario.getName() + "' ha ganado.");
							}
						}
					} catch (FileNotFoundException e) {
						System.out.println("[ERROR] No se pudo leer el archivo de resultado del usuario: " + carpetaUsuario.getName());
					}
				}
			}
		}

		if (hayGanador) {
			// Finalizar la partida vaciando o eliminando partida.txt
			eliminarPartida();
			System.out.println("[INFO] Se ha finalizado la partida actual debido a que hay un ganador. Datos guardados en estadoPartida.txt.");
		} else {
			System.out.println("\n[INFO] Comprobacion finalizada. Ningun usuario ha conseguido la victoria todavia.");
		}
	}
}
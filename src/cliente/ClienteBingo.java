package cliente;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Random;

public class ClienteBingo {
	public static final String RUTA_COMPARTIDA = "\\\\DESKTOP-V7J205P\\Reto-Almingo-Bingo";
	public static final int PREGUNTAS_CORRECTAS = 0;
	public static final int PREGUNTAS_FALLIDAS = 0;

	public static void main(String[] args) {
		Scanner scTeclado = new Scanner(System.in);
		Scanner scNumero = new Scanner(System.in);
		int numeroIntroducido;
		int maxCaracteresNombre = 13;
		int minCaracteresNombre = 3;

		System.out.println("=== CLIENTE DEL BINGO ALMINGO ===");
		System.out.print("Introduce tu nombre: ");
		String nombre = scTeclado.next();
		while (nombre.length() > maxCaracteresNombre && nombre.length() < minCaracteresNombre) {
			if (nombre.length() < minCaracteresNombre) {
				System.out.println("Tu nombre tiene que tener mas de 3 caracteres.");
				System.out.print("Introduce tu nombre: ");
				nombre = scTeclado.next();
			}
			if (nombre.length() > maxCaracteresNombre) {
				System.out.println("Tu nombre tiene que tener menos de 13 caracteres.");
				System.out.print("Introduce tu nombre: ");
				nombre = scTeclado.next();
			}
		}

		// Generar un UID único para la sesión del usuario
		int uid = generarUid();
		String nombreCarpeta = nombre + "-" + uid;
		String rutaCarpeta = RUTA_COMPARTIDA + "\\usuarios\\" + nombreCarpeta;
		System.out.println("Tu UID: " + uid);
		System.out.println("Carpeta: " + rutaCarpeta);

		// Ruta de la carpeta de este usuario en la red compartida
		File dirJugador = new File(rutaCarpeta);
		dirJugador.mkdirs();

		// 1. Guardar el archivo de identificación uid.txt
		try {
			PrintWriter pwId = new PrintWriter(new File(dirJugador, "uid" + ".txt"));
			pwId.println(uid);
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

			// Crear archivo para guardar las preguntas hechas
			PrintWriter pwPreguntas = new PrintWriter(new File(dirJugador, "preguntasHechas.txt"));
			pwPreguntas.close();

			System.out.println("\n[INFORMACION] ¡Cartón generado y guardado en la red con éxito!");
			System.out.println("-> Carpeta asignada: " + nombreCarpeta);
		} catch (FileNotFoundException e) {
			System.out.println("Error al guardar el cartón.");
		}

		int opcion = 0;
		do {
			// Imprimir el cartón 3x9 por pantalla aplicando el formato de 0 y guiones (-)
			System.out.println("\n=== TU CARTÓN DE BINGO ===");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 9; j++) {
					int valor = carton[i][j];
					System.out.print("[ " + valor + " ]\t");
				}
				System.out.println();
			}

			System.out.println("\n=== MENU ===");
			System.out.println("1. Comprobar numero");
			System.out.println("2. Comprobar linea");
			System.out.println("3. Comprobar bingo");
			System.out.println("0. Salir");
			System.out.print("Selecciona una opción: ");

			if (scTeclado.hasNextInt()) {
				opcion = scTeclado.nextInt();
			} else {
				System.out.println("Por favor, introduce un número válido.");
				scTeclado.next(); // Limpiar entrada incorrecta
				continue;
			}

			if (opcion == 1) {
				// Leer los números que han salido en partida.txt para comprobar aciertos
				System.out.print("Introduce el numero para comprobar: ");
				int numero = scNumero.nextInt();
				comprobarNumero(numero, rutaCarpeta);
			}

			if (opcion == 2) {
				// Comprobar linea
				boolean linea = false;
				linea = comprobarLinea(rutaCarpeta);
			}

			if (opcion == 3) {
				// Comprobar bingo
				boolean bingo = false;
				bingo = comprobarBingo(rutaCarpeta);
			}

			if (opcion == 0) {
				System.out.println("\nSaliendo de la partida...");
			} else {
				System.out.println("\nOpción no válida.");
			}
		} while (opcion != 0);

		scTeclado.close();
		scNumero.close();
	}

	private static boolean comprobarLinea(String rutaCarpeta) {
		boolean linea = false;
		String contiene;

		try {
			Scanner scPartida = new Scanner(new File(RUTA_COMPARTIDA + "\\partida.txt"));
			Scanner scCarton = new Scanner(new File(rutaCarpeta + "\\carton.txt"));
			while (scCarton.hasNextLine()) { // Lectura secuencial con hasNextLine
				contiene = scCarton.nextLine();
				if (contiene.startsWith("-") && PREGUNTAS_CORRECTAS >= 3) {
					linea = true;
				}
				if (!contiene.startsWith("-")) {
					linea = false;
				}
			}

			if (linea) {
				// Si hay bingo
			} else if (!linea) {
				// Si no hay bingo
				System.out.println("[INFORMACION] No puedes hacer linea, no esta completa");
			} else {
				System.out.println("[ERROR] Error desconocido");
			}

			scPartida.close();
			scCarton.close();
		} catch (FileNotFoundException e) {

		}

		return linea;
	}

	private static boolean comprobarBingo(String rutaCarpeta) {
		boolean bingo = false;
		String contiene;

		try {
			Scanner scPartida = new Scanner(new File(RUTA_COMPARTIDA + "\\partida.txt"));
			Scanner scCarton = new Scanner(new File(rutaCarpeta + "\\carton.txt"));
			while (scCarton.hasNextLine()) { // Lectura secuencial con hasNextLine
				contiene = scCarton.nextLine();
				if (contiene.startsWith("-") && PREGUNTAS_CORRECTAS >= 3) {
					bingo = true;
				}
				if (!contiene.startsWith("-")) {
					bingo = false;
				}
			}

			if (bingo) {
				// Si hay bingo
			} else if (!bingo) {
				// Si no hay bingo
				System.out.println("[INFORMACION] No puedes hacer bingo, no esta completo");
			} else {
				System.out.println("[ERROR] Error desconocido");
			}

			scPartida.close();
			scCarton.close();
		} catch (FileNotFoundException e) {

		}

		return bingo;
	}

	private static void comprobarNumero(int numero, String rutaCarpeta) {
		boolean esta = false;
		int numeroLinea = 0;

		try {
			Scanner scPartida = new Scanner(new File(RUTA_COMPARTIDA + "\\partida.txt"));
			Scanner scCarton = new Scanner(new File(rutaCarpeta + "\\carton.txt"));

			PrintWriter pwCarton = new PrintWriter(new File(rutaCarpeta, "\\carton.txt"));

			while (scPartida.hasNextInt()) { // Lectura secuencial con hasNext
				numero = scPartida.nextInt();
				esta = true;
				numeroLinea++;
			}

			if (esta) {
				numero = numero - (2 * numero);
			}

			pwCarton.println(numeroLinea);

			scPartida.close();
			scCarton.close();
		} catch (FileNotFoundException e) {

		}
	}

	private static int generarUid() {
		int uid;
		Random rnd = new Random();

		uid = rnd.nextInt(100, 99999);

		return uid;
	}

	// Método auxiliar para generar el cartón con números totalmente aleatorios (del 1 al 90) sin repetir
	private static int[][] generarCartonCliente() {
		int[][] carton = new int[3][9];
		Random rnd = new Random();

		// Inicializar el cartón a 0 (12 huecos vacíos en total, 4 por fila)
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				carton[i][j] = 0;
			}
		}

		// Rellenar cada fila para que tenga exactamente 5 números
		for (int i = 0; i < 3; i++) {
			int numerosColocados = 0;
			while (numerosColocados < 5) {
				int j = rnd.nextInt(9); // Columna aleatoria (0 al 8) donde se colocará el número

				if (carton[i][j] == 0) {
					// Generar un número completamente aleatorio entre 1 y 90
					int num = rnd.nextInt(1, 91);

					// Comprobar que el número NO se repita en todo el cartón
					boolean repetidoEnCarton = false;
					for (int f = 0; f < 3; f++) {
						for (int c = 0; c < 9; c++) {
							if (carton[f][c] == num) {
								repetidoEnCarton = true;
								break;
							}
						}
						if (repetidoEnCarton)
							break;
					}

					// Si no está repetido, se asigna a esa posición
					if (!repetidoEnCarton) {
						carton[i][j] = num;
						numerosColocados++;
					}
				}
			}
		}
		return carton;
	}
}
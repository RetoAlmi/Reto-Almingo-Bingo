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

		// Generar un UID unico para la sesion del usuario
		int uid = generarUid();
		String nombreCarpeta = nombre + "-" + uid;
		String rutaCarpeta = RUTA_COMPARTIDA + "\\usuarios\\" + nombreCarpeta;
		System.out.println("[INFO] Tu UID: " + uid);
		System.out.println("[INFO] Carpeta: " + rutaCarpeta);

		// Ruta de la carpeta de este usuario en la red compartida
		File dirJugador = new File(rutaCarpeta);
		dirJugador.mkdirs();

		// 1. Guardar el archivo de identificacion uid.txt
		try {
			PrintWriter pwId = new PrintWriter(new File(dirJugador, "uid" + ".txt"));
			pwId.println(uid);
			pwId.println(nombre);
			pwId.close(); // Cerrar fichero segun la teoria
		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] Error al guardar el identificador.");
		}

		// 2. El cliente genera su propio carton 3x9 (5 numeros por fila, 12 ceros)
		int[][] carton = generarCartonCliente();

		// 3. Guardar el carton en carton.txt para que el servidor lo detecte
		try {
			PrintWriter pwCarton = new PrintWriter(new File(dirJugador, "carton.txt"));
			for (int fila = 0; fila < 3; fila++) {
				for (int columna = 0; columna < 9; columna++) {
					pwCarton.println(carton[fila][columna]);
				}
			}
			pwCarton.close(); // Cerrar fichero

			// Crear archivo para guardar las preguntas hechas
			PrintWriter pwPreguntas = new PrintWriter(new File(dirJugador, "preguntasHechas.txt"));
			pwPreguntas.close();

			System.out.println("\n[LOG] Carton generado y guardado en la red con exito!");
			System.out.println("-> Carpeta asignada: " + nombreCarpeta);
		} catch (FileNotFoundException e) {
			System.out.println("Error al guardar el carton.");
		}

		int opcion = 0;
		do {
			// Recargar el carton desde el archivo para reflejar los numeros tachados con '-'
			// (Usamos una matriz de String para poder mostrar los numeros con el guion)
			String[][] cartonVisual = new String[3][9];
			try (Scanner scRecarga = new Scanner(new File(rutaCarpeta + "\\carton.txt"))) {
				for (int fila = 0; fila < 3; fila++) {
					for (int columna = 0; columna < 9; columna++) {
						if (scRecarga.hasNextLine()) {
							cartonVisual[fila][columna] = scRecarga.nextLine();
						}
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("[ERROR] No se pudo leer el carton para mostrarlo.");
			}

			// Imprimir el carton 3x9 por pantalla actualizado
			System.out.println("\n=== TU CARTON DE BINGO ===");
			for (int fila = 0; fila < 3; fila++) {
				for (int columna = 0; columna < 9; columna++) {
					String valor = cartonVisual[fila][columna];
					System.out.print("[ " + valor + " ]\t");
				}
				System.out.println();
			}
			
			// Mostrar los numeros que han salido hasta ahora en partida.txt
			System.out.print("\nNumeros que ya han salido: ");
			try (Scanner scBolasSalidas = new Scanner(new File(RUTA_COMPARTIDA + "\\partida.txt"))) {
				boolean primero = true;
				while (scBolasSalidas.hasNext()) {
					if (!primero) {
						System.out.print(", ");
					}
					System.out.print(scBolasSalidas.next());
					primero = false;
				}
				if (primero) {
					System.out.print("Ninguno todavia");
				}
				System.out.println();
			} catch (FileNotFoundException e) {
				System.out.println("Ninguno todavia");
			}

			System.out.println("\n=== MENU ===");
			System.out.println("1. Comprobar ultimo numero");
			System.out.println("2. Comprobar numero");
			System.out.println("3. Comprobar linea");
			System.out.println("4. Comprobar bingo");
			System.out.println("0. Salir");
			System.out.print("\nSelecciona una opcion: ");

			if (scTeclado.hasNextInt()) {
				opcion = scTeclado.nextInt();
			} else {
				System.out.println("[INFO] Por favor, introduce un numero valido.");
				scTeclado.next(); // Limpiar entrada incorrecta
				continue;
			}
			
			if (opcion == 1) {
				System.out.println("[LOG] Comprobando numeros...");
			}

			else if (opcion == 2) {
				// Leer los numeros que han salido en partida.txt para comprobar aciertos
				System.out.print("[NUMERO] Introduce el numero para comprobar: ");
				int numero = scNumero.nextInt();
				comprobarNumero(numero, rutaCarpeta);
			}

			else if (opcion == 3) {
				// Comprobar linea
				boolean linea = false;
				linea = comprobarLinea(rutaCarpeta);
			}

			else if (opcion == 4) {
				// Comprobar bingo
				boolean bingo = false;
				bingo = comprobarBingo(rutaCarpeta);
			}

			else if (opcion == 0) {
				System.out.println("\n[LOG] Saliendo de la partida...");
			}
			
			else {
				System.out.println("\n[ALERTA] Opcion no valida.");
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
				System.out.println("[INFO] No puedes hacer linea, no esta completa");
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
				System.out.println("[INFO] No puedes hacer bingo, no esta completo");
			} else {
				System.out.println("[ERROR] Error desconocido");
			}

			scPartida.close();
			scCarton.close();
		} catch (FileNotFoundException e) {

		}

		return bingo;
	}

	private static void comprobarNumero(int numeroAComprobar, String rutaCarpeta) {
		boolean estaEnPartida = false;

		// 1. Comprobar si el numero introducido esta en partida.txt
		try (Scanner scPartida = new Scanner(new File(RUTA_COMPARTIDA + "\\partida.txt"))) {
			while (scPartida.hasNextInt()) {
				int bolaPartida = scPartida.nextInt();
				if (bolaPartida == numeroAComprobar) {
					estaEnPartida = true;
					break;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] No se pudo encontrar el archivo partida.txt");
			return;
		}

		if (!estaEnPartida) {
			System.out.println("\n[INFO] El numero " + numeroAComprobar + " NO ha salido todavia en la partida.");
			return;
		}

		// 2. Si esta en partida, leemos el carton actual (guardado en formato de 27 lineas o matriz)
		// Como carton.txt guarda los 27 elementos secuencialmente (3 filas x 9 columnas), los cargamos:
		String[] lineasCarton = new String[27];
		boolean encontradoEnCarton = false;
		int indice = 0;

		try (Scanner scCartonLectura = new Scanner(new File(rutaCarpeta + "\\carton.txt"))) {
			while (scCartonLectura.hasNextLine() && indice < 27) {
				lineasCarton[indice] = scCartonLectura.nextLine();
				indice++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] No se pudo leer el carton del usuario.");
			return;
		}

		// 3. Buscar el numero en el carton y anteponer el "-" si coincide
		for (int fila = 0; fila < 27; fila++) {
			// Comparamos si la linea coincide con el numero (ignorando si ya tiene un guion previo)
			if (lineasCarton[fila] != null && !lineasCarton[fila].startsWith("-")) {
				try {
					int val = Integer.parseInt(lineasCarton[fila]);
					if (val == numeroAComprobar) {
						lineasCarton[fila] = "-" + val; // Se le agrega el "-" por delante
						encontradoEnCarton = true;
					}
				} catch (NumberFormatException e) {
					// Si ya contiene un guion u otro caracter, se ignora
				}
			}
		}

		if (!encontradoEnCarton) {
			System.out.println("\n[INFO] El numero " + numeroAComprobar + " ha salido en la partida, pero NO esta en tu carton!");
			return;
		}

		// 4. Reescribir el archivo carton.txt con el numero tachado (-)
		try (PrintWriter pwCartonEscritura = new PrintWriter(new File(rutaCarpeta + "\\carton.txt"))) {
			for (int fila = 0; fila < 27; fila++) {
				if (lineasCarton[fila] != null) {
					pwCartonEscritura.println(lineasCarton[fila]);
				}
			}
			System.out.println(
					"\n[CORRECTO] El numero " + numeroAComprobar + " estaba en tu carton y ha sido marcado con '-'.");
		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] No se pudo actualizar el archivo carton.txt");
		}
	}

	private static int generarUid() {
		int uid;
		Random rnd = new Random();

		uid = rnd.nextInt(100, 99999);

		return uid;
	}

	// Metodo auxiliar para generar el carton con numeros totalmente aleatorios (del 1 al 90) sin repetir
	private static int[][] generarCartonCliente() {
		int[][] carton = new int[3][9];
		Random rnd = new Random();

		// Inicializar el carton a 0 (12 huecos vacios en total, 4 por fila)
		for (int fila = 0; fila < 3; fila++) {
			for (int columna = 0; columna < 9; columna++) {
				carton[fila][columna] = 0;
			}
		}

		// Rellenar cada fila para que tenga exactamente 5 numeros
		for (int fila = 0; fila < 3; fila++) {
			int numerosColocados = 0;
			while (numerosColocados < 5) {
				int columna = rnd.nextInt(9); // Columna aleatoria (0 al 8) donde se colocara el numero

				if (carton[fila][columna] == 0) {
					// Generar un numero completamente aleatorio entre 1 y 90
					int num = rnd.nextInt(1, 91);

					// Comprobar que el numero NO se repita en todo el carton
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

					// Si no este repetido, se asigna a esa posicion
					if (!repetidoEnCarton) {
						carton[fila][columna] = num;
						numerosColocados++;
					}
				}
			}
		}
		return carton;
	}
}
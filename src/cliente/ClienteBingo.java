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

			// Crear archivo para guardar el resultado final
			PrintWriter pwResultado = new PrintWriter(new File(dirJugador, "resultado.txt"));
			pwResultado.close();

			System.out.println("\n[LOG] Carton generado y guardado en la red con exito!");
			System.out.println("-> Carpeta asignada: " + nombreCarpeta);
		} catch (FileNotFoundException e) {
			System.out.println("Error al guardar el carton.");
		}

		int opcion = 0;
		do {
			// Recargar el carton desde el archivo para reflejar los numeros tachados con
			// '-'
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
					System.out.print("Ninguno todavia.");
				}
				System.out.println();
			} catch (FileNotFoundException e) {
				System.out.println("Ninguno todavia.");
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
				if (bingo) {
					opcion = 0;
				}
			}
			
			else if (opcion == 10) {
				// Opcion oculta para comprobar todos los numeros que han salido en partida.txt
				System.out.println("[ADMIN] Comprobancion automatica de todos los numeros.");
				comprobarTodosLosNumeros(rutaCarpeta);
				System.out.println("[ADMIN] Todos los numeros comprobados.");
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

	private static void comprobarTodosLosNumeros(String rutaCarpeta) {
		try {
			// 1. Leer todas las bolas que han salido en partida.txt
			Scanner scPartida = new Scanner(new File(RUTA_COMPARTIDA + "\\partida.txt"));
			java.util.ArrayList<Integer> bolasSalidas = new java.util.ArrayList<>();
			while (scPartida.hasNextInt()) {
				bolasSalidas.add(scPartida.nextInt());
			}
			scPartida.close();

			if (bolasSalidas.isEmpty()) {
				System.out.println("\n[INFO] Todavia no ha salido ningun numero en la partida.");
				return;
			}

			// 2. Leer las 27 lineas del carton actual
			String[] lineasCarton = new String[27];
			int indice = 0;
			Scanner scCartonLectura = new Scanner(new File(rutaCarpeta + "\\carton.txt"));
			while (scCartonLectura.hasNextLine() && indice < 27) {
				lineasCarton[indice] = scCartonLectura.nextLine();
				indice++;
			}
			scCartonLectura.close();

			// 3. Cruzar los datos: revisar cada numero que salio y ver si esta en el carton
			boolean huboAciertos = false;
			for (int bola : bolasSalidas) {
				for (int fila = 0; fila < 27; fila++) {
					if (lineasCarton[fila] != null && !lineasCarton[fila].startsWith("-")) {
						try {
							int val = Integer.parseInt(lineasCarton[fila]);
							if (val == bola) {
								lineasCarton[fila] = "-" + val; // Tachar con '-'
								huboAciertos = true;
							}
						} catch (NumberFormatException e) {
							// Ignorar si no es un numero parseable
						}
					}
				}
			}

			// 4. Guardar los cambios actualizados en carton.txt si hubo modificaciones
			if (huboAciertos) {
				PrintWriter pwCartonEscritura = new PrintWriter(new File(rutaCarpeta + "\\carton.txt"));
				for (int fila = 0; fila < 27; fila++) {
					if (lineasCarton[fila] != null) {
						pwCartonEscritura.println(lineasCarton[fila]);
					}
				}
				pwCartonEscritura.close();
				System.out.println("\n[CORRECTO] Se han comprobado todos los numeros de la partida y se han tachado los aciertos en tu carton.");
			} else {
				System.out.println("\n[INFO] Se han revisado todos los numeros que han salido, pero ninguno coincide con tu carton.");
			}

		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] No se pudo encontrar alguno de los archivos para la comprobacion masiva.");
		}
	}

	private static boolean comprobarLinea(String rutaCarpeta) {
		boolean lineaCompleta = false;
		int lineasCorrectas = 0;
		int lineasErroneas = 0;

		try {
			Scanner scCarton = new Scanner(new File(rutaCarpeta + "\\carton.txt"));
			
			// Leemos el carton agrupado en 3 filas (cada fila tiene 9 valores)
			for (int i = 0; i < 3; i++) {
				boolean filaEsLinea = true;
				boolean tieneNumerosValidos = false;

				for (int j = 0; j < 9; j++) {
					if (scCarton.hasNextLine()) {
						String valor = scCarton.nextLine();
						// Si es un numero distinto de 0
						if (!valor.equals("0")) {
							tieneNumerosValidos = true;
							// Para que la linea este correcta, el numero debe estar tachado con '-'
							if (!valor.startsWith("-")) {
								filaEsLinea = false;
							}
						}
					}
				}

				// Si la fila tenia numeros y todos estaban tachados, cuenta como linea correcta
				if (tieneNumerosValidos && filaEsLinea) {
					lineasCorrectas++;
					lineaCompleta = true;
				} else if (tieneNumerosValidos) {
					lineasErroneas++;
				}
			}
			scCarton.close();

			// Guardar el resultado en el archivo comprobado.txt dentro de la carpeta del usuario
			try (PrintWriter pwComprobado = new PrintWriter(new File(rutaCarpeta + "\\comprobado.txt"))) {
				pwComprobado.println("Lineas completas: " + lineasCorrectas);
				pwComprobado.println("Lineas sin completar: " + lineasErroneas);
			}

			if (lineaCompleta) {
				System.out.println("\n[LINEA] Has completado al menos una linea correctamente!");
				System.out.println("[LOG] Resultados guardados en comprobado.txt (Completas: " + lineasCorrectas + ", Sin completar: " + lineasErroneas + ")");
			} else {
				System.out.println("\n[INFO] Todavia no tienes ninguna linea completa.");
				System.out.println("[LOG] Resultados guardados en comprobado.txt (Completas: " + lineasCorrectas + ", Sin completar: " + lineasErroneas + ")");
			}

		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] No se pudo encontrar el archivo carton.txt para comprobar la linea.");
		}

		return lineaCompleta;
	}

	private static boolean comprobarBingo(String rutaCarpeta) {
		boolean bingoCompleto = true;
		boolean tieneNumeros = false;

		try {
			Scanner scCarton = new Scanner(new File(rutaCarpeta + "\\carton.txt"));
			
			// Leer todo el carton (27 valores: 3 filas x 9 columnas)
			while (scCarton.hasNextLine()) {
				String valor = scCarton.nextLine();
				// Si encontramos un valor que no sea 0
				if (!valor.equals("0")) {
					tieneNumeros = true;
					// Si algun numero valido NO esta tachado con '-', entonces no hay bingo
					if (!valor.startsWith("-")) {
						bingoCompleto = false;
						break;
					}
				}
			}
			scCarton.close();

			// Si el carton estaba vacio (raro, pero preventivo)
			if (!tieneNumeros) {
				bingoCompleto = false;
			}

			if (bingoCompleto) {
				// Calcular estadisticas: contar cuantas bolas han salido en total de partida.txt
				int totalBolasPartida = 0;
				try (Scanner scPartida = new Scanner(new File(RUTA_COMPARTIDA + "\\partida.txt"))) {
					while (scPartida.hasNextInt()) {
						scPartida.nextInt();
						totalBolasPartida++;
					}
				} catch (FileNotFoundException e) {
					// Ignorar si no existe
				}

				System.out.println("=== PARTIDA FINALIZADA ===");
				System.out.println("\n[BINGO] Felicidades! Has completado todo el carton correctamente.");
				System.out.println("[INFO] El estado y estadisticas se han guardado en comprobado.txt. Partida finalizada!");
				
				// Mostrar estadisticas por pantalla
				System.out.println("\n=== ESTADISTICAS DE LA PARTIDA ===");
				System.out.println("- Bolas totales sacadas en la partida: " + totalBolasPartida);
				System.out.println("- Preguntas correctas: " + PREGUNTAS_CORRECTAS);
				System.out.println("- Preguntas fallidas: " + PREGUNTAS_FALLIDAS);

				// Guardar el resultado y estadisticas en el archivo comprobado.txt
				try (PrintWriter pwComprobado = new PrintWriter(new File(rutaCarpeta + "\\resultado.txt"))) {
					pwComprobado.println("BINGO: COMPLETO Y CORRECTO!");
					pwComprobado.println("=== ESTADISTICAS ===");
					pwComprobado.println("Bolas totales sacadas: " + totalBolasPartida);
					pwComprobado.println("Preguntas correctas: " + PREGUNTAS_CORRECTAS);
					pwComprobado.println("Preguntas fallidas: " + PREGUNTAS_FALLIDAS);
				}
			} else {
				// Guardar estado incompleto en comprobado.txt
				try (PrintWriter pwComprobado = new PrintWriter(new File(rutaCarpeta + "\\comprobado.txt"))) {
					pwComprobado.println("BINGO: Incompleto");
				}
				System.out.println("\n[INFO] Todavia no hay BINGO, te faltan numeros por tachar.");
			}

		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] No se pudo encontrar el archivo carton.txt para comprobar el bingo.");
			bingoCompleto = false;
		}

		return bingoCompleto;
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
			System.out.println("[ERROR] No se pudo encontrar el archivo partida.txt.");
			return;
		}

		if (!estaEnPartida) {
			System.out.println("\n[INFO] El numero " + numeroAComprobar + " NO ha salido todavia en la partida.");
			return;
		}

		// 2. Si esta en partida, leemos el carton actual (guardado en formato de 27
		// lineas o matriz)
		// Como carton.txt guarda los 27 elementos secuencialmente (3 filas x 9
		// columnas), los cargamos:
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
			// Comparamos si la linea coincide con el numero (ignorando si ya tiene un guion
			// previo)
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
			System.out.println(
					"\n[INFO] El numero " + numeroAComprobar + " ha salido en la partida, pero NO esta en tu carton!");
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
			System.out.println("[ERROR] No se pudo actualizar el archivo carton.txt.");
		}
	}

	private static int generarUid() {
		int uid;
		Random rnd = new Random();

		uid = rnd.nextInt(100, 99999);

		return uid;
	}

	// Metodo auxiliar para generar el carton con numeros totalmente aleatorios (del
	// 1 al 90) sin repetir
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
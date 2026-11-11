package cliente;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Random;

public class ClienteBingo {
	public static final String RUTA_COMPARTIDA = "\\\\DESKTOP-V7J205P\\Reto-Almingo-Bingo";
	public static int PREGUNTAS_CORRECTAS = 0;
	public static int PREGUNTAS_FALLIDAS = 0;

	public static void main(String[] args) {
		Scanner scTeclado = new Scanner(System.in);
		Scanner scNumero = new Scanner(System.in);
		int minCaracteresNombre = 3;
		int maxCaracteresNombre = 13;

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
			pwId.close();
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
			pwCarton.close();

			// Crear archivos auxiliares vacios
			PrintWriter pwPreguntas = new PrintWriter(new File(dirJugador, "preguntasHechas.txt"));
			pwPreguntas.close();

			PrintWriter pwResultado = new PrintWriter(new File(dirJugador, "resultado.txt"));
			pwResultado.close();

			PrintWriter pwEstadisticas = new PrintWriter(new File(dirJugador, "estadisticas.txt"));
			pwEstadisticas.close();

			System.out.println("\n[LOG] Carton generado y guardado en la red con exito!");
			System.out.println("-> Carpeta asignada: " + nombreCarpeta);
		} catch (FileNotFoundException e) {
			System.out.println("Error al guardar el carton.");
		}

		try {
			for (int cuentaAtras = 3; cuentaAtras > 0; cuentaAtras--) {
				System.out.print(cuentaAtras + "... ");
				Thread.sleep(1000);
			}
			System.out.println();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int opcion = 0;
		do {
			// Comprobar al inicio de cada iteracion si hay un ganador registrado por el
			// servidor
			if (hayGanador()) {
				System.out.println("\n[INFO] La partida ha finalizado. Saliendo del cliente...");
				break;
			}

			// Recargar el carton desde el archivo para reflejar los numeros tachados con
			// '-'
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
			numerosSacados();

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
				try {
					for (int cuentaAtras = 1; cuentaAtras > 0; cuentaAtras--) {
						System.out.print(cuentaAtras + "... ");
						Thread.sleep(1000);
					}
					System.out.println();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			else if (opcion == 2) {
				System.out.print("[NUMERO] Introduce el numero para comprobar: ");
				int numero = scNumero.nextInt();
				comprobarNumero(numero, rutaCarpeta);
				try {
					for (int cuentaAtras = 1; cuentaAtras > 0; cuentaAtras--) {
						System.out.print(cuentaAtras + "... ");
						Thread.sleep(1000);
					}
					System.out.println();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			else if (opcion == 3) {
				comprobarLinea(rutaCarpeta);
				try {
					for (int cuentaAtras = 3; cuentaAtras > 0; cuentaAtras--) {
						System.out.print(cuentaAtras + "... ");
						Thread.sleep(1000);
					}
					System.out.println();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			else if (opcion == 4) {
				boolean bingo = comprobarBingo(rutaCarpeta, nombre, uid);
				if (bingo) {
					opcion = 0; // Salir del bucle tras cantar bingo con exito
				}
				try {
					for (int cuentaAtras = 3; cuentaAtras > 0; cuentaAtras--) {
						System.out.print(cuentaAtras + "... ");
						Thread.sleep(1000);
					}
					System.out.println();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			else if (opcion == 10) {
				System.out.println("[ADMIN] Comprobacion automatica de todos los numeros.");
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

	public static void comprobarTodosLosNumeros(String rutaCarpeta) {
		try {
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

			String[] lineasCarton = new String[27];
			int indice = 0;
			Scanner scCartonLectura = new Scanner(new File(rutaCarpeta + "\\carton.txt"));
			while (scCartonLectura.hasNextLine() && indice < 27) {
				lineasCarton[indice] = scCartonLectura.nextLine();
				indice++;
			}
			scCartonLectura.close();

			boolean huboAciertos = false;
			for (int bola : bolasSalidas) {
				for (int fila = 0; fila < 27; fila++) {
					if (lineasCarton[fila] != null && !lineasCarton[fila].startsWith("-")) {
						try {
							int val = Integer.parseInt(lineasCarton[fila]);
							if (val == bola) {
								lineasCarton[fila] = "-" + val;
								huboAciertos = true;
							}
						} catch (NumberFormatException e) {
							// Ignorar
						}
					}
				}
			}

			if (huboAciertos) {
				PrintWriter pwCartonEscritura = new PrintWriter(new File(rutaCarpeta + "\\carton.txt"));
				for (int fila = 0; fila < 27; fila++) {
					if (lineasCarton[fila] != null) {
						pwCartonEscritura.println(lineasCarton[fila]);
					}
				}
				pwCartonEscritura.close();
				System.out.println("\n[CORRECTO] Se han comprobado todos los numeros y se han tachado los aciertos.");
			} else {
				System.out.println("\n[INFO] Se han revisado los numeros, pero ninguno coincide con tu carton.");
			}

		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] No se pudo encontrar alguno de los archivos para la comprobacion masiva.");
		}
	}

	public static boolean comprobarLinea(String rutaCarpeta) {
		boolean lineaCompleta = false;
		int lineasCorrectas = 0;
		int lineasErroneas = 0;

		File fLineasRespondidas = new File(rutaCarpeta + "\\lineasRespondidas.txt");
		java.util.HashSet<Integer> filasRespondidas = new java.util.HashSet<>();
		if (fLineasRespondidas.exists()) {
			try (Scanner scResp = new Scanner(fLineasRespondidas)) {
				while (scResp.hasNextInt()) {
					filasRespondidas.add(scResp.nextInt());
				}
			} catch (FileNotFoundException e) {
				// Ignorar
			}
		}

		try {
			Scanner scCarton = new Scanner(new File(rutaCarpeta + "\\carton.txt"));

			for (int fila = 0; fila < 3; fila++) {
				boolean filaEsLinea = true;
				boolean tieneNumerosValidos = false;

				for (int columna = 0; columna < 9; columna++) {
					if (scCarton.hasNextLine()) {
						String valor = scCarton.nextLine();
						if (!valor.equals("0")) {
							tieneNumerosValidos = true;
							if (!valor.startsWith("-")) {
								filaEsLinea = false;
							}
						}
					}
				}

				if (tieneNumerosValidos && filaEsLinea) {
					lineasCorrectas++;
					lineaCompleta = true;

					if (!filasRespondidas.contains(fila)) {
						System.out.println(
								"\n[INFO] Has completado la Fila " + (fila + 1) + "! Debes responder una pregunta:");
						boolean respuestaCorrecta = realizarPregunta();
						if (respuestaCorrecta) {
							PREGUNTAS_CORRECTAS++;
							System.out.println("[INFO] Respuesta correcta.\n");
						} else {
							PREGUNTAS_FALLIDAS++;
							System.out.println("[INFO] Respuesta erronea.\n");
						}
						filasRespondidas.add(fila);
					}
				} else if (tieneNumerosValidos) {
					lineasErroneas++;
				}
			}
			scCarton.close();

			try (PrintWriter pwResp = new PrintWriter(fLineasRespondidas)) {
				for (int filaID : filasRespondidas) {
					pwResp.println(filaID);
				}
			} catch (FileNotFoundException e) {
				System.out.println("[ERROR] No se pudo actualizar lineasRespondidas.txt");
			}

			try (PrintWriter pwComprobado = new PrintWriter(new File(rutaCarpeta + "\\comprobado.txt"))) {
				pwComprobado.println("Lineas completas: " + lineasCorrectas);
				pwComprobado.println("Lineas sin completar: " + lineasErroneas);
			}

			if (lineaCompleta) {
				System.out.println("\n[LINEA] Tienes lineas completas en tu carton.");
			} else {
				System.out.println("\n[INFO] Todavia no tienes ninguna linea completa.");
			}

		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] No se pudo encontrar el archivo carton.txt para comprobar la linea.");
		}

		return lineaCompleta;
	}

	public static boolean comprobarBingo(String rutaCarpeta, String nombre, int uid) {
		boolean bingoCompleto = true;
		boolean tieneNumeros = false;

		File fLineasRespondidas = new File(rutaCarpeta + "\\lineasRespondidas.txt");
		int filasRespondidasTotal = 0;
		if (fLineasRespondidas.exists()) {
			try (Scanner scResp = new Scanner(fLineasRespondidas)) {
				while (scResp.hasNextInt()) {
					scResp.nextInt();
					filasRespondidasTotal++;
				}
			} catch (FileNotFoundException e) {
				// Ignorar
			}
		}

		if (filasRespondidasTotal < 3) {
			System.out.println(
					"\n[ALERTA] No puedes cantar bingo todavia. Debes completar y responder las preguntas de las 3 lineas primero.");
			return false;
		}

		try {
			Scanner scCarton = new Scanner(new File(rutaCarpeta + "\\carton.txt"));

			while (scCarton.hasNextLine()) {
				String valor = scCarton.nextLine();
				if (!valor.equals("0")) {
					tieneNumeros = true;
					if (!valor.startsWith("-")) {
						bingoCompleto = false;
						break;
					}
				}
			}
			scCarton.close();

			if (!tieneNumeros) {
				bingoCompleto = false;
			}

			if (bingoCompleto) {
				boolean respuestaCorrecta = realizarPregunta();
				if (respuestaCorrecta) {
					PREGUNTAS_CORRECTAS++;
					System.out.println("[INFO] Respuesta correcta.\n");
				} else {
					PREGUNTAS_FALLIDAS++;
					System.out.println("[INFO] Respuesta erronea.\n");
				}

				int totalBolasPartida = 0;
				try (Scanner scPartida = new Scanner(new File(RUTA_COMPARTIDA + "\\partida.txt"))) {
					while (scPartida.hasNextInt()) {
						scPartida.nextInt();
						totalBolasPartida++;
					}
				} catch (FileNotFoundException e) {
					// Ignorar
				}

				System.out.println("=== PARTIDA FINALIZADA ===");
				System.out.println("[BINGO] Felicidades! Has completado todo el carton correctamente.");

				System.out.println("\n=== ESTADISTICAS DE LA PARTIDA ===");
				System.out.println("- Bolas totales sacadas en la partida: " + totalBolasPartida);
				System.out.println("- Preguntas correctas: " + PREGUNTAS_CORRECTAS);
				System.out.println("- Preguntas fallidas: " + PREGUNTAS_FALLIDAS);

				try (PrintWriter pwEst = new PrintWriter(new File(rutaCarpeta + "\\estadisticas.txt"))) {
					pwEst.println("BINGO: COMPLETO Y CORRECTO!");
					pwEst.println("=== ESTADISTICAS ===");
					pwEst.println("Bolas totales sacadas: " + totalBolasPartida);
					pwEst.println("Preguntas correctas: " + PREGUNTAS_CORRECTAS);
					pwEst.println("Preguntas fallidas: " + PREGUNTAS_FALLIDAS);
				}

				try (PrintWriter pwRes = new PrintWriter(new File(rutaCarpeta + "\\resultado.txt"))) {
					pwRes.println("Victoria");
					pwRes.println(nombre);
					pwRes.println(uid);
				}
			} else {
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

	public static void comprobarNumero(int numeroAComprobar, String rutaCarpeta) {
		boolean estaEnPartida = false;

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

		for (int fila = 0; fila < 27; fila++) {
			if (lineasCarton[fila] != null && !lineasCarton[fila].startsWith("-")) {
				try {
					int val = Integer.parseInt(lineasCarton[fila]);
					if (val == numeroAComprobar) {
						lineasCarton[fila] = "-" + val;
						encontradoEnCarton = true;
					}
				} catch (NumberFormatException e) {
					// Ignorar
				}
			}
		}

		if (!encontradoEnCarton) {
			System.out.println(
					"\n[INFO] El numero " + numeroAComprobar + " ha salido en la partida, pero NO esta en tu carton!");
			return;
		}

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

	public static int generarUid() {
		Random rnd = new Random();
		return rnd.nextInt(100, 99999);
	}

	public static int[][] generarCartonCliente() {
		int[][] carton = new int[3][9];
		Random rnd = new Random();

		for (int fila = 0; fila < 3; fila++) {
			for (int columna = 0; columna < 9; columna++) {
				carton[fila][columna] = 0;
			}
		}

		for (int fila = 0; fila < 3; fila++) {
			int numerosColocados = 0;
			while (numerosColocados < 5) {
				int columna = rnd.nextInt(9);

				if (carton[fila][columna] == 0) {
					int num = rnd.nextInt(1, 91);

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

					if (!repetidoEnCarton) {
						carton[fila][columna] = num;
						numerosColocados++;
					}
				}
			}
		}
		return carton;
	}

	public static boolean realizarPregunta() {
		System.out.println();
		Scanner sc = new Scanner(System.in);
		JuegoPreguntas juego = new JuegoPreguntas();
		return juego.lanzarPregunta(sc);
	}

	public static void numerosSacados() {
		System.out.println("\nNumeros que ya han salido:");
		try (Scanner scBolasSalidas = new Scanner(new File(RUTA_COMPARTIDA + "\\partida.txt"))) {
			boolean primero = true;
			int contadorBolas = 0;

			while (scBolasSalidas.hasNext()) {
				String numero = scBolasSalidas.next();

				if (!primero) {
					System.out.print(", ");
				}

				System.out.print(numero);
				primero = false;
				contadorBolas++;

				if (contadorBolas % 30 == 0) {
					System.out.println();
					primero = true;
				}
			}

			if (primero && contadorBolas == 0) {
				System.out.print("Ninguno todavia.");
			}
			System.out.println();
		} catch (FileNotFoundException e) {
			System.out.println("Ninguno todavia.");
		}
	}

	public static boolean hayGanador() {
		boolean ganador = false;
		File fEstado = new File(RUTA_COMPARTIDA + "\\estadoPartida.txt");

		if (fEstado.exists() && fEstado.length() > 0) {
			try (Scanner scEstado = new Scanner(fEstado)) {
				if (scEstado.hasNextLine()) {
					String primeraLinea = scEstado.nextLine().trim();
					if (primeraLinea.startsWith("=")) {
						ganador = true;

						System.out.println("\n[AVISO] Se ha detectado un ganador en la partida!");
						System.out.println("=== CONTENIDO DE ESTADO DE PARTIDA ===");
						System.out.println(primeraLinea);

						while (scEstado.hasNextLine()) {
							System.out.println(scEstado.nextLine());
						}
						System.out.println("=====================================");
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("[ERROR] No se pudo leer el archivo estadoPartida.txt.");
			}
		}

		return ganador;
	}
}
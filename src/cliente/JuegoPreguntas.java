package cliente;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class JuegoPreguntas {

	private ArrayList<DatosPregunta> listaPreguntas = new ArrayList<>();
	private ArrayList<Integer> idsUsados = new ArrayList<>();

	public JuegoPreguntas() { // lee el archivo con las preguntas y las divide en partes, guardando cada
								// opción en una variable diferente para tener las opciones de las preguntas
								// separadas
		try {
			BufferedReader br = new BufferedReader(new FileReader("preguntas.txt"));

			String linea = br.readLine();

			while (linea != null) {
				String[] partes = linea.split("\\$"); // coloco la doble barra para evitar un salto de linea por el
														// caracter

				int id = Integer.parseInt(partes[0]);
				String pregunta = partes[1];
				String opA = partes[2];
				String opB = partes[3];
				String opC = partes[4];
				String opD = partes[5];
				String correcta = partes[6];

				DatosPregunta p = new DatosPregunta(id, pregunta, opA, opB, opC, opD, correcta); // se añaden las
																									// variables a un
																									// arraylist
				listaPreguntas.add(p);

				linea = br.readLine();
			}
			br.close();

		} catch (IOException e) {
			System.out.println("Error al leer el archivo");
		}
	}

	public DatosPregunta sacarPregunta() { // baraja las preguntas para no mantener siempre el mismo orden, una vez
											// salida la pregunta se guarda el id en un array para evitar que se repita
											// la prefunta

		if (idsUsados.size() == listaPreguntas.size()) {
			idsUsados.clear();
		}

		DatosPregunta p;

		do {
			int numero = (int) (Math.random() * listaPreguntas.size());
			p = listaPreguntas.get(numero);
		} while (idsUsados.contains(p.getId()));

		idsUsados.add(p.getId());
		return p;
	}

	public boolean lanzarPregunta(Scanner sc) { // lanza las preguntas, comprueba la respuesta si es correcta y lanza el
												// mensaje. devuelve un booleano dependiendo si es correcta o no

		DatosPregunta p = sacarPregunta();

		System.out.println(p.getTextoPregunta());
		System.out.println("A) " + p.getOpA());
		System.out.println("B) " + p.getOpB());
		System.out.println("C) " + p.getOpC());
		System.out.println("D) " + p.getOpD());

		System.out.println("Tu respuesta:");
		String respuesta = sc.nextLine();

		if (respuesta.equalsIgnoreCase(p.getCorrecta())) {
			System.out.println("¡Correcto!");
			return true;
		} else {
			System.out.println("Incorrecto. La correcta era: " + p.getCorrecta());
			return false;
		}
	}

}

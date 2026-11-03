package preguntas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class JuegoPreguntas {

	public static void main(String[] args) {

		ArrayList<DatosPregunta> listaPreguntas = new ArrayList<>();


		try {

			BufferedReader br = new BufferedReader(new FileReader("preguntas.csv"));

			String linea;

			br.readLine();

			linea = br.readLine();

			while (linea != null) {

				String[] partes = linea.split(",");

				int id = Integer.parseInt(partes[0]);
				String pregunta = partes[1];
				String opA = partes[2];
				String opB = partes[3];
				String opC = partes[4];
				String opD = partes[5];
				String correcta = partes[6];

				DatosPregunta p = new DatosPregunta(id, pregunta, opA, opB, opC, opD, correcta);

				listaPreguntas.add(p);

				linea = br.readLine();
			}
			br.close();

		} catch (IOException e) {
			System.out.println("Error al leer el archivo");
		}

	}


	public static void lanzarPregunta(ArrayList<DatosPregunta> lista) {

		int numero = (int) (Math.random() * lista.size());

		DatosPregunta p = lista.get(numero);

		System.out.println(p.textoPregunta);
		System.out.println("A) " + p.opA);
		System.out.println("B) " + p.opB);
		System.out.println("C) " + p.opC);
		System.out.println("D) " + p.opD);

		Scanner sc = new Scanner(System.in);

		System.out.println("Tu respuesta:");
		String respuesta = sc.nextLine();

		if (respuesta.equalsIgnoreCase(p.correcta)) {
			System.out.println("¡Correcto!");
		} else {
			System.out.println("Incorrecto");
			System.out.println("La correcta era: " + p.correcta);
		}
	}

}

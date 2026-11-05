package preguntas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class JuegoPreguntas {

    private ArrayList<DatosPregunta> listaPreguntas = new ArrayList<>();
    private ArrayList<Integer> idsUsados = new ArrayList<>();


    public JuegoPreguntas() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("preguntas.txt"));

            String linea = br.readLine();

            while (linea != null) {
                String[] partes = linea.split("\\$");

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

 
    public DatosPregunta sacarPregunta() {

    
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


    public boolean lanzarPregunta(Scanner sc) {

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
    
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        JuegoPreguntas juego = new JuegoPreguntas();
        int x=1, y =0;
        while (x>y) {
        juego.lanzarPregunta(sc);

        }
    }
        
}

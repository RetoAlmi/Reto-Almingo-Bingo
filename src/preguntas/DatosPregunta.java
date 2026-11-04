package preguntas;

public class DatosPregunta {

    int id;
    String textoPregunta;
    String opA;
    String opB;
    String opC;
    String opD;
    String correcta;

    public DatosPregunta(int id, String textoPregunta, String opA,
            String opB, String opC, String opD, String correcta) {

        this.id = id;
        this.textoPregunta = textoPregunta;
        this.opA = opA;
        this.opB = opB;
        this.opC = opC;
        this.opD = opD;
        this.correcta = correcta;
    }

    public int getId() {
        return id;
    }

    public String getTextoPregunta() {
        return textoPregunta;
    }

    public String getOpA() {
        return opA;
    }

    public String getOpB() {
        return opB;
    }

    public String getOpC() {
        return opC;
    }

    public String getOpD() {
        return opD;
    }

    public String getCorrecta() {
        return correcta;
    }
}

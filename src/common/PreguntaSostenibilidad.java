package common;

import java.io.Serializable;

public class PreguntaSostenibilidad implements Serializable {
    private static final long serialVersionUID = 1L;
    private String enunciado;
    private String respuestaCorrecta;

    public PreguntaSostenibilidad(String enunciado, String respuestaCorrecta) {
        this.enunciado = enunciado;
        this.respuestaCorrecta = respuestaCorrecta;
    }

    // OBTENER ENUNCIADO
    public String getEnunciado() {
        return enunciado;
    }

    // COMPROBAR RESPUESTA SI ES CORRECTA O NO
    public boolean validarRespuesta(String respuestaUsuario) {
        return this.respuestaCorrecta.trim().equalsIgnoreCase(respuestaUsuario.trim());
    }
}
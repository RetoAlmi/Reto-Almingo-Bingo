package comun;

import java.io.IOException;

public class Config {
    public static final String RUTA_COMPARTIDA = "\\\\DESKTOP-V7J205P\\Users\\DAM1\\Documents\\GitHub\\Reto-Almingo-Bingo\\carpeta_compartida";
    
    // Credenciales para la red de Windows
    private static final String USUARIO_RED = "Alumnado";
    private static final String PASS_RED = "Almi26-27";
    private static final String EQUIPO_RED = "\\\\DESKTOP-V7J205P\\Users\\\\DAM1\\\\Documents\\\\GitHub\\\\Reto-Almingo-Bingo\\\\carpeta_compartida";

    // Método para forzar la conexión de red desde Java
    public static void conectarRed() {
        try {
            // Comando de Windows para autenticar la máquina en la red local
            String comando = "net use " + EQUIPO_RED + " " + PASS_RED + " /user:" + USUARIO_RED;
            Process proceso = Runtime.getRuntime().exec(comando);
            proceso.waitFor();
            System.out.println("[CONFIG] Conexión a la carpeta compartida establecida correctamente.");
        } catch (Exception e) {
            System.out.println("[CONFIG] Aviso: No se pudo establecer el enlace de red automático (puede que ya esté conectado).");
        }
    }
}
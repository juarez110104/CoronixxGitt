// ============================================================
// CoronixGit.java
// Punto de entrada principal del programa Coronix IDE.
// Se encarga únicamente de lanzar la interfaz gráfica
// de forma segura usando el hilo de eventos de Swing.
// ============================================================

import Interfaz.InterfazCoronix;
import javax.swing.SwingUtilities;

public class CoronixGit {

    /**
     * Método principal del programa.
     * Utiliza SwingUtilities.invokeLater para garantizar que la interfaz
     * gráfica se cree en el hilo de despacho de eventos (EDT),
     * lo cual es obligatorio en aplicaciones Swing para evitar
     * problemas de concurrencia.
     *
     * @param args Argumentos de línea de comandos (no se usan en este proyecto)
     */
    public static void main(String[] args) {
        // Lanza la ventana principal del IDE Coronix en el hilo de eventos de Swing
        SwingUtilities.invokeLater(InterfazCoronix::new);
    }
}

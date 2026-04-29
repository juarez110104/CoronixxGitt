package Analizador;

/**
 * Error léxico, sintáctico o semántico del lenguaje Coronix.
 */
public class Error {

    public enum Tipo { LEXICO, SINTACTICO, SEMANTICO }

    public final Tipo   tipo;
    public final int    linea;
    public final String mensaje;

    public Error(Tipo tipo, int linea, String mensaje) {
        this.tipo    = tipo;
        this.linea   = linea;
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        String l = linea > 0 ? " en línea " + linea : "";
        return "Error " + tipo.name() + l + ": " + mensaje;
    }
}
package Analizador;

/**
 * Información de un token reconocido por el Lexer.
 * Se usa para llenar la tabla Token | Tipo | Línea | Valor en la GUI.
 */
public class Token {
    public final String lexema;
    public final String tipo;
    public final int    linea;
    public final String valor;

    public Token(String lexema, String tipo, int linea, String valor) {
        this.lexema = lexema;
        this.tipo   = tipo;
        this.linea  = linea;
        this.valor  = valor;
    }
}
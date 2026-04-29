package Analizador;
 
/**
 * Excepción lanzada por el Lexer de Coronix cuando detecta
 * un carácter no válido o un identificador de más de 10 caracteres.
 */
public class LexicalException extends RuntimeException {
    public LexicalException(String mensaje) { super(mensaje); }
}
 
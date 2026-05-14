// ============================================================
// Token.java
// Representa la unidad mínima de significado (token) que
// produce el analizador léxico al procesar código Coronix.
//
// Por ejemplo, al leer:  cuarto a => 10!!
// El lexer generaría tokens como:
//   [TIPO, "cuarto"] [IDENTIFICADOR, "a"] [ASIGNACION, "=>"]
//   [ENTERO, "10"]   [FIN_LINEA, "!!"]
//
// NOTA: Esta clase está pendiente de implementar. Actualmente
// el proyecto usa análisis manual de cadenas en InterfazCoronix.
// Está preparada para conectarse con JFlex en el futuro.
// ============================================================

package Analizador;

/**
 * Clase Token — unidad léxica del lenguaje Coronix.
 * Pendiente de implementación con JFlex.
 */
public class Token {
    // TODO: Agregar atributos como:
    //   - String tipo      (ej. "CUARTO", "IDENTIFICADOR", "ENTERO")
    //   - String valor     (ej. "cuarto", "a", "10")
    //   - int    linea     (número de línea donde aparece el token)
}

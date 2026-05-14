// ============================================================
// Error.java
// Representa un error detectado durante el análisis del código
// Coronix. Cada error tiene un tipo, número de línea y mensaje
// descriptivo para informar al usuario qué salió mal y dónde.
// ============================================================

package Analizador;

public class Error {

    /**
     * Tipos de errores que puede detectar el analizador de Coronix:
     * - LEXICO:     carácter o símbolo no reconocido por el lenguaje
     * - SINTACTICO: estructura de la sentencia incorrecta
     * - SEMANTICO:  error de lógica, por ejemplo dividir entre cero
     *               o usar una variable no declarada
     */
    public enum TipoError { LEXICO, SINTACTICO, SEMANTICO }

    // Tipo de error (léxico, sintáctico o semántico)
    private final TipoError tipo;

    // Número de línea donde ocurrió el error
    private final int linea;

    // Descripción del error para mostrar al usuario
    private final String mensaje;

    /**
     * Constructor del error.
     *
     * @param tipo    Categoría del error (LEXICO, SINTACTICO, SEMANTICO)
     * @param linea   Línea del código donde se detectó el error
     * @param mensaje Descripción detallada del problema
     */
    public Error(TipoError tipo, int linea, String mensaje) {
        this.tipo    = tipo;
        this.linea   = linea;
        this.mensaje = mensaje;
    }

    /**
     * Devuelve una representación legible del error,
     * incluyendo su tipo, línea y mensaje.
     * Ejemplo: "❌ Error LEXICO en línea 3: carácter inválido '@'"
     */
    @Override
    public String toString() {
        return String.format("❌ Error %s en línea %d: %s",
                tipo.name(), linea, mensaje);
    }

    // ── Getters ──────────────────────────────────────────────

    /** Retorna el tipo de error (LEXICO, SINTACTICO o SEMANTICO) */
    public TipoError getTipo()    { return tipo;    }

    /** Retorna el número de línea donde ocurrió el error */
    public int       getLinea()   { return linea;   }

    /** Retorna el mensaje descriptivo del error */
    public String    getMensaje() { return mensaje; }
}

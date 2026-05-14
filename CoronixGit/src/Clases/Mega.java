// ============================================================
// Mega.java
// Representa el tipo de dato CADENA (String) del lenguaje Coronix.
// El nombre "mega" hace referencia a una cadena de texto.
// Las cadenas deben ir entre comillas dobles.
//
// Sintaxis en Coronix:   mega saludo => "hola mundo"!!
// ============================================================

package Clases;

public class Mega {

    // Valor de texto almacenado en esta variable
    private String valor;

    /**
     * Constructor que inicializa el valor de texto.
     *
     * @param valor La cadena de texto que almacenará esta variable
     */
    public Mega(String valor) {
        this.valor = valor;
    }

    /**
     * Retorna el valor de texto almacenado.
     *
     * @return La cadena de texto
     */
    public String getValor() { return valor; }

    // ── Operaciones de cadena ─────────────────────────────────

    /**
     * Concatena (une) esta cadena con otra Mega usando el operador @.
     * A diferencia de los tipos numéricos, Mega no soporta
     * operaciones aritméticas, solo concatenación.
     *
     * Ejemplo en Coronix: mega a = "hola", mega b = " mundo"
     *                     a @ b  →  "hola mundo"
     *
     * @param otro La otra cadena de tipo Mega a concatenar
     * @return Nuevo Mega con ambas cadenas unidas
     */
    public Mega concatenar(Mega otro) {
        return new Mega(this.valor + otro.valor);
    }

    /**
     * Devuelve el valor de texto para mostrarlo en la consola.
     * Ejemplo: si valor = "hola mundo", retorna "hola mundo"
     */
    @Override
    public String toString() { return valor; }
}

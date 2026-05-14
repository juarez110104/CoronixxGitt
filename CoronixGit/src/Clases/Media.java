// ============================================================
// Media.java
// Representa el tipo de dato DECIMAL del lenguaje Coronix.
// El nombre "media" hace referencia a un número con decimales
// (equivalente a double en Java).
//
// Sintaxis en Coronix:   media pi => 3.14!!
// ============================================================

package Clases;

public class Media {

    // Valor decimal almacenado en esta variable
    private double valor;

    /**
     * Constructor que inicializa el valor decimal.
     *
     * @param valor El número decimal que almacenará esta variable
     */
    public Media(double valor) {
        this.valor = valor;
    }

    /**
     * Retorna el valor decimal almacenado.
     *
     * @return El valor numérico decimal
     */
    public double getValor() { return valor; }

    // ── Operaciones aritméticas ───────────────────────────────

    /**
     * Suma este valor con otro Media y devuelve el resultado.
     * Ejemplo: media a = 1.5, media b = 2.5 → a.sumar(b) = 4.0
     *
     * @param otro El otro operando de tipo Media
     * @return Nuevo Media con el resultado de la suma
     */
    public Media sumar(Media otro) {
        return new Media(this.valor + otro.valor);
    }

    /**
     * Resta este valor con otro Media y devuelve el resultado.
     *
     * @param otro El otro operando de tipo Media
     * @return Nuevo Media con el resultado de la resta
     */
    public Media restar(Media otro) {
        return new Media(this.valor - otro.valor);
    }

    /**
     * Multiplica este valor con otro Media y devuelve el resultado.
     *
     * @param otro El otro operando de tipo Media
     * @return Nuevo Media con el resultado de la multiplicación
     */
    public Media multiplicar(Media otro) {
        return new Media(this.valor * otro.valor);
    }

    /**
     * Divide este valor entre otro Media y devuelve el resultado.
     * Lanza una excepción semántica si el divisor es cero.
     *
     * @param otro El divisor de tipo Media
     * @return Nuevo Media con el resultado de la división
     * @throws ArithmeticException si el divisor es cero
     */
    public Media dividir(Media otro) {
        // Validación semántica: no se permite dividir entre cero
        if (otro.valor == 0)
            throw new ArithmeticException("❌ Error semántico: división entre cero");
        return new Media(this.valor / otro.valor);
    }

    /**
     * Devuelve el valor como texto para mostrarlo en la consola.
     * Ejemplo: si valor = 3.14, retorna "3.14"
     */
    @Override
    public String toString() { return String.valueOf(valor); }
}

// ============================================================
// Cuarto.java
// Representa el tipo de dato ENTERO del lenguaje Coronix.
// El nombre "cuarto" hace referencia a un número entero simple.
//
// Sintaxis en Coronix:   cuarto a => 10!!
// ============================================================

package Clases;

public class Cuarto {

    // Valor entero almacenado en esta variable
    private int valor;

    /**
     * Constructor que inicializa el valor entero.
     *
     * @param valor El número entero que almacenará esta variable
     */
    public Cuarto(int valor) {
        this.valor = valor;
    }

    /**
     * Retorna el valor entero almacenado.
     *
     * @return El valor numérico entero
     */
    public int getValor() { return valor; }

    // ── Operaciones aritméticas ───────────────────────────────

    /**
     * Suma este valor con otro Cuarto y devuelve el resultado.
     * Ejemplo: cuarto a = 5, cuarto b = 3 → a.sumar(b) = 8
     *
     * @param otro El otro operando de tipo Cuarto
     * @return Nuevo Cuarto con el resultado de la suma
     */
    public Cuarto sumar(Cuarto otro) {
        return new Cuarto(this.valor + otro.valor);
    }

    /**
     * Resta este valor con otro Cuarto y devuelve el resultado.
     *
     * @param otro El otro operando de tipo Cuarto
     * @return Nuevo Cuarto con el resultado de la resta
     */
    public Cuarto restar(Cuarto otro) {
        return new Cuarto(this.valor - otro.valor);
    }

    /**
     * Multiplica este valor con otro Cuarto y devuelve el resultado.
     *
     * @param otro El otro operando de tipo Cuarto
     * @return Nuevo Cuarto con el resultado de la multiplicación
     */
    public Cuarto multiplicar(Cuarto otro) {
        return new Cuarto(this.valor * otro.valor);
    }

    /**
     * Divide este valor entre otro Cuarto y devuelve el resultado.
     * Lanza una excepción semántica si el divisor es cero.
     *
     * @param otro El divisor de tipo Cuarto
     * @return Nuevo Cuarto con el resultado de la división entera
     * @throws ArithmeticException si el divisor es cero
     */
    public Cuarto dividir(Cuarto otro) {
        // Validación semántica: no se permite dividir entre cero
        if (otro.valor == 0)
            throw new ArithmeticException("❌ Error semántico: división entre cero");
        return new Cuarto(this.valor / otro.valor);
    }

    /**
     * Devuelve el valor como texto para mostrarlo en la consola.
     * Ejemplo: si valor = 42, retorna "42"
     */
    @Override
    public String toString() { return String.valueOf(valor); }
}

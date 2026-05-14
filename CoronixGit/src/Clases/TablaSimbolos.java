// ============================================================
// TablaSimbolos.java
// Estructura de datos central del intérprete Coronix.
// Almacena todas las variables declaradas durante la ejecución
// de un programa, guardando su nombre, tipo y valor actual.
//
// Funciona como un diccionario:
//   nombre de variable → (tipo, valor)
//
// Ejemplo: después de ejecutar "cuarto edad => 20!!"
//   tabla["edad"] = Simbolo("cuarto", 20)
// ============================================================

package Clases;

import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {

    /**
     * Clase interna que representa una entrada en la tabla.
     * Cada símbolo guarda el tipo del dato y su valor actual.
     */
    public static class Simbolo {

        // Tipo del dato: "cuarto" (entero), "media" (decimal) o "mega" (cadena)
        public String tipo;

        // Valor actual de la variable (puede ser Cuarto, Media o Mega)
        public Object valor;

        /**
         * Constructor del símbolo.
         *
         * @param tipo  El tipo de dato ("cuarto", "media" o "mega")
         * @param valor El valor almacenado en la variable
         */
        public Simbolo(String tipo, Object valor) {
            this.tipo  = tipo;
            this.valor = valor;
        }

        /**
         * Representación textual del símbolo.
         * Ejemplo: "[cuarto] = 20"
         */
        @Override
        public String toString() {
            return "[" + tipo + "] = " + valor;
        }
    }

    // Mapa principal que asocia nombres de variables con sus símbolos
    // Clave: nombre de la variable (ej. "edad")
    // Valor: objeto Simbolo con tipo y valor
    private final Map<String, Simbolo> tabla = new HashMap<>();

    // ── Métodos de la tabla ───────────────────────────────────

    /**
     * Verifica si una variable ya fue declarada en la tabla.
     * Útil para detectar errores semánticos como redeclaración
     * o uso de variables no definidas.
     *
     * @param nombre El nombre de la variable a buscar
     * @return true si la variable existe, false si no
     */
    public boolean existe(String nombre) {
        return tabla.containsKey(nombre);
    }

    /**
     * Agrega una nueva variable a la tabla de símbolos.
     * Si el nombre ya existe, sobreescribe el valor anterior.
     *
     * @param nombre El nombre de la variable
     * @param s      El símbolo con tipo y valor a guardar
     */
    public void agregar(String nombre, Simbolo s) {
        tabla.put(nombre, s);
    }

    /**
     * Obtiene el símbolo asociado a una variable.
     * Retorna null si la variable no existe (usar existe() antes).
     *
     * @param nombre El nombre de la variable a buscar
     * @return El Simbolo con tipo y valor, o null si no existe
     */
    public Simbolo obtener(String nombre) {
        return tabla.get(nombre);
    }

    /**
     * Elimina todas las variables de la tabla.
     * Se usa al limpiar o reiniciar el programa Coronix.
     */
    public void limpiar() {
        tabla.clear();
    }

    /**
     * Devuelve todas las variables almacenadas en la tabla.
     * Útil para mostrar el estado completo de variables
     * al usuario o para depuración.
     *
     * @return Mapa con todos los símbolos registrados
     */
    public Map<String, Simbolo> getTodos() {
        return tabla;
    }
}

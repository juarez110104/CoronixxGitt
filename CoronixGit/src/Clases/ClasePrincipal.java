// ============================================================
// ClasePrincipal.java
// Prototipo inicial del intérprete Coronix usando análisis
// manual de cadenas de texto (sin JFlex ni CUP).
//
// Esta clase fue la primera versión del intérprete, creada
// para probar la lógica básica antes de desarrollar la
// interfaz gráfica. Actualmente está reemplazada por el
// analizador integrado en InterfazCoronix.java.
//
// NOTA: Ya no se usa en la aplicación principal, pero sirve
// como referencia del funcionamiento interno del lenguaje.
// ============================================================

package Clases;

import java.util.*;

public class ClasePrincipal {

    // Mapa estático que simula una tabla de símbolos simple.
    // Guarda las variables de tipo "cuarto" declaradas durante la ejecución.
    static Map<String, Cuarto> variables = new HashMap<>();

    /**
     * Método principal para probar el intérprete de forma manual.
     * Simula la ejecución de tres líneas de código Coronix.
     */
    public static void main(String[] args) {
        // Prueba: declarar dos variables enteras y sumarlas
        procesar("cuarto a = 5;");
        procesar("cuarto b = 3;");
        procesar("a + b");
    }

    /**
     * Procesa una línea de código Coronix de forma manual.
     * Detecta si es una declaración de variable o una expresión aritmética.
     *
     * Soporta:
     *   - Declaración: "cuarto nombre = valor;"
     *   - Suma:        "variable1 + variable2"
     *
     * @param linea La línea de código Coronix a procesar
     */
    public static void procesar(String linea) {

        linea = linea.trim();

        // ── Detección léxica: la línea comienza con "cuarto" ─
        // Significa que es una declaración de variable entera
        if (linea.startsWith("cuarto")) {

            // Divide la línea en partes: ["cuarto", "nombre", "=", "valor"]
            String[] partes = linea.replace(";", "").split(" ");

            String nombre = partes[1];               // nombre de la variable
            int valor = Integer.parseInt(partes[3]);  // valor entero asignado

            // Guarda la variable en el mapa de variables
            variables.put(nombre, new Cuarto(valor));

            System.out.println("Declarado: " + nombre);

        // ── Detección sintáctica + semántica: la línea contiene "+" ─
        // Significa que es una expresión de suma entre dos variables
        } else if (linea.contains("+")) {

            // Divide la expresión en dos operandos: ["a", "b"]
            String[] partes = linea.split("\\+");

            // Obtiene los objetos Cuarto correspondientes a cada variable
            Cuarto a = obtener(partes[0].trim());
            Cuarto b = obtener(partes[1].trim());

            // Realiza la suma usando el método de la clase Cuarto
            Cuarto resultado = a.sumar(b);

            System.out.println("Resultado: " + resultado);
        }
    }

    /**
     * Busca una variable por nombre en el mapa de variables.
     * Si la variable no existe, lanza un error semántico.
     *
     * @param nombre El nombre de la variable a buscar
     * @return El objeto Cuarto asociado a esa variable
     * @throws RuntimeException si la variable no fue declarada antes de usarse
     */
    public static Cuarto obtener(String nombre) {
        if (!variables.containsKey(nombre)) {
            throw new RuntimeException("Error semántico: variable no definida -> " + nombre);
        }
        return variables.get(nombre);
    }
}

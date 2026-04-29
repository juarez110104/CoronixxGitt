package Clases;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.util.*;

/**
 *
 * @author zvela
 */
public class ClasePrincipal {

    static Map<String, Cuarto> variables = new HashMap<>();

    public static void main(String[] args) {

        procesar("cuarto a = 5;");
        procesar("cuarto b = 3;");
        procesar("a + b");
    }

    public static void procesar(String linea) {

        linea = linea.trim();

        // 🔹 Simulación léxica: detectar "cuarto"
        if (linea.startsWith("cuarto")) {

            String[] partes = linea.replace(";", "").split(" ");

            String nombre = partes[1];
            int valor = Integer.parseInt(partes[3]);

            variables.put(nombre, new Cuarto(valor));

            System.out.println("Declarado: " + nombre);
        } // 🔹 Simulación sintáctica + semántica
        else if (linea.contains("+")) {

            String[] partes = linea.split("\\+");

            Cuarto a = obtener(partes[0].trim());
            Cuarto b = obtener(partes[1].trim());

            Cuarto resultado = a.sumar(b);

            System.out.println("Resultado: " + resultado);
        }
    }

    public static Cuarto obtener(String nombre) {
        if (!variables.containsKey(nombre)) {
            throw new RuntimeException("Error semántico: variable no definida -> " + nombre);
        }
        return variables.get(nombre);
    }
}

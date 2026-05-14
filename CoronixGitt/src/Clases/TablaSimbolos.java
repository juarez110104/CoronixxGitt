package Clases;

import Analizador.ErrorC;
import java.util.*;

public class TablaSimbolos {

    // Clase interna para guardar tipo y valor
    public static class Entrada {
        public String tipo;
        public Object valor;
        public Entrada(String tipo, Object valor) {
            this.tipo = tipo;
            this.valor = valor;
        }
    }

    private Map<String, Entrada> tabla = new HashMap<>();

    // ✅ Verifica si una variable ya fue declarada
    public boolean existe(String nombre) {
        return tabla.containsKey(nombre);
    }

    public void guardar(String nombre, String tipo, Object valor) {
        tabla.put(nombre, new Entrada(tipo, valor));
    }

    public Entrada obtener(String nombre, int ln) throws ErrorC {
        Entrada e = tabla.get(nombre);
        if (e == null)
            throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                "Variable no declarada: '" + nombre + "'");
        return e;
    }
}
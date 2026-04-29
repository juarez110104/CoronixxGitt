package Clases;

import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {

    // tipo puede ser "cuarto", "media", "mega"
    public static class Simbolo {
        public String tipo;
        public Object valor;

        public Simbolo(String tipo, Object valor) {
            this.tipo  = tipo;
            this.valor = valor;
        }

        @Override
        public String toString() {
            return "[" + tipo + "] = " + valor;
        }
    }

    private final Map<String, Simbolo> tabla = new HashMap<>();

    public boolean existe(String nombre) {
        return tabla.containsKey(nombre);
    }

    public void agregar(String nombre, Simbolo s) {
        tabla.put(nombre, s);
    }

    public Simbolo obtener(String nombre) {
        return tabla.get(nombre);
    }

    public void limpiar() {
        tabla.clear();
    }

    public Map<String, Simbolo> getTodos() {
        return tabla;
    }
}
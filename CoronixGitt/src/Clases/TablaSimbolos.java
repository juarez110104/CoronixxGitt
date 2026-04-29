package Clases;

import java.util.LinkedHashMap;
import java.util.Map;

public class TablaSimbolos {

    // 🔹 Clase interna: símbolo
    public static class Simbolo {
        public String nombre;
        public String tipo;
        public Object valor;

        public Simbolo(String nombre, String tipo, Object valor) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.valor = valor;
        }

        @Override
        public String toString() {
            return nombre + " : " + tipo + " = " + valor;
        }
    }

    // 🔹 Tabla
    private Map<String, Simbolo> tabla = new LinkedHashMap<>();

    // 🔥 INSERTAR / ACTUALIZAR
    public void guardar(String nombre, String tipo, Object valor) {
        tabla.put(nombre, new Simbolo(nombre, tipo, valor));
    }

    // 🔥 OBTENER
    public Simbolo obtener(String nombre) {
        if (!tabla.containsKey(nombre)) {
            throw new RuntimeException("Variable no definida: " + nombre);
        }
        return tabla.get(nombre);
    }

    // 🔥 EXISTE
    public boolean existe(String nombre) {
        return tabla.containsKey(nombre);
    }

    // 🔥 LIMPIAR
    public void limpiar() {
        tabla.clear();
    }

    // 🔥 PARA MOSTRAR EN INTERFAZ
    public Map<String, Simbolo> getTabla() {
        return tabla;
    }
}
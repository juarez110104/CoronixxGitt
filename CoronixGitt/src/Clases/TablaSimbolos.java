package Clases;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tabla de símbolos de Coronix.
 * Permite re-declarar variables (sobreescritura).
 */
public class TablaSimbolos {

    public static class Simbolo {
        public String tipo;   // "cuarto" | "media" | "mega"
        public Object valor;  // instancia Cuarto / Media / Mega

        public Simbolo(String tipo, Object valor) {
            this.tipo  = tipo;
            this.valor = valor;
        }
        @Override public String toString() { return "[" + tipo + "] = " + valor; }
    }

    private final Map<String, Simbolo> tabla = new LinkedHashMap<>();

    public boolean  existe(String n)             { return tabla.containsKey(n); }
    public void     agregar(String n, Simbolo s) { tabla.put(n, s); }
    public Simbolo  obtener(String n)            { return tabla.get(n); }
    public void     limpiar()                    { tabla.clear(); }
    public Map<String, Simbolo> getTodos()       { return tabla; }
}
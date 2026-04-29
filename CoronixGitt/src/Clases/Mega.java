package Clases;

/**
 * Mega — tipo cadena (String) del lenguaje Coronix.
 */
public class Mega {
    private String valor;

    public Mega(String valor) { this.valor = valor; }

    public String getValor()         { return valor; }
    public void   setValor(String v) { this.valor = v; }

    /** Concatenación con @ */
    public Mega concatenar(Mega o) { return new Mega(valor + o.valor); }

    @Override public String toString() { return valor; }
}